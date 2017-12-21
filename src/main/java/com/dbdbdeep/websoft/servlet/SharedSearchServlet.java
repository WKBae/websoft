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
import java.util.*;

@WebServlet(name = "SharedSearchServlet", urlPatterns = "/shared/search/*")
public class SharedSearchServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if (user == null) {
				response.sendRedirect("/login");
				return;
			}

			String keyword = request.getParameter("keyword");
			String path = request.getPathInfo();
			if (path == null) {
				//response.sendRedirect(response.encodeRedirectURL("shared/search/?keyword=" + keyword));
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			if (keyword == null || keyword.length() == 0) {
				response.sendRedirect(response.encodeRedirectURL("/shared/files" + path));
				return;
			}

			String[] splitPath = path.split("/");
			int rootId = Integer.parseInt(splitPath[1]);
			FolderModel rootFolder = FolderModel.get(rootId);
			if(rootFolder == null) {
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
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			HashMap<String, FolderModel> folders = new HashMap<>();
			HashMap<String, FileModel> files = new HashMap<>();

			Queue<FolderModel> folderQueue = new LinkedList<>();
			HashMap<FolderModel, String> paths = new HashMap<>();
			folderQueue.add(baseFolder);
			paths.put(baseFolder, path);

			FolderModel folder;
			while ((folder = folderQueue.poll()) != null) {
				FolderModel parent = folder.getParent();
				String currentPath;
				if(parent == null) {
					currentPath = "";
				} else {
					currentPath = paths.get(parent) + "/" + folder.getName();
				}
				paths.put(folder, currentPath);

				for(FolderModel found : folder.searchFolders(keyword)) {
					folders.put(currentPath + "/" + found.getName() + "/", found);
				}
				for(FileModel found : folder.searchFiles(keyword)) {
					files.put(currentPath + "/" + found.getName(), found);
				}
				Collections.addAll(folderQueue, folder.getFolders());
			}

			request.setAttribute("files", files);
			request.setAttribute("folders", folders);
			request.getRequestDispatcher("/WEB-INF/jsp/shared_search.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
