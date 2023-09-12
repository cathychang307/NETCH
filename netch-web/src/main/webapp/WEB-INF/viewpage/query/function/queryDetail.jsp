<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title>
	<spring:message code="js.queryDetail.title"><!--查詢明細--></spring:message>
</title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/queryDetail');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/009.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><spring:message code="js.queryDetail.title"><!--查詢明細--></spring:message></h3>
					</td>
				</tr>
			</table>			
		</div>
		
		<!-- table -->
        <div class="rowMargin">
	        <form id="mform" onsubmit="return false;">
			    <div class="row row-flex">
			      <div class="col-sm-12 col-xs-12 hd_style_2"><spring:message code="js.queryDetail.tableTitle"><!--查詢條件--></spring:message></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.queryDetail.table.title.01"><!--時間(起)--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="startDate" name="startDate" class="date"/></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.queryDetail.table.title.02"><!--時間(迄)--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="endDate" name="endDate" class="date"/></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.queryDetail.table.title.03"><!--付費分行--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><select id="inquiryChargeBankId" name="inquiryChargeBankId" combotype="4" space="ALL" style="width: 75%"></select></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.queryDetail.table.title.04"><!--查詢人ID--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><input type="text" id="inquiryAccount" name="inquiryAccount" style="width: 75%"/></div>
			    </div>
			    <div class="row row-flex">
			      <div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.queryDetail.table.title.05"><!--查詢類型--></spring:message></div>
			      <div class="col-sm-8 col-xs-12 td_style_1"><select id="inquiryTxCode" name="inquiryTxCode" combotype="4" space="ALL" style="width: 75%"></select></div>
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
				1. 若起始時間與結束時間皆不輸入，則查詢時間設為今日<br/>
				2. 若起始時間與結束時間只輸入一筆，則以輸入日當做查詢時間<br/>
				3. 起始時間與結束時間之查詢週期為 60 天
			</div>
		</div>
</div>
</body>
</html>
