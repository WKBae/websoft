package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "SharedFolderServlet", urlPatterns = "/sharedfolder/*")
public class SharedFolderServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserModel user = (UserModel) request.getSession(true).getAttribute("user");
            if (user == null) {
                response.sendRedirect("/login");
                return;
            }

            FolderPermissionModel[] folders = FolderPermissionModel.findPermissions(user);  //사용자에게 권한이 있는 모든 폴더
            FilePermissionModel[] files = FilePermissionModel.findPermissions(user);  //사용자에게 권한이 있는 모든 파일

            //  폴더의 최상위 폴더 찾기
            ArrayList<FolderModel> rootFolders = new ArrayList<>();  //최상위 폴더들만 갖고있는 변수
            boolean find = false;
            for(int i = 0; i < folders.length; i++) {
                FolderModel folder = folders[i].getFolder();
                while(find == false) {
                    FolderModel parentFolder = folder.getParent();  //folder의 부모폴더 가져오기
                    if(parentFolder == null) {
                        if (!rootFolders.contains(folder)) {  //rootFolders에 찾은 checkFolder가 없을 경우에 추가
                            rootFolders.add(folder);
                            find = true;
                        }
                    }
                    else {
                        FolderPermissionModel checkFolder = FolderPermissionModel.get(parentFolder, user);
                        if (checkFolder == null) {  //부모 폴더에 사용자가 권한이 없을 때
                            if (!rootFolders.contains(folder)) {  //rootFolders에 찾은 checkFolder가 없을 경우에 추가
                                rootFolders.add(folder);
                                find = true;
                            }
                        }
                    }
                    folder = parentFolder;
                }
            }

            ArrayList<FileModel> rootFiles = new ArrayList<>();  //최상위 파일들만 갖고있는 변수
            for(int i = 0; i < files.length; i++) {
                rootFiles.add(files[i].getFile());
            }

            request.setAttribute("folders", rootFolders);
            request.setAttribute("files", rootFiles);
            request.getRequestDispatcher("/WEB-INF/jsp/shared_folder.jsp").forward(request, response);
        } catch(SQLException e) {
            throw new IOException(e);
        }
    }
}
