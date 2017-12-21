package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.*;

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

@WebServlet(name = "SharedFileServlet", urlPatterns = "/shared/file/*")
public class SharedFileServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  //파일 복사, 이동
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";
			String[] splitPath = path.split("/");

			int rootId = Integer.parseInt(splitPath[1]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
			if(folderPermission == null) {  // 사용자가 rootFolder에 권한이 있는지 확인
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

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

			FolderPermissionModel targetPermission = FolderPermissionModel.get(baseFolder, user);
			FolderPermissionModel toFolderPermission = FolderPermissionModel.get(toFolder, user);
			if(targetPermission == null || toFolderPermission == null) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			if("copy".equals(type)){
				if(targetPermission.isReadable() && toFolderPermission.isWritable()) {
					if (to.endsWith("/")) {
						target.clone(toFolder);
					} else {
						target.clone(toFolder, splitTo[splitTo.length - 1]);
					}
				} else {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
			}
			else if("move".equals(type)){
				if(targetPermission.isWritable() && toFolderPermission.isWritable()) {
					if (to.endsWith("/")) {
						target.setParent(toFolder);
					} else {
						target.move(toFolder, splitTo[splitTo.length - 1]);
					}
				} else {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");

			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			} else {
				String path = request.getPathInfo();
				String[] splitPath = path.split("/");
				String filename = splitPath[splitPath.length - 1];

				int rootId = Integer.parseInt(splitPath[1]);
				FolderModel rootFolder = FolderModel.get(rootId);
				if(rootFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				FolderModel baseFolder;
				if(splitPath.length == 2) {
					baseFolder = rootFolder;
				} else {
					baseFolder = rootFolder.getFolder(splitPath[2]);
					baseFolder = baseFolder.transverse(Arrays.copyOfRange(splitPath, 2, splitPath.length - 1));
				}
				if (baseFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				FileModel downloadFile = baseFolder.getFile(filename);
				if (downloadFile == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				FolderPermissionModel baseFolderPermission = FolderPermissionModel.get(baseFolder, user);
				if(baseFolderPermission == null || !baseFolderPermission.isReadable()) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
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

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  //파일 삭제
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");

			if (user == null) {
				response.sendRedirect("/login");
			} else {
				String path = request.getPathInfo();
				String[] splitPath = path.split("/");
				String filename = splitPath[splitPath.length - 1];

				int rootId = Integer.parseInt(splitPath[1]);
				FolderModel rootFolder = FolderModel.get(rootId);
				if(rootFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
				FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
				if(folderPermission == null) {  // 사용자가 rootFolder에 권한이 있는지 확인
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}

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
