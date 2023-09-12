<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
<meta name="decorator" content="none">
<title><spring:message code="rateConfigure.title">
        <!--費率設定-->
    </spring:message></title>
</head>
<body>
<div class="tr01">
    <script>
          loadScript('js/query/function/rateConfigureResult');
        </script>
        
        <!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/008.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3>
							<spring:message code="rateConfigure.title">
								<!--費率設定-->
							</spring:message>
						</h3>
					</td>
				</tr>
			</table>			
		</div>


    <form method="POST" id="actionForm" name="actionForm" class="form-horizontal" role="form" onsubmit="return false;" autocomplete="off">
	    <div class="rowMargin">
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.01"><!--查詢類型--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionType" name="transactionType"></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.03"><!--交易代號--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="inputRateType" name="inputRateType" ></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.02"><!--說明--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionName" name="transactionName"></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.06"><!--啟用日期--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="effectDateRocYMD" name="effectDateRocYMD"></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.04"><!--一般費率--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionRate" name="transactionRate"></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.05"><!--手續費--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionPoundage" name="transactionPoundage"></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.07"><!--折扣門檻--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionRecordsAtDiscount" name="transactionRecordsAtDiscount" ></span></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="js.rateConfigure.grid.title.08"><!--折扣費率--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="transactionDiscountRate" name="transactionDiscountRate"></span><spring:message code="rateConfigure.percent"><!--(%)--></spring:message></div>
			</div>
		    <div class="row row-flex">		
				<div class="col-sm-4 col-xs-12 hd_style_1"><spring:message code="rateConfigure.status"><!--狀態--></spring:message></div>
				<div class="col-sm-8 col-xs-12 td_style_1"><span id="status" name="status"></span></div>
			</div>
		</div>
        <!-- errorMsg -->
        <div class="row rowMargin">
            <div id="errorMsgSection" class="text-red"></div>
        </div>
        <!-- btns -->
        <div class="btns rowMargin" align="center">
            <button type="button" id="complete" name="complete" class="btn btn-default">
                <spring:message code="btn.end">
                    <!-- 結 束 -->
                </spring:message>
            </button>
        </div>
    </form>
</div>
</body>
</html>
