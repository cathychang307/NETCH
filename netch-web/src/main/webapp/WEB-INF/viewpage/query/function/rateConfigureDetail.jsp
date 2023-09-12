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
          loadScript('js/query/function/rateConfigureDetail');
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
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.01"><!--查詢類型--></spring:message>
				</div>
				<div class="col-sm-9 col-xs-12 td_style_1">
					<select id="inputRateType" name="inputRateType" combotype="2" class="primary" style="width:75%;"></select>
				</div>
			</div>
			<div class="row row-flex">
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.06"><!--啟用日期--></spring:message>
				</div>
				<div class="col-sm-9 col-xs-12 td_style_1">
					<select type="text" id="inputYear" name="inputYear" combotype="2" class="primary"></select>
                    <spring:message code="rateQuery.year"><!--年--></spring:message>
                    <select type="text" id="inputMonth" name="inputMonth" combotype="2" class="primary"></select>
                    <spring:message code="rateQuery.month"><!--月--></spring:message>
                </div>
			</div>
			<div class="row row-flex">
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.04"><!--一般費率--></spring:message>
				</div>
				<div class="col-sm-3 col-xs-12 td_style_1">
					<spring:message code="js.rateConfigure.currntRate"><!--目前費率--></spring:message>
	                <span id="transactionRate" name="transactionRate" class="field"></span>
                </div>
				<div class="col-sm-6 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.newRate"><!--新費率--></spring:message>
					<input type="text" id="inputRate" name="inputRate" style="width:50%;"/>
				</div>
			</div>

			<div class="row row-flex">
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.05"><!--手續費--></spring:message>
				</div>
				<div class="col-sm-3 col-xs-12 td_style_1"> 
					<spring:message code="rateConfigure.currnt"><!--目前--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.05"><!--手續費--></spring:message>
                    <span id="transactionPoundage" name="transactionPoundage" class="field"></span>
                </div>
				<div class="col-sm-6 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.newRate"><!--新費率--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.05"><!--手續費--></spring:message>
                    <input type="text" id="inputPoundage" name="inputPoundage" style="width:50%;"/>
                </div>
			</div>
			<div class="row row-flex">
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.07"><!--折扣門檻--></spring:message>
				</div>
				<div class="col-sm-3 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.currnt"><!--目前--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.07"><!--折扣門檻--></spring:message>
                    <span id="transactionRecordsAtDiscount" name="transactionRecordsAtDiscount" class="field"></span>
                </div>
				<div class="col-sm-6 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.newRate"><!--新費率--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.07"><!--折扣門檻--></spring:message>
                    <input type="text" id="inputRecordsAtDiscount" name="inputRecordsAtDiscount" style="width:50%;"/>
                </div>
			</div>
			<div class="row row-flex">
				<div class="col-sm-3 col-xs-12 hd_style_1">
					<spring:message code="js.rateConfigure.grid.title.08"><!--折扣費率--></spring:message>
				</div>
				<div class="col-sm-3 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.currnt"><!--目前--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.08"><!--折扣費率--></spring:message>
                    <span id="transactionDiscountRate" name="transactionDiscountRate" class="field"></span>
                    <spring:message code="rateConfigure.percent"><!--(%)--></spring:message>
                   </div>
				<div class="col-sm-6 col-xs-12 td_style_1">
					<spring:message code="rateConfigure.new"><!--新--></spring:message>
                    <spring:message code="js.rateConfigure.grid.title.08"><!--折扣費率--></spring:message>
                    <input type="text" id="inputDiscountRate" name="inputDiscountRate" style="width:50%;"/>
                    <spring:message code="rateConfigure.percent"><!--(%)--></spring:message>
                </div>
			</div>
		</div>

		<div class="col-sm-4 col-xs-12">*OBU以美元計費</div>

        <!-- errorMsg -->
        <div class="row rowMargin">
            <div id="errorMsgSection" class="text-red"></div>
        </div>

        <div class="btns rowMargin" align="center">
            <button type="button" id="sure" name="sure" class="btn btn-default">
                <spring:message code="btn.execute">
                    <!-- 確 定 -->
                </spring:message>
            </button>
            <button type="button" id="clear" name="clear" class="btn btn-default">
                <spring:message code="btn.clear">
                    <!-- 清 除 -->
                </spring:message>
            </button>
            <button type="button" id="cancel" name="cancel" class="btn btn-default">
                <spring:message code="btn.cancel">
                    <!-- 取 消 -->
                </spring:message>
            </button>
        </div>
	</form>
</div>
</body>
</html>
