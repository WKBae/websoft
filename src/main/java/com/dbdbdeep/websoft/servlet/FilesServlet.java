package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

@WebServlet(name = "FilesServlet", urlPatterns = "/files/*")
public class FilesServlet extends HttpServlet {
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

			FolderModel rootFolder = FolderModel.getRoot(user);

			String[] splitPath = path.split("/");
			FolderModel baseFolder = rootFolder.transverse(splitPath);
			if (baseFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderModel[] folders = baseFolder.getFolders();
			FileModel[] files = baseFolder.getFiles();

			StringBuilder sb = new StringBuilder();
			sb.append('/');
			for(String spl : splitPath) {
				sb.append(spl);
				sb.append('/');
			}
			String currentPath = sb.toString();
			HashMap<String, FolderModel> folderMap = new HashMap<>();
			for(FolderModel folder : folders) {
				folderMap.put(currentPath + folder.getName() + "/", folder);
			}
			HashMap<String, FileModel> fileMap = new HashMap<>();
			for(FileModel file : files) {
				fileMap.put(currentPath + file.getName(), file);
			}

			request.setAttribute("path", path);
			request.setAttribute("folders", folderMap);
			request.setAttribute("files", fileMap);
			request.getRequestDispatcher("/WEB-INF/jsp/files.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
