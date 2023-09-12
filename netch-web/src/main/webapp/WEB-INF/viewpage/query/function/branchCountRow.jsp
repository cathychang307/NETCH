<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.branchCountRow.title"><!--新增角色--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/branchCountRow');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/009.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.branchCountRow.title"><!--查詢統計--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- table -->
        <div class="rowMargin">
	        <form id="mform" onsubmit="return false;">
			    <div class="row row-flex">
			      <div class="col-sm-12 col-xs-12 hd_style_2"><spring:message code="js.branchCountRow.tableTitle"><!--查詢統計--></spring:message></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchCountRow.inputYearMonth"><!--時間--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1">
			      	<select id="inputYear" name="inputYear" combotype="2"></select>
					<spring:message code="js.branchCountRow.year"><!--年--></spring:message>
					<select id="inputMonth" name="inputMonth" combotype="2"></select>
					<spring:message code="js.branchCountRow.month"><!--月--></spring:message>
			      </div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.branchCountRow.explaination.01"><!--說明--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><spring:message code="js.branchCountRow.explaination.02"><!--總筆數=各類連線總和+各類快取總和--></spring:message></div>
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
</div>
</body>
</html>
