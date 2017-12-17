package com.dbdbdeep.websoft.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LogoutServlet")
public class LogoutServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(); //현재 세션 리턴

        String resUrl = null;

        if(session.getAttribute("user") != null){
            session.invalidate(); //세션 무효화
            resUrl = "/login";
        } else{
            request.setAttribute("message", "PlEASE_LOGIN");
            resUrl = "/login";
        }
        RequestDispatcher rdp = request.getRequestDispatcher(resUrl);
        rdp.forward(request, response);
    }
}
