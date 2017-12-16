package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;


@WebServlet(name = "UploadServlet", urlPatterns="/upload")
public class UploadServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserModel user = (UserModel) request.getSession(true).getAttribute("user");
        try {
            if (user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String path = request.getPathInfo();
            String[] splitPath = path.split("/");
            FolderModel rootFolder = FolderModel.getRoot(user);
            FolderModel baseFolder = rootFolder.transverse(splitPath);
            if(baseFolder == null){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Part uploaded = request.getPart("file");
            FileModel file = FileModel.create(baseFolder, uploaded.getSubmittedFileName(), user, new Date(), uploaded.getInputStream());

            if(file != null){
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }catch (SQLException e){
            throw new IOException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}