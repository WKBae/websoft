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
		int i;

		try{
			UserModel[] adusers = UserModel.getUsers(0,100); //총 100명까지 회원 배열에 입력
			request.setAttribute("users", adusers);
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("WEB-INF/jsp/admin.jsp").forward(request, response);
	}

}
