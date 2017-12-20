package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
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
import java.util.Arrays;

@WebServlet(name = "FolderServlet", urlPatterns = "/folder/*")
public class FolderServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
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

			FolderModel target = baseFolder.getFolder(splitPath[splitPath.length - 1]);
			String to = request.getParameter("to");
			String type = request.getParameter("type");//move, copy
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";

			FolderModel rootFolder = FolderModel.getRoot(user);

			String[] splitPath = path.split("/");
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			JSONArray folderList = new JSONArray();
			JSONArray fileList = new JSONArray();

			for (FolderModel folder : baseFolder.getFolders()) {
				folderList.add(folder.getName());
			}
			for (FileModel file : baseFolder.getFiles()) {
				fileList.add(file.getName());
			}

			JSONObject obj = new JSONObject();
			obj.put("files", fileList);
			obj.put("folders", folderList);

			response.setContentType("application/json");
			PrintWriter writer = response.getWriter();
			writer.print(obj);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null) path = "/";

			FolderModel rootFolder = FolderModel.getRoot(user);

			String[] splitPath = path.split("/");
			FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String folderName = splitPath[splitPath.length - 1];

			if (baseFolder.getFolder(folderName) == null) {
				FolderModel.create(baseFolder, folderName, user);
			}

			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader("Content-Location", "/file" + path + (path.endsWith("/") ? "" : "/") + folderName + "/");
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

				FolderModel rootFolder = FolderModel.getRoot(user);
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
