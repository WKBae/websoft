package com.dbdbdeep.websoft.servlet;

import com.dbdbdeep.websoft.models.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "SignServlet", urlPatterns = "/sign")
public class SignServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("WEB-INF/jsp/sign.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("sign", "정보를 입력해주세요.");
            String signuser = request.getParameter("username");
            String signpassword = request.getParameter("password");
            String signname = request.getParameter("name");
            String signemail = request.getParameter("email");
            boolean signadmin = false;

            //아이디 갯수 제한
            if (signuser == null || signuser.length() <= 3) {
                request.setAttribute("message", "아이디를 4글자 이상 입력해주세요");
                request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response); //오류났을때 오류명령어와 함께 돌아옴
                return;
            } else {
                //아이디 중복 제한
                if (UserModel.getUser(signuser) != null) {
                    request.setAttribute("message", "중복된 아이디가 있습니다.");
                    request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response); //오류났을때 오류명령어와 함께 돌아옴
                    return;
                }
            }

            //비밀번호 6자리 이상
            if (signpassword == null || signpassword.length() < 6) {
                request.setAttribute("message", "비밀번호는 6자리 이상 입력해주세요");
                request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response); //오류발생시 오류명령어와 함께 돌아옴
                return;
            }

            //이름 입력 되었는지
            if (signname == null || signname.length() == 0) {
                request.setAttribute("message", "이름을 입력해주세요");
                request.getRequestDispatcher("WEB-INF/jsp/login.jsp").forward(request, response); //오류났을때 오류명령어와 함께 돌아옴
                return;
            }

            //이메일이 비어있으면 null로 바꾸어줌
            if (signemail != null && signemail.length() == 0) {
                signemail = null;
            }

            UserModel.create(signuser, signpassword, signname, signemail, signadmin); //회원가입 정보를 저장

            response.sendRedirect("/login"); //login 주소로 넘어감

        } catch (SQLException e) {
            throw new IOException(e);
        }

    }

}
