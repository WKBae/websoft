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
import java.util.HashMap;
import java.util.HashSet;

@WebServlet(name = "SharedServlet", urlPatterns = "/shared/*")
public class SharedServlet extends HttpServlet {
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
			HashSet<FolderModel> rootFolders = new HashSet<>();  //최상위 폴더들만 갖고있는 변수

			for (int i = 0; i < folders.length; i++) {
				FolderModel folder = folders[i].getFolder();
				boolean topmostFound = false;
				while (!topmostFound) {
					FolderModel parentFolder = folder.getParent();  //folder의 부모폴더 가져오기
					if (parentFolder == null) {
						rootFolders.add(folder);
						topmostFound = true;
					} else {
						FolderPermissionModel checkFolder = FolderPermissionModel.get(parentFolder, user);
						if (checkFolder == null) {  //부모 폴더에 사용자가 권한이 없을 때
							rootFolders.add(folder);
							topmostFound = true;
						}
					}
					folder = parentFolder;
				}
			}

			ArrayList<FileModel> rootFiles = new ArrayList<>();  //최상위 파일들만 갖고있는 변수
			for (int i = 0; i < files.length; i++) {
				rootFiles.add(files[i].getFile());
			}

			String path = request.getPathInfo();
			HashMap<String, FolderModel> folderMap = new HashMap<>();
			for(FolderModel folder : rootFolders) {
				folderMap.put("/" + folder.getId() + "/", folder);
			}

			HashMap<String, FileModel> fileMap = new HashMap<>();
			for(FileModel file : rootFiles) {
				fileMap.put("/" + file.getId() + "/", file);
			}

			request.setAttribute("path", path);
			request.setAttribute("folders", folderMap);
			request.setAttribute("files", fileMap);
			request.getRequestDispatcher("/WEB-INF/jsp/shared_folder.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
