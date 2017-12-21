package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
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
import java.util.HashMap;

@WebServlet(name = "SharedFilesServlet", urlPatterns = "/shared/files/*")
public class SharedFilesServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String path = request.getPathInfo();
			if (path == null || "/".equals(path)) {
				response.sendRedirect("/shared/");
				return;
			}

			String[] splitPath = path.split("/");
			int rootId = Integer.parseInt(splitPath[1]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			FolderPermissionModel folderPermission = FolderPermissionModel.get(rootFolder, user);
			if(folderPermission == null) {  // 사용자가 rootFolder에 권한이 있는지 확인
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			FolderModel baseFolder;
			if(splitPath.length == 2) {
				baseFolder = rootFolder;
			} else {
				baseFolder = rootFolder.getFolder(splitPath[2]);
				baseFolder = baseFolder.transverse(Arrays.copyOfRange(splitPath, 2, splitPath.length));
			}
			if (baseFolder == null) {
				response.sendRedirect("/shared/");
				return;
			}

			FileModel[] files = baseFolder.getFiles();
			FolderModel[] folders = baseFolder.getFolders();

			HashMap<String, FolderModel> folderMap = new HashMap<>();
			for(FolderModel folder : folders) {
				folderMap.put(path + folder.getName() + "/", folder);
			}
			HashMap<String, FileModel> fileMap = new HashMap<>();
			for(FileModel file : files) {
				fileMap.put(path + file.getName(), file);
			}

			request.setAttribute("path", path);
			request.setAttribute("files", fileMap);
			request.setAttribute("folders", folderMap);
			request.getRequestDispatcher("/WEB-INF/jsp/shared_files.jsp").forward(request, response);
		} catch(SQLException e) {
			throw new IOException(e);
		}
	}
}
