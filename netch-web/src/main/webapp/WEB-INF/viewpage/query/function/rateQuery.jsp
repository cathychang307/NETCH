<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="rateQuery.title">
        <!--費率查詢-->
    </spring:message></title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/rateQuery');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/009.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3>
							<spring:message code="rateQuery.title">
								<!--費率查詢-->
							</spring:message>
						</h3>
					</td>
				</tr>
			</table>			
		</div>


	    <form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateQuery.grid.title.01"> <!--查詢類型--> </spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><select id="inputRateType" name="inputRateType" combotype="2" space="ALL" style="width: 80%;"></select></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="rateQuery.time"> <!--時間--> </spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><spring:message code="rateQuery.time"> <!--時間--> </spring:message>                
		                    <select id="inputYear" name="inputYear" combotype="2"></select>
		                    <spring:message code="rateQuery.year"><!--年--></spring:message>
		                    <select id="inputMonth" name="inputMonth" combotype="2"></select>
		                    <spring:message code="rateQuery.month"><!--月--></spring:message></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="rateQuery.constraint"> <!--查詢條件--> </spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><spring:message code="rateQuery.constraint"> <!--查詢條件--> </spring:message>
			                <input type="radio" id="inputQueryType" name="inputQueryType" value="0" checked />
			                <spring:message code="rateQuery.currntRate"> <!--目前費率--> </spring:message>
			                <input type="radio" id="inputQueryType" name="inputQueryType" value="1" /> 
			                <spring:message code="rateQuery.historyRate"> <!--歷史紀錄--> </spring:message></div>
				</div>
			</div>
	        <!-- errorMsg -->
	        <div class="row rowMargin">
	            <div id="errorMsgSection" class="text-red"></div>
	        </div>
	        <!-- btns -->
	        <div class="btns rowMargin" align="center">
	            <button type="button" id="query" name="query" class="btn btn-default">
	                <spring:message code="btn.query">
	                    <!-- 查 詢 -->
	                </spring:message>
	            </button>
	            <button type="button" id="clear" name="clear" class="btn btn-default">
	                <spring:message code="btn.clear">
	                    <!-- 清 除 -->
	                </spring:message>
	            </button>
	        </div>
	    </form>
	    
	    <!-- grid -->
	    <div class="row rowMargin">
	        <div id="gridzone">
	            <div class="col-sm-12">
	                <div id="gridview"></div>
	            </div>
				<div class="col-sm-4 col-xs-12" style="padding: 8px;">*OBU以美元計費</div>
			</div>
	    </div>
</div>
</body>
</html>
