<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="queryCountConfigure.title">
        <!--系統查詢參數設定-->
    </spring:message></title>
</head>
<body class="scrollbar1" bgcolor="#ffffff" background="<c:url value="../static/images/bgOrange2.gif" />">
<div class="tr01">
    <script>
          loadScript('js/query/function/queryCountConfigure');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/011.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="queryCountConfigure.title"><!--系統查詢參數設定--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>

		<form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="queryCountConfigure.queryCount"><!-- 資料查詢最大筆數 --></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1">
						<div class="row">
							<div class="col-sm-12 col-xs-12">
								<spring:message code="queryCountConfigure.currentCount"><!-- 目前設定 --></spring:message>
								<span id="currentvalue" name="currentvalue" class="text-red"></span> 
								<spring:message code="queryCountConfigure.rows"><!-- 筆 --></spring:message>
							</div>
							<div class="col-sm-12 col-xs-12">
								<spring:message code="queryCountConfigure.setting"><!-- 新設筆數 --></spring:message>
								<select size="1" id="newvalue" name="newvalue">
									<option>300</option>
									<option>500</option>
									<option>800</option>
									<option>1500</option>
								</select>
								<spring:message code="queryCountConfigure.rows"><!-- 筆 --></spring:message>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>

		<!-- btn -->
		<div class="row rowMargin">
		    <div class="btns" align="center">
				<button type="button" id="configure" name="configure" class="btn btn-default">
					<spring:message code="queryCountConfigure.btn.configure">
						<!-- 設 定 -->
					</spring:message>
				</button>
				<button type="button" id="cancel" name="cancel" class="btn btn-default">
					<spring:message code="btn.cancel">
						<!-- 取消 -->
					</spring:message>
				</button>
			</div>
		</div>
    
</div>
</body>
</html>
