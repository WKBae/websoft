<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="idValidity"
       value="${errors.contains('ID_TOO_SHORT') or errors.contains('ID_DUPLICATE')? ' is-invalid' : ''}"/>
<c:set var="pwValidity" value="${errors.contains('PASSWORD_TOO_SHORT')? ' is-invalid' : ''}"/>
<c:set var="nameValidity" value="${errors.contains('NAME_EMPTY')? ' is-invalid' : ''}"/>

<t:bootstrap title="WebSoft :: 회원가입">
    <jsp:attribute name="head">
        <style>
            body {
                background: #EEE;
            }
        </style>
    </jsp:attribute>
    <jsp:attribute name="script">
        <script>
            var $pw = $("#password");
            var $pwCheck = $("#password-check");
            $pw.on('input', function () {
                if (this.value.length < 6) {
                    $pw.addClass("is-invalid");
                } else {
                    $pw.removeClass("is-invalid");
                }
                if ($pwCheck.val() != $pw.val()) {
                    $pwCheck.addClass("is-invalid");
                } else {
                    $pwCheck.removeClass("is-invalid");
                }
            });
            $pwCheck.on('input', function () {
                if (this.value != $pw.val()) {
                    $pwCheck.addClass("is-invalid");
                } else {
                    $pwCheck.removeClass("is-invalid");
                }
            });
            $("#sign-form").on('submit', function (e) {
                if ($pw.val() != $pwCheck.val()) {
                    $pwCheck.addClass("is-invalid");
                    $pwCheck.focus();
                    e.preventDefault();
                    e.stopPropagation();
                } else if (this.checkValidity() === false) {
                    e.preventDefault();
                    e.stopPropagation();
                }
                this.classList.add('was-validated');
            });
        </script>
    </jsp:attribute>
    <jsp:body>
        <div class="container">
            <div class="row">
                <div class="col">
                    <form action="<c:url value="/sign"/>" method="POST" id="sign-form" novalidate>
                        <div class="form-group">
                            <label for="username">아이디</label>
                            <input type="text" class="form-control${idValidity}" name="username" id="username"
                                   placeholder="Username" required value="${id}">
                            <div class="invalid-feedback">
                                <c:choose>
                                    <c:when test="${errors.contains('ID_TOO_SHORT')}">
                                        아이디가 너무 짧습니다.
                                    </c:when>
                                    <c:when test="${errors.contains('ID_DUPLICATE')}">
                                        아이디가 중복됩니다.
                                    </c:when>
                                    <c:otherwise>
                                        아이디를 입력해주세요.
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="password">비밀번호</label>
                            <input type="password" class="form-control${pwValidity}" name="password" id="password"
                                   placeholder="Password" required>
                            <div class="invalid-feedback">
                                비밀번호가 너무 짧습니다.
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="password-check">비밀번호 확인</label>
                            <input type="password" class="form-control" id="password-check"
                                   placeholder="Repeat password" required>
                            <div class="invalid-feedback">
                                비밀번호와 다릅니다.
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="name">이름</label>
                            <input type="text" class="form-control${nameValidity}" name="name" id="name"
                                   placeholder="Name" required value="${name}">
                            <div class="invalid-feedback">
                                이름을 입력해주세요.
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="email">이메일</label>
                            <input type="email" class="form-control" name="email" id="email" placeholder="Email"
                                   value="${email}">
                        </div>
                        <button type="submit" class="btn btn-primary">회원가입</button>
                    </form>
                </div>
            </div>
        </div>
    </jsp:body>
</t:bootstrap>
