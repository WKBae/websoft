package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FilePermissionModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.security.sasl.SaslException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "FilePermissionServlet", urlPatterns = "/permission/file/*")
public class FilePermissionServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null) {
				response.sendRedirect("/files/");
				return;
			}

			FolderModel rootFolder = FolderModel.getRoot(user);

			String[] splitPath = path.split("/");
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FileModel file = baseFolder.getFile(splitPath[splitPath.length - 1]);
			FilePermissionModel filePermission = FilePermissionModel.get(file, user);

			if(filePermission == null){
				filePermission = FilePermissionModel.create(file, user, false, false);
			}
			String readable = request.getParameter("readable");
			Boolean isReadable = false;
			if("Y".equals(readable)){
				isReadable = true;
			}
			else if("N".equals(readable)){
				isReadable = false;
			}
			else{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			String permittable = request.getParameter("permittable");
			Boolean isPermittable = false;
			if("Y".equals(permittable)){
				isPermittable = true;
			}
			else if("N".equals(permittable)){
				isPermittable = false;
			}
			else{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if(!isReadable && !isPermittable){
				filePermission.delete();
			}
			else {
				filePermission.setReadable(isReadable);
				filePermission.setPermittable(isPermittable);
			}

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}catch (SQLException e){
			throw new IOException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}
}
