package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "SharedFilePermissionServlet", urlPatterns = "/permission/shared/file/*")
public class SharedFilePermissionServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null) {
				response.sendRedirect("/shared/");
				return;
			}
			String[] splitPath = path.split("/");

			int rootId = Integer.parseInt(splitPath[0]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
			if(folderPermission == null){
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}

			if(folderPermission.isPermittable() == true) {
				FileModel file = baseFolder.getFile(splitPath[splitPath.length - 1]);
				FilePermissionModel filePermission = FilePermissionModel.get(file, user);
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

				if (!isReadable && !isPermittable) {
					if (filePermission != null) {
						filePermission.delete();
					}
				} else {
					if (filePermission == null) {
						FilePermissionModel.create(file, user, isReadable, isPermittable);
					} else {
						filePermission.setReadable(isReadable);
						filePermission.setPermittable(isPermittable);
					}
				}

				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			} else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
		}catch (SQLException e){
			throw new IOException(e);
		}
	}
}
