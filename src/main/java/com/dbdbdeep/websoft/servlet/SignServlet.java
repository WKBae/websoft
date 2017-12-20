package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;

@WebServlet(name = "SignServlet", urlPatterns = "/sign")
public class SignServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserModel user = (UserModel) request.getSession(true).getAttribute("user");
		if (user != null) { //로그인이 돼있으면 바로 files 페이지로 넘어감
			response.sendRedirect("/files/");
			return;
		}

		request.getRequestDispatcher("WEB-INF/jsp/sign.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

			String signuser = request.getParameter("username");
			String signpassword = request.getParameter("password");
			String signname = request.getParameter("name");
			String signemail = request.getParameter("email");
			boolean signadmin = false;

			HashSet<String> errors = null;

			//아이디 갯수 제한
			if (signuser == null || signuser.length() <= 3) {
				if (errors == null) errors = new HashSet<>();
				errors.add("ID_TOO_SHORT");
			} else {
				//아이디 중복 제한
				if (UserModel.getUser(signuser) != null) {
					if (errors == null) errors = new HashSet<>();
					errors.add("ID_DUPLICATE");
				}
			}

			//비밀번호 6자리 이상
			if (signpassword == null || signpassword.length() < 6) {
				if (errors == null) errors = new HashSet<>();
				errors.add("PASSWORD_TOO_SHORT");
			}

			//이름 입력 되었는지
			if (signname == null || signname.length() == 0) {
				if (errors == null) errors = new HashSet<>();
				errors.add("NAME_EMPTY");
			}

			//이메일이 비어있으면 null로 바꾸어줌
			if (signemail != null && signemail.length() == 0) {
				signemail = null;
			}

			if (errors != null) {
				request.setAttribute("errors", errors);
				request.setAttribute("id", signuser);
				request.setAttribute("name", signname);
				request.setAttribute("email", signemail);
				request.getRequestDispatcher("/WEB-INF/jsp/sign.jsp").forward(request, response); //오류났을때 오류명령어와 함께 돌아옴
				return;
			}

			UserModel.create(signuser, signpassword, signname, signemail, signadmin); //회원가입 정보를 저장

			response.sendRedirect("/login"); //login 주소로 넘어감
		} catch (SQLException e) {
			throw new IOException(e);
		}

	}

}
