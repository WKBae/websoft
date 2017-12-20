package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.FolderPermissionModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet(name = "SharedFolderServlet", urlPatterns = "/shared/folder/*")
public class SharedFolderServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  //폴더 복사, 이동
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";
			String[] splitPath = path.split("/");

			int rootId = Integer.parseInt(splitPath[0]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
			if(folderPermission == null) {  // 사용자가 rootFolder에 권한이 있는지 확인
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderModel target = baseFolder.getFolder(splitPath[splitPath.length - 1]);
			String to = request.getParameter("to");
			String type = request.getParameter("type");//move, copy
			String[] splitTo = to.split("/");
			FolderModel toFolder = rootFolder.transverse(Arrays.copyOf(splitTo, splitTo.length - 1));
			if(toFolder == null){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if("copy".equals(type)){
				target.clone(toFolder);
			}
			else if("move".equals(type)){
				target.setParent(toFolder);
			}
			else{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
		}catch (SQLException e){
			throw new IOException(e);
		}
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {  //폴더 삭제
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");

			if (user == null) {
				response.sendRedirect("/login");
			} else {
				String path = request.getPathInfo();
				String[] splitPath = path.split("/");

				int rootId = Integer.parseInt(splitPath[0]);
				FolderModel rootFolder = FolderModel.get(rootId);
				if(rootFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}
				FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
				if(folderPermission == null) {  // 사용자가 rootFolder에 권한이 있는지 확인
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
				}

				FolderModel deletedFolder = rootFolder.transverse(splitPath);
				if (deletedFolder == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				deletedFolder.delete();

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
