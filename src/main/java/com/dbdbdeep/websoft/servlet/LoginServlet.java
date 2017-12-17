package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// ...
		//request.setAttribute("asdf", "content");
		request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response);

		if(request.getAttribute("user") != null){ //로그인이 돼있으면 바로 files 페이지로 넘어감
			response.sendRedirect("/files/");
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			UserModel user = UserModel.getUser(request.getParameter("username")); //아이디 가져오기
			String password = request.getParameter("password"); //패스워드 가져오기

			if (user == null) {
				// 아이디를 제대로 입력해 주세요
				request.setAttribute("error", "ID_INCORRECTED");
			} else { // 아이디는 맞은경우
				if (user.checkPassword(password)) {
					//로그인
					request.getSession(true).setAttribute("user", user);
					response.sendRedirect("/files/"); //files 주소로 넘어감
					return;
				} else {
					// false -> 아이디나 비밀번호가 틀렸습니다.
					request.setAttribute("error", "PASSWORD_INCORRECTED");
				}
			}
			request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response); //오류났을때 오류명령어와 함께 돌아옴
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
