<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<t:bootstrap title="로그인 테스트">
    <jsp:attribute name="script">

    </jsp:attribute>
    <jsp:body>
        <div class="container">
            <div class="row">
                <div class="col">
                    <form action="<c:url value="/login"/>" method="POST">
                        <c:if test="${not empty error}">
                            <div class="alert alert-danger alert-dismissible fade show">
                                ${error}
                                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                    <span aria-hidden="true">&times;</span>
                                </button>
                            </div>
                        </c:if>
                        <div class="form-group">
                            <label for="username">아이디</label>
                            <input type="text" class="form-control" name="username" id="username" placeholder="Username">
                        </div>
                        <div class="form-group">
                            <label for="password">비밀번호</label>
                            <input type="password" class="form-control" name="password" id="password" placeholder="Password">
                        </div>
                        <button type="submit" class="btn btn-primary">로그인</button>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
