package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.FileModel;
import com.dbdbdeep.websoft.models.FolderModel;
import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

@WebServlet(name="DownloadServlet", urlPatterns="/download")
public class DownloadServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, SQLException {
        UserModel user = (UserModel) request.getSession(true).getAttribute("user");

        if (user == null) {
            response.sendRedirect("/login");
        }
        else {
            try {
                String path = request.getPathInfo();
                String[] splitPath = path.split("/");
                String filename = splitPath[splitPath.length - 1];

                FolderModel rootFolder = FolderModel.getRoot(user);
                FolderModel baseFolder = rootFolder.transverse(Arrays.copyOf(splitPath, splitPath.length - 1));

                if(baseFolder == null){
                    response.sendRedirect("/files");
                    return;
                }
                FileModel downloadFile = FileModel.getFile(baseFolder, filename);
                InputStream is = downloadFile.getContents().getBinaryStream();
                OutputStream os = response.getOutputStream();

                int size = blob.getBufferSize();
                byte[] buffer = new byte[size];
                int length = -1;

                while((length=is.read(buffer))!=-1){
                    os.write(buffer,0,length);
                }

                os.flush();
                os.close();
                is.close();
            }catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

    }
}
