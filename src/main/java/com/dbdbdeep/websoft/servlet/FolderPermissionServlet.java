package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.database.Database;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.FolderPermissionModel;
import com.dbdbdeep.websoft.models.UserModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "FolderPermissionServlet" , urlPatterns = "/permission/folder/*")
public class FolderPermissionServlet extends HttpServlet {
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
			String writable = request.getParameter("writable");
			boolean isWritable = false;
			if("Y".equals(writable)){
				isWritable = true;
			}
			else if("N".equals(writable)){
				isWritable = false;
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
			else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if(isPermittable && !isReadable){
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			FolderModel folder = baseFolder;
			UserModel permittee = UserModel.getUser(request.getParameter("permittee"));

			Database db = Database.getDatabase();
			db.beginTransaction();
			propagatePermission(folder, permittee, isReadable, isWritable, isPermittable);
			db.endTransaction();

			response.setStatus(HttpServletResponse.SC_NO_CONTENT);
		}catch (SQLException e){
			throw new IOException(e);
		}
	}

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

			FolderModel rootFolder = FolderModel.getRoot(user);
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderPermissionModel[] folderPermissions = FolderPermissionModel.findPermissions(baseFolder);

			JSONArray permissionList = new JSONArray();

			for(FolderPermissionModel folderPermission : folderPermissions){
				JSONObject obj = new JSONObject();
				obj.put("user", folderPermission.getUser().getUsername());
				obj.put("readable", folderPermission.isReadable());
				obj.put("writable", folderPermission.isWritable());
				obj.put("permittable", folderPermission.isPermittable());
				permissionList.add(obj);
			}

			response.setContentType("application/json");
			PrintWriter writer = response.getWriter();
			writer.print(permissionList);

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
