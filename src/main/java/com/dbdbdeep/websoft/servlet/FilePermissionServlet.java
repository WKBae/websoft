package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.security.sasl.SaslException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet(name = "FilePermissionServlet", urlPatterns = "/permission/file/*")
public class FilePermissionServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			String[] splitPath = path.split("/");
			String filename = splitPath[splitPath.length - 1];

			FolderModel rootFolder = FolderModel.getRoot(user);
			FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length-1));
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			FileModel file = baseFolder.getFile(filename);
			FilePermissionModel[] filePermissions = FilePermissionModel.findPermissions(file);

			JSONArray permissionList = new JSONArray();

			for(FilePermissionModel filePermission : filePermissions){
				JSONObject obj = new JSONObject();
				obj.put("user", filePermission.getUser().getUsername());
				obj.put("readable", filePermission.isReadable());
				obj.put("permittable", filePermission.isPermittable());
				permissionList.add(obj);
			}

			response.setContentType("application/json");
			PrintWriter writer = response.getWriter();
			writer.print(permissionList);

		}catch (SQLException e){
			throw new IOException(e);
		}
	}
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
			String[] splitPath = path.split("/");
			FolderModel rootFolder = FolderModel.getRoot(user);
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			String readable = request.getParameter("readable");
			boolean isReadable = false;

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
			boolean isPermittable = false;
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

			if(isPermittable && !isReadable){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			FileModel file = baseFolder.getFile(splitPath[splitPath.length - 1]);
			UserModel permittee = UserModel.getUser(request.getParameter("permittee"));
			FilePermissionModel filePermission = FilePermissionModel.get(file, permittee);

			if(!isReadable && !isPermittable){
				if(filePermission != null) {
					filePermission.delete();
				}
			}
			else {
				if(filePermission == null){
					FilePermissionModel.create(file, permittee, isReadable, isPermittable);
				}
				else {
					filePermission.setReadable(isReadable);
					filePermission.setPermittable(isPermittable);
				}
			}

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}catch (SQLException e){
			throw new IOException(e);
		}
	}
}
