package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;
import org.json.simple.JSONArray;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet(name = "FolderListServlet", urlPatterns = "/folderlist/*")
public class FolderListServlet extends HttpServlet {
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

			for (FolderModel folder : baseFolder.getFolders()) {
				folderList.add(folder.getName());
			}

			response.setContentType("application/json");
			PrintWriter writer = response.getWriter();
			writer.print(folderList);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
