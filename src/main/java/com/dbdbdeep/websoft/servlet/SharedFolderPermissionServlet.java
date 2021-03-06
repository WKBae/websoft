package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.database.Database;
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

@WebServlet(name = "SharedFolderPermissionServlet", urlPatterns = "/permission/shared/folder/*")
public class SharedFolderPermissionServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			String path = request.getPathInfo();
			if (path == null) {
				response.sendRedirect("/shared/");
				return;
			}
			String[] splitPath = path.split("/");
			int rootId = Integer.parseInt(splitPath[1]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderPermissionModel folderPermission = FolderPermissionModel.get(baseFolder, user);
			if(folderPermission == null){
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			if(folderPermission.isPermittable() == true) {
				String readable = request.getParameter("readable");
				boolean isReadable = false;
				if ("Y".equals(readable)) {
					isReadable = true;
				} else if ("N".equals(readable)) {
					isReadable = false;
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				String writable = request.getParameter("writable");
				boolean isWritable = false;
				if ("Y".equals(writable)) {
					isWritable = true;
				} else if ("N".equals(writable)) {
					isWritable = false;
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				String permittable = request.getParameter("permittable");
				boolean isPermittable = false;
				if ("Y".equals(permittable)) {
					isPermittable = true;
				} else if ("N".equals(permittable)) {
					isPermittable = false;
				} else {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				if(isPermittable && (!isReadable || !isWritable)){
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				FolderModel folder = baseFolder.getFolder(splitPath[splitPath.length - 1]);
				UserModel permittee = UserModel.getUser(request.getParameter("permittee"));

				Database db = Database.getDatabase();
				db.beginTransaction();
				propagatePermission(folder, permittee, isReadable, isWritable, isPermittable);
				db.endTransaction();

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}catch (SQLException e){
			throw new IOException(e);
		}
	}

	private void propagatePermission(FolderModel folder, UserModel permittee, boolean isReadable, boolean isWritable, boolean isPermittable) throws SQLException {
		FolderPermissionModel folderPermission = FolderPermissionModel.get(folder, permittee);
		if(!isReadable && !isWritable &&!isPermittable){
			if(folderPermission != null){
				folderPermission.delete();
			}
		}
		else {
			if(folderPermission == null){
				FolderPermissionModel.create(folder, permittee, isReadable, isWritable, isPermittable);
			}
			else {
				folderPermission.setReadable(isReadable);
				folderPermission.setWritable(isWritable);
				folderPermission.setPermittable(isPermittable);
			}
		}

		for (FolderModel child : folder.getFolders()) {
			propagatePermission(child, permittee, isReadable, isWritable, isPermittable);
		}
	}
}
