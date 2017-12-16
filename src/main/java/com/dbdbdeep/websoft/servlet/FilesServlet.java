package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name="FilesServlet", urlPatterns="/files")
public class FilesServlet extends HttpServlet{
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel user = (UserModel)request.getSession(true).getAttribute("user");

        if(user == null) {
            response.sendRedirect("/login");
        }
        else{
            try {
                String path = request.getPathInfo();
                String[] splitpath = path.split("/");
                FolderModel rootFolder = FolderModel.getRoot(user);
                FolderModel baseFolder = rootFolder.transverse(splitpath);

                if(baseFolder == null){
                    response.sendRedirect("/files");
                    return;
                }

                FolderModel[] folders = baseFolder.getFolders();
                FileModel[] files = baseFolder.getFiles();

                request.setAttribute("path", path);
                request.setAttribute("folders", folders);
                request.setAttribute("files", files);
                request.getRequestDispatcher("WEB-INF/jsp/files.jsp").forward(request, response);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
