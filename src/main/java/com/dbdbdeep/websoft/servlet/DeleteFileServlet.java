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
import java.util.Arrays;

@WebServlet(name = "DeleteFileServlet", urlPatterns = "/deletefile/*")
public class DeleteFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserModel user = (UserModel) request.getSession(true).getAttribute("user");

            if (user == null) {
                response.sendRedirect("/login");
            } else {
                String path = request.getPathInfo();
                String[] splitPath = path.split("/");
                String filename = splitPath[splitPath.length - 1];

                FolderModel rootFolder = FolderModel.getRoot(user);
                FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));
                if (baseFolder == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                FileModel deletedFile = baseFolder.getFile(filename);
                if (deletedFile == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                deletedFile.delete();
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (SQLException e){
                throw new IOException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
