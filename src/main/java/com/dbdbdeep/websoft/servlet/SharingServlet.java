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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

@WebServlet(name = "SharingServlet", urlPatterns = "/sharing/*")
public class SharingServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = (UserModel) request.getSession(true).getAttribute("user");
			if(user == null) {
				response.sendRedirect("/login");
				return;
			}

			FolderModel[] sharingFolders = FolderModel.getSharing(user);
			FileModel[] sharingFiles = FileModel.getSharing(user);

			//  폴더의 최상위 폴더 찾기
			HashSet<FolderModel> rootFolders = new HashSet<>();  //최상위 폴더들만 갖고있는 변수
			for (int i = 0; i < sharingFolders.length; i++) {
				FolderModel folder = sharingFolders[i];
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

			String path = request.getPathInfo();
			HashMap<String, FolderModel> folderMap = new HashMap<>();
			for(FolderModel folder : rootFolders) {
				LinkedList<String> pathList = new LinkedList<>();
				for(FolderModel current = folder; current != null; current = current.getParent()) {
					pathList.addFirst(current.getName());
				}
				StringBuilder sb = new StringBuilder();
				sb.append('/');
				for(String folderName : pathList) {
					sb.append(folderName);
					sb.append('/');
				}
				folderMap.put(sb.toString(), folder);
			}

			HashMap<String, FileModel> fileMap = new HashMap<>();
			for(FileModel file : sharingFiles) {
				LinkedList<String> pathList = new LinkedList<>();
				for(FolderModel current = file.getParent(); current != null; current = current.getParent()) {
					pathList.addFirst(current.getName());
				}
				StringBuilder sb = new StringBuilder();
				sb.append('/');
				for(String folderName : pathList) {
					sb.append(folderName);
					sb.append('/');
				}
				sb.append(file.getName());
				fileMap.put(sb.toString(), file);
			}

			request.setAttribute("path", path);
			request.setAttribute("folders", folderMap);
			request.setAttribute("files", fileMap);
			request.getRequestDispatcher("/WEB-INF/jsp/sharing.jsp").forward(request, response);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
