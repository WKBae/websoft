<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:bootstrap title="로그인 테스트">
    <jsp:body>
        <div class="container">
            <div class="row">
                <div class="col">
                    <form action="<c:url value="/login"/>" method="POST" id="login-form" novalidate>
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">
                                아이디 또는 비밀번호가 틀렸습니다. 다시 입력해주세요.
                            </div>
                        </c:if>
                        <div class="form-group">
                            <label for="username">아이디</label>
                            <input type="text" class="form-control" name="username" id="username" placeholder="Username"
                                   required value="${id}">
                        </div>
                        <div class="form-group">
                            <label for="password">비밀번호</label>
                            <input type="password" class="form-control" name="password" id="password"
                                   placeholder="Password" required>
                        </div>
                        <button type="submit" class="btn btn-primary">로그인</button>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
