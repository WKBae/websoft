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

			FolderPermissionModel[] folders = FolderPermissionModel.findPermissions(user);  //사용자에게 권한이 있는 모든 폴더
			FilePermissionModel[] files =  FilePermissionModel.findPermissions(user);  //사용자에게 권한이 있는 모든 파일

			ArrayList<FolderModel> ownFolders = new ArrayList<>();
			for(int i = 0; i < folders.length; i++) {  //사용자가 공유한 폴더 찾기
				FolderModel folder = folders[i].getFolder();
				if(folder.getOwner().equals(user)) {
					ownFolders.add(folder);
				}
			}

			//  폴더의 최상위 폴더 찾기
			HashSet<FolderModel> rootFolders = new HashSet<>();  //최상위 폴더들만 갖고있는 변수
			for (int i = 0; i < ownFolders.size(); i++) {
				FolderModel folder = ownFolders.get(i);
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

			ArrayList<FileModel> ownFiles = new ArrayList<>();
			for(int i = 0; i < files.length; i++) {  //사용자가 공유한 파일 찾기
				FileModel file = files[i].getFile();
				if(file.getOwner().equals(user)) {
					ownFiles.add(file);
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
			for(FileModel file : ownFiles) {
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
