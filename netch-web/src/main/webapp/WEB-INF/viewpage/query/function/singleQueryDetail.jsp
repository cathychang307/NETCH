<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="singleQuery.title">
        <!--單筆查詢-->
    </spring:message></title>
</head>
<body>
    <div class="tr01">
        <script>
                  loadScript('js/query/function/singleQueryDetail');
                </script>

        <form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
            <input type="hidden" id="action" name="action" value="query"> <input type="hidden" id="inputTransactionId" name="inputTransactionId" value="${ inputTransactionId }">
            <c:choose>
                <c:when test="${ queryDef.displayTransactionId }">
                    <c:set var="queryDisplayName" value="${queryDef.queryName } (查詢代號${queryDef.transactionId })" />
                </c:when>
                <c:otherwise>
                    <c:set var="queryDisplayName" value="${queryDef.queryName }" />
                </c:otherwise>
            </c:choose>
            <div class="row rowMargin">
                <div class="row row-flex">
                    <div class="col-sm-1 col-xs-12">
                        <img src="<c:url value="../static/images/09.gif" />" width="43" height="41">
                    </div>
                    <div class="col-sm-11 col-xs-12 style1">
                        <h2>${queryDisplayName }</h2>
                    </div>
                </div>
            </div>
            <!-- errorMsg -->
            <div class="row rowMargin">
                <div id="errorMsgSection" class="text-red"></div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_2">${queryDisplayName }</div>
            </div>
            <c:forEach items="${queryDef.queryInputFieldDefinition }" var="fieldDef" varStatus="fieldDefStatus">
                <c:set var="currentFieldName" value="${fieldDef.fieldName }_0" scope="page" />
                <div class="row row-flex">
                    <div class="col-sm-4 col-xs-12 hd_style_1" align="center">
                        <label class="control-label"> <span>${fieldDef.fieldDesc} <c:if test="${fieldDef.required }">
                                    <span class="text-red">(*)</span>
                                </c:if>
                        </span>
                        </label>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">
                        <input type="text" id="${currentFieldName}" name="${currentFieldName}" value="${paramMap[ currentFieldName ][0] }" />
                    </div>
                </div>
            </c:forEach>

            <div class="row row-flex">
                <div class="col-sm-4 col-xs-12 hd_style_1" align="center">
                    <label for="memo" class="control-label"> <spring:message code="singleQuery.memo">
                            <!--說明-->
                        </spring:message>
                    </label>
                </div>
                <div class="col-sm-8 col-xs-12 td_style_1">
                    <ul>
                        <c:forEach items="${queryDef.memo}" var="memo">
                            <li><span class="style1">${ memo}</span></li>
                        </c:forEach>
                    </ul>
                    <div class="text-red" style="padding: 20px;">
                        <spring:message code="singleQuery.notice.require">
                            <!--(*)表示必須輸入-->
                        </spring:message>
                    </div>
                </div>
            </div>
            <div class="row rowMargin">
                <div class="btns" align="center">
                    <button type="button" id="sure" name="sure" class="btn btn-default">
                        <spring:message code="btn.sure">
                            <!-- 確 定 -->
                        </spring:message>
                    </button>
                    <button type="button" id="clear" name="clear" class="btn btn-default">
                        <spring:message code="btn.clear">
                            <!-- 清 除 -->
                        </spring:message>
                    </button>
                </div>
            </div>
        </form>
    </div>
</body>
</html>
