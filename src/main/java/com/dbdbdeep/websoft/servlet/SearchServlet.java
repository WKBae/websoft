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
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

@WebServlet(name = "SearchServlet", urlPatterns = "/search/*")
public class SearchServlet extends HttpServlet {
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
                response.sendRedirect(response.encodeRedirectURL("/search/?keyword=" + keyword));
                return;
            }
            if (keyword == null || keyword.length() == 0) {
                response.sendRedirect(response.encodeRedirectURL("/files" + path));
                return;
            }
            String[] splitPath = path.split("/");

            FolderModel rootFolder = FolderModel.getRoot(user);
            FolderModel baseFolder = rootFolder.transverse(splitPath);
            if (baseFolder == null) {
	            response.sendError(HttpServletResponse.SC_NOT_FOUND);
	            return;
            }

            ArrayList<FolderModel> sFolders = new ArrayList<>();
            ArrayList<FileModel> sFiles = new ArrayList<>();
            Queue<FolderModel> folders = new LinkedList<>();
            folders.add(baseFolder);

            FolderModel folder;
            while ((folder = folders.poll()) != null) {
                Collections.addAll(sFiles, folder.searchFiles(keyword));
                Collections.addAll(sFolders, folder.searchFolders(keyword));
                Collections.addAll(folders, folder.getFolders());
            }

            request.setAttribute("files", sFiles);
            request.setAttribute("folders", sFolders);
            request.getRequestDispatcher("/WEB-INF/jsp/search.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
