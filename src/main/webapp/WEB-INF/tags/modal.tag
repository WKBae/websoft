<%@ tag description="Bootstrap modal template" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" %>
<%@ attribute name="formId" %>
<%@ attribute name="title" %>
<%@ attribute name="body" fragment="true" %>
<%@ attribute name="footer" fragment="true" %>

<div class="modal fade" id="${id}" tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <c:if test="${not empty formId}">
            <form id="${formId}">
                </c:if>
                <div class="modal-header">
                    <h5 class="modal-title">${title}</h5>
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
                <div class="modal-body">
                    <jsp:invoke fragment="body"/>
                </div>
                <div class="modal-footer">
                    <jsp:invoke fragment="footer"/>
                </div>
                <c:if test="${not empty formId}">
            </form>
            </c:if>
        </div>
    </div>
</div>
