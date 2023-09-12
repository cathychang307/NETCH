<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="accountCaculateType.title">
		<!--帳務作業參數設定-->
	</spring:message></title>
</head>
<body class="scrollbar1" bgcolor="#ffffff"
	background="<c:url value="../static/images/bgOrange2.gif" />">
	<div class="tr01">
		<script>
          loadScript('js/query/function/accountCaculateType');
        </script>

		<!-- title -->
		<div class="row rowMargin">
			<table width="100%" cellpadding="2" cellspacing="2"
				style="margin: 10px 0px;">
				<tr>
					<td width="8%"><img src="../static/images/011.jpg" width="40"
						height="40"></td>
					<td width="92%" class="style1">
						<h3>
							<spring:message code="accountCaculateType.title">
								<!--帳務作業參數設定-->
							</spring:message>
						</h3>
					</td>
				</tr>
			</table>
		</div>

		<form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="accountCaculateType.accountCaculateType"> <!-- 帳務作業參數 --> </spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1">
						<div class="row">
							<div class="col-sm-12 col-xs-12">
								<label>
									<input type="radio" id="newvalue" name="newvalue" value="0" />
									<spring:message code="accountCaculateType.type.0"> <!-- 啟用一般計算 --> </spring:message>
								</label>
							</div>
							<div class="col-sm-12 col-xs-12">
								<label>
									<input type="radio" id="newvalue" name="newvalue" value="1" />
									<spring:message code="accountCaculateType.type.1"> <!-- 啟用公式計算 --> </spring:message>
								</label>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>

		<!-- errorMsg -->
		<div class="row rowMargin">
			<div id="errorMsgSection" class="text-red"></div>
		</div>

		<!-- btn -->
		<div class="row rowMargin">
			<div class="btns" align="center">
				<button type="button" id="configure" name="configure"
					class="btn btn-default">
					<spring:message code="accountCaculateType.btn.configure">
						<!-- 設 定 -->
					</spring:message>
				</button>
				<button type="button" id="cancel" name="cancel"
					class="btn btn-default">
					<spring:message code="btn.cancel">
						<!-- 取消 -->
					</spring:message>
				</button>
			</div>
		</div>
	</div>
</body>
</html>
