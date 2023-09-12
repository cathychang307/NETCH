<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.operationSummary.title"><!--作業查詢狀況統計--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/operationSummary');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/11.gif" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.operationSummary.title"><!--作業查詢狀況統計--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- table -->
        <div class="rowMargin">
	        <form id="mform" onsubmit="return false;">
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.operationSummary.table.title.01"><!--使用者ID --></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="userId" name="userId" style="width:75%;"/></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.operationSummary.table.title.02"><!--單位代號 --></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><select id="departmentId" name="departmentId" combotype="4" space="ALL" style="width:75%;"></select></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.operationSummary.table.title.03"><!--起始時間 --></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="startDate" name="startDate" class="date"/></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.operationSummary.table.title.04"><!--結束時間--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="endDate" name="endDate" class="date"/></div>
			    </div>
	        </form>
        </div>
		
		<!-- errorMsg -->
		<div class="row rowMargin"> 
		    <div id="errorMsgSection" class="text-red"></div>
		</div>
		
		<!-- btn -->
		<div class="row rowMargin">
		    <div class="btns" align="center">
		        <button id="query" type="button" class="btn1"><spring:message code="btn.query"><!--查詢--></spring:message></button>
		        <button id="clear" type="button" class="btn1"><spring:message code="btn.clear"><!--清除--></spring:message></button>
		        <button id="csv" type="button" class="btn1"><spring:message code="btn.csv"><!--CSV--></spring:message></button>
		        <button id="pdf" type="button" class="btn1"><spring:message code="btn.pdf"><!--PDF--></spring:message></button>
		    </div>
		</div>
		
		<!-- grid -->
		<div class="row rowMargin">
			<div class="col-sm-12">
				<div id="gridview"></div>
			</div>
		</div>
		
		<!-- remark -->
		<div class="row rowMargin">
			<div class="col-md-12">
				備註：<br/>
				1. 若起始時間與結束時間皆不輸入，則查詢時間設為 [今日]<br/>
				2. 若起始時間與結束時間只輸入一筆，則以 [輸入日至今日] 為查詢區間
			</div>
		</div>
</div>
</body>
</html>
