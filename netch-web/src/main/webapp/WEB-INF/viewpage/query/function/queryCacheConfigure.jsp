<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="queryCacheConfigure.title">
        <!--快取作業參數設定-->
    </spring:message></title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/queryCacheConfigure');
        </script>
        
        <!-- title -->
        <div class="row">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/011.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3>
							<spring:message code="queryCacheConfigure.title">
								<!--快取作業參數設定-->
							</spring:message>
						</h3>
					</td>
				</tr>
			</table>			
		</div>

		<form method="POST" id="actionForm" name="actionForm" onsubmit="return false;">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="queryCacheConfigure.queryCache"> <!-- 資料快取 --> </spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1">
						<div class="row">
							<div class="col-sm-12 col-xs-12">
								<spring:message code="queryCacheConfigure.currentDay"> <!-- 快取時間 --> </spring:message>
								<span id="currentvalue" name="currentvalue" class="text-red"></span>
								<spring:message code="queryCacheConfigure.days"> <!-- 天 --> </spring:message>
							</div>
							<div class="col-sm-12 col-xs-12">
								<spring:message code="queryCacheConfigure.setting"> <!-- 新設時間 --> </spring:message>
								<spring:message code="queryCacheConfigure.currentDay"> <!-- 快取時間 --> </spring:message>
								<input type="text" id="newvalue" name="newvalue" size="5" class="number" /> 
								<spring:message code="queryCacheConfigure.days"> <!-- 天 --> </spring:message>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>

		<!-- errorMsg -->
		<div class="row"> 
		    <div id="errorMsgSection" class="text-red"></div>
		</div>
    
    	<!-- btn -->
		<div class="row">
			<div class="btns" align="center">
				<button type="button" id="configure" name="configure" class="btn btn-default">
					<spring:message code="queryCacheConfigure.btn.configure">
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
