package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.UserModel;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "AdminServlet", urlPatterns = "/admin")
public class AdminServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String[] choice = request.getParameterValues("adminIds");
			for (String strId : choice) {
				int id = Integer.parseInt(strId); //스트링형태의 strId를 인티저로 바꾸어준다.
				UserModel.get(id);
				UserModel.get(id).setAdmin(Boolean.TRUE);   //체크박스를 체크하면 admin이 트루로 변하여 관리자로 설정.
			}
		} catch(SQLException e) {
			throw new IOException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			UserModel[] adusers = UserModel.getUsers(0,100); //총 100명까지 회원 배열에 입력
			request.setAttribute("users", adusers); //회원리스트 출력은 프론트에서 해결
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
