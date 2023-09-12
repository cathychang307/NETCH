<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="applicationParameterResult.title.E020">
        <!--系統查詢參數設定-->
    </spring:message></title>
</head>
<body>
<div class="tr01">
    	<script>
          loadScript('js/query/function/applicationParameterResult');
        </script>
        
        
        <!-- title -->
        <div class="row">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/011.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3><span id="title" name="title"></span></h3>
					</td>
				</tr>
			</table>			
		</div>
		
    	<form id="mform" name="mform" onsubmit="return false;">
			<div class="rowMargin">
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"> <span id="name" name="name"> </span></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="value" name="value"></span></div>
				</div>
				<div class="row row-flex">
					<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="applicationParameterResult.status"><!-- 狀態 --></spring:message></div>
					<div class="col-sm-8 col-xs-12 td_style_1"><span id="status" name="status"></span></div>
				</div>
			</div>
			<!-- errorMsg -->
			<div class="row"> 
			    <div id="errorMsgSection" class="text-red"></div>
			</div>
			<!-- btn -->
			<div class="row">
	            <div class="btns"  align="center">
	                <button type="button" class="btn btn-default" id="complete" name="complete">
	                    <spring:message code="btn.end">
	                        <!-- 結束 -->
	                    </spring:message>
	                </button>
	            </div>
			</div>
	    </form>
</div>
</body>
</html>
