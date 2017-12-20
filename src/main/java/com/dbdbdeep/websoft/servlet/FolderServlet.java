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

@WebServlet(name = "FolderServlet", urlPatterns = "/folder/*")
public class FolderServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String folderName = request.getParameter("folderName");

			if (baseFolder.getFolder(folderName) == null) {
				FolderModel.create(baseFolder, folderName, user);
			}

			response.setStatus(HttpServletResponse.SC_CREATED);
			response.setHeader("Content-Location", "/files" + path + (path.endsWith("/") ? "" : "/") + folderName + "/");
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