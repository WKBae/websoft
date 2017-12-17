package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet(name = "DownloadServlet", urlPatterns = "/download/*")
public class DownloadServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");

			if (user == null) {
				response.sendRedirect("/login");
			} else {
				String path = request.getPathInfo();
				String[] splitPath = path.split("/");
				String filename = splitPath[splitPath.length - 1];

				FolderModel rootFolder = FolderModel.getRoot(user);
				FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));
				if (baseFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				FileModel downloadFile = baseFolder.getFile(filename);
				if (downloadFile == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				downloadFile.getContent(new FileModel.ContentReader() {
					@Override
					public void onFileBlobReady(Blob blob) throws IOException, SQLException {
						response.setContentType(MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename));
						try (InputStream is = blob.getBinaryStream();
						     OutputStream os = response.getOutputStream()) {
							byte[] buffer = new byte[2048];
							int length;
							while ((length = is.read(buffer)) != -1) {
								os.write(buffer, 0, length);
							}
						}
					}

					@Override
					public void onFileDoesNotExist() throws IOException {
						response.sendError(HttpServletResponse.SC_NOT_FOUND);
					}
				});
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
