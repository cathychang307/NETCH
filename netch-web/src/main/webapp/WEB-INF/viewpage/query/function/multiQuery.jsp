<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="multiQuery.title">
        <!--多筆查詢-->
    </spring:message></title>
</head>
<body>
    <div class="tr01">


        <!-- title -->
        <div class="row rowMargin">
            <div class="row row-flex">
                <div class="col-sm-1 col-xs-12">
                    <img src="../static/images/09.gif" width="40" height="40">
                </div>
                <div class="col-sm-11 col-xs-12 style1">
                    <h2>
                        <spring:message code="multiQuery.queryMode">
                            <!--請選擇多筆查詢類別-->
                        </spring:message>
                    </h2>
                </div>
            </div>
        </div>

        <form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
            <!-- errorMsg -->
            <div class="row rowMargin">
                <div id="errorMsgSection" class="text-red"></div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK02.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">

                    <span><spring:message code="multiQuery.msg.1">
                            <!-- 提供被查詢者最近三年內退票總張數、總金額及其他相關資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="0" end="2">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">
                        <c:choose>
                            <c:when test="${'4111' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryById">
                                        <!-- 依個人統一編號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4112' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4113' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByBankAccount">
                                        <!-- 依銀行代號及帳號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK03.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">
                    <span><spring:message code="multiQuery.msg.2">
                            <!-- 提供被查詢者最近三年內退票總張數、總金額、退票明細資料及其他相關資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="3" end="5">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">

                        <c:choose>
                            <c:when test="${'4114' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryById">
                                        <!-- 依個人統一編號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4115' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4116' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByBankAccount">
                                        <!-- 依銀行代號及帳號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK04.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">
                    <span><spring:message code="multiQuery.msg.3">
                            <!-- 提供被查詢者最近三年內全部列管資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="6" end="7">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">
                        <c:choose>
                            <c:when test="${'4121' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryById">
                                        <!-- 依個人統一編號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4122' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK05.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">
                    <span><spring:message code="multiQuery.msg.3">
                            <!-- 提供被查詢者最近三年內全部列管資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="8" end="9">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">
                        <c:choose>
                            <c:when test="${'4123' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryById">
                                        <!-- 依個人統一編號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4124' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK07.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">

                    <span><spring:message code="multiQuery.msg.1">
                            <!-- 提供被查詢者最近三年內退票總張數、總金額及其他相關資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="10" end="11">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">
                        <c:choose>
                            <c:when test="${'4132' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4133' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByBankAccount">
                                        <!-- 依銀行代號及帳號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12" style="padding: 0px 0px;">
                    <img src="<c:url value="../static/images/BANK01.gif" />" width="24" height="31"><img src="<c:url value="../static/images/BANK08.gif" />" width="146" height="31">
                </div>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12 hd_style_1">
                    <span><spring:message code="multiQuery.msg.2">
                            <!-- 提供被查詢者最近三年內退票總張數、總金額、退票明細資料及其他相關資訊。 -->
                        </spring:message></span>
                </div>
            </div>
            <div class="row row-flex">
                <c:forEach items="${availableRateList }" var="rateType" varStatus="rateTypeStatus" begin="12" end="13">
                    <div class="col-sm-4 col-xs-12 td_style_1">
                        <span class="btns">
                            <button type="button" id="query${rateType.transactionShortDesc }" name="query${rateType.transactionShortDesc }" class="btn btn-default"
                                onclick="$.ajax({url: url('multiqueryhandler/multiInquiry'), data: {'inputTransactionId': '${rateType.transactionShortDesc }'}}).done(function(){router.to('function/multiQueryDetail');});">${rateType.key.transactionId}</button>
                        </span>
                    </div>
                    <div class="col-sm-8 col-xs-12 td_style_1">

                        <c:choose>
                            <c:when test="${'4135' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByCompany">
                                        <!-- 依公司行號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:when test="${'4136' == rateType.key.transactionId }">
                                <span><spring:message code="multiQuery.inquiryByBankAccount">
                                        <!-- 依銀行代號及帳號查詢 -->
                                    </spring:message></span>
                            </c:when>
                            <c:otherwise>
                                <span></span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:forEach>
            </div>
            <div class="row row-flex">
                <div class="col-sm-12 col-xs-12">
                    <hr>
                </div>
            </div>
        </form>
    </div>
</body>
</html>
