<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:bootstrap title="WebSoft :: 회원가입">
    <jsp:attribute name="script">

    </jsp:attribute>
    <jsp:body>
        <div class="container">
            <div class="row">
                <div class="col">
                    <form action="<c:url value="/sign"/>" method="POST">
                        <c:if test="${not empty message}"><div class="alert alert-danger">${message}</div></c:if>
                        <div class="form-group">
                            <label for="username">아이디</label>
                            <input type="text" class="form-control" name="username" id="username" placeholder="Username">
                        </div>
                        <div class="form-group">
                            <label for="password">비밀번호</label>
                            <input type="password" class="form-control" name="password" id="password" placeholder="Password">
                        </div>
                        <div class="form-group">
                            <label for="password-check">비밀번호 확인</label>
                            <input type="password" class="form-control" id="password-check" placeholder="Repeat password">
                        </div>
                        <div class="form-group">
                            <label for="name">이름</label>
                            <input type="text" class="form-control" name="name" id="name" placeholder="Name">
                        </div>
                        <div class="form-group">
                            <label for="email">이메일</label>
                            <input type="email" class="form-control" name="email" id="email" placeholder="Email">
                        </div>
                        <button type="submit" class="btn btn-primary">회원가입</button>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
