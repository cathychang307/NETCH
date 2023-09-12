<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="singleQueryResult.title">
        <!--查詢結果-->
    </spring:message></title>
</head>
<body>
    <div class="tr01 pr01">
        <script>
                  loadScript('js/query/function/multiQueryResult');
                </script>
        <c:choose>
            <c:when test="${maxQueryCount == 1 }">
                <c:set var="detailPage" value="singleQuery.htm" />
                <c:set var="queryMode" value="單筆" />
            </c:when>
            <c:otherwise>
                <c:set var="detailPage" value="multiQuery.htm" />
                <c:set var="queryMode" value="多筆" />
            </c:otherwise>
        </c:choose>
        <form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
            <input type="hidden" name="inputTransactionId" id="inputTransactionId" value="">
            <div class="row hidden-print">
                <div class="form-group">
                    <div class="col-sm-10">
                        <spring:message code="singleQuery.result.thisInquiry">
                            <!-- 本次共查詢 -->
                        </spring:message>
                        ${fn:length(inquiryThreads) }
                        <spring:message code="singleQuery.result.rows">
                            <!-- 筆 -->
                        </spring:message>
                    </div>
                </div>
            </div>
            <div class="btns hidden-print">
                <button type="button" id="print" name="print" class="btn btn-default">
                    <spring:message code="singleQuery.btn.print">
                        <!-- 列 印 -->
                    </spring:message>
                </button>
                <button type="button" id="previous" name="previous" class="btn btn-default"
                    onclick="$.ajax({url: url('singlequeryhandler/inquiry'), data: {'inputTransactionId': '${inputTransactionId }'}}).done(function(){router.to('function/singleQueryDetail');});return false;">
                    <spring:message code="singleQuery.btn.previous">
                        <!-- 回到上頁 -->
                    </spring:message>
                </button>
                <button type="button" id="complete" name="complete" class="btn btn-default">
                    <spring:message code="btn.end">
                        <!-- 結 束 -->
                    </spring:message>
                </button>
            </div>
        </form>
        <c:forEach items="${inquiryThreads }" var="inquiry" varStatus="inquiryStatus">
            <c:if test="${ inquiryStatus.index > 0 }">
                <div style="page-break-before: always;">&nbsp;</div>
            </c:if>
            <div class="row row-flex hidden-print">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <ol>
                <li><span style="color: blue"><spring:message code="singleQuery.inquiry.which">
                            <!-- 第 -->
                        </spring:message> ${inquiryStatus.count } / ${fn:length(inquiryThreads) } <spring:message code="multiQuery.inquiry.rowInquiry">
                            <!-- 筆查詢 -->
                        </spring:message></span></li>
            </ol>
            <div>
                <c:choose>
                    <c:when test="${ inquiry.status == 10 }">
                        <%-- STATUS_RUNNING ==> timeout --%>
                        <c:set var="statusDesc" value="查詢逾時無回應" scope="page" />
                    </c:when>
                    <c:when test="${ inquiry.status == 20 }">
                        <%-- STATUS_COMPLETED --%>
                        <c:set var="statusDesc" value="查詢完成" scope="page" />
                    </c:when>
                    <c:when test="${ inquiry.status == 30 }">
                        <%-- STATUS_ERROR  --%>
                        <c:set var="statusDesc" value="${inquiry.throwable.message }" scope="page" />
                    </c:when>
                </c:choose>
                <%-- 
    Bot_<c:out value="${bank.departmentId }"/>_<c:out value="${user.employeeId }"/>
    _<c:if test="${not empty inquiryCacheTime }"><c:out value="${inquiry.inquiryLog.inquiryCacheTime }"/></c:if><c:if test="${empty inquiryCacheTime }"><c:out value="${inquiry.inquiryLog.inquiryTime }"/></c:if>
    _<c:if test="${inquiry.inquiryLog.inquiryCacheFlag }">1</c:if><c:if test="${not inquiry.inquiryLog.inquiryCacheFlag }">0</c:if>
     --%>
                <div class="row row-flex">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span style="font-weight: bold"><spring:message code="singleQueryResult.state">
                                <!-- 查詢狀態 -->
                            </spring:message></span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">${statusDesc }&nbsp;</div>
                    <c:forEach items="${queryDef.queryInputFieldDefinition }" var="fieldDef" varStatus="fieldDefStatus">
                        <c:set var="currentFieldName" value="${fieldDef.fieldName }_${ inquiry.inquiryLog.requestFieldPosition }" scope="page" />
                        <div class="col-sm-4 col-xs-12 td_style_1">
                            <span style="font-weight: bold">${fieldDef.fieldDesc}</span>
                        </div>
                        <div class="col-sm-8 col-xs-12 td_style_1">${paramMap[ currentFieldName ][0] }&nbsp;</div>
                    </c:forEach>
                </div>
            </div>
            <div class="row row-flex hidden-print">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <%-- 無錯誤的列出本文 --%>
            <c:if test="${ inquiry.status == 20 }">
        ${inquiry.inquiryLog.inquiryResponse}
    </c:if>
    </div>
    </c:forEach>
    </div>
</body>
</html>
