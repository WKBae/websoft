package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;

@WebServlet(name = "FileServlet", urlPatterns = "/file/*")
@MultipartConfig(fileSizeThreshold = 512 * 1024 * 1024, maxFileSize = 4L * 1024 * 1024 * 1024, maxRequestSize = 16L * 1024 * 1024 * 1024)
public class FileServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserModel user = (UserModel) request.getSession(true).getAttribute("user");

		try {
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";
			String[] splitPath = path.split("/");
			FolderModel rootFolder = FolderModel.getRoot(user);
			FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FileModel target = baseFolder.getFile(splitPath[splitPath.length - 1]);
			String to = request.getParameter("to");
			String type = request.getParameter("type");//move, copy

			if(to == null || type == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			String[] splitTo = to.split("/");

			FolderModel toFolder;
			if(to.endsWith("/")){
				toFolder = rootFolder.transverse(splitTo);
			}
			else{
				toFolder = rootFolder.transverse(Arrays.copyOf(splitTo, splitTo.length - 1));
			}

			if(toFolder == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if("copy".equals(type)){
				if(to.endsWith("/")) {
					target.clone(toFolder);
				}
				else{
					target.clone(toFolder, splitTo[splitTo.length - 1]);
				}
			}
			else if("move".equals(type)){
				if(to.endsWith("/")){
					target.setParent(toFolder);
				}
				else {
					target.move(toFolder, splitTo[splitTo.length - 1]);
				}
			}
			else{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			if(to.endsWith("/")) {
				response.setHeader("Content-Location", "/shared/file" + to + target.getName());
			} else {
				response.setHeader("Content-Location", "/shared/file" + to + "/" + splitTo[splitTo.length - 1]);
			}
		}catch (SQLException e){
			throw new IOException(e);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		UserModel user = (UserModel) request.getSession(true).getAttribute("user");
		try {
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";
			String[] splitPath = path.split("/");
			FolderModel rootFolder = FolderModel.getRoot(user);
			FolderModel baseFolder;
			if(path.endsWith("/")) {
				baseFolder = rootFolder.transverse(splitPath);
			} else {
				baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));
			}
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			Part uploaded = request.getPart("file");
			String fileName;
			if(path.endsWith("/")) {
				fileName = Paths.get(uploaded.getSubmittedFileName()).getFileName().toString(); // MSIE fix
			} else {
				fileName = splitPath[splitPath.length - 1];
			}
			FileModel file = FileModel.create(baseFolder, fileName, user, new Date(), uploaded.getInputStream());

			if (file != null) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
				response.setHeader("Content-Location", "/file" + path + (path.endsWith("/") ? "" : "/") + fileName);
			} else {
				throw new IOException("File \"" + fileName + "\" cannot be created.");
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

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

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

				FileModel deletedFile = baseFolder.getFile(filename);
				if (deletedFile == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				deletedFile.delete();
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
