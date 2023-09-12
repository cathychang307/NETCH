<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="none">
<title>錯誤頁面</title>
<link rel="stylesheet" href="../static/css/default.css?cache=20220329" />
<script type="text/javascript">
    var count = 3;
    function countdownAndRedirect() {
        document.getElementById( "countdownField" ).innerHTML = count + ' 秒後前往';
        if( count > 0 ) {
            count--;
            setTimeout( "countdownAndRedirect()", 1000 );
        }else {
            top.location.href = "https://web.bot.com.tw/";
        }
    }
    function countAndRedirect() {
        document.getElementById( "countdownField" ).innerHTML = count + ' 秒後前往';
        if( count > 0 )
            return;
        top.location.href = "https://web.bot.com.tw/";
    }

    function zeroAndRedirect() {
        count = 0;
        countAndRedirect();
    }
    window.onload = function (){
      countdownAndRedirect();
    }
</script>

</head>
<body>
	<div class="tr01"style="width: 60%; margin: 150px auto;">
		<!-- title -->
        <div class="row rowMargin">
        	<table width="100%" cellpadding="2" cellspacing="2" style="margin: 10px 0px;">
				<tr>
					<td width="8%">
						<img src="../static/images/010.jpg" width="40" height="40">
					</td>
					<td width="92%" class="style1">
						<h3>這個網頁必需登入才能執行</h3>
					</td>
				</tr>
			</table>			
		</div>
		<div style="text-align: center; ">
			<span id="countdownField" class="errPageText1">&nbsp;</span> 
			<u><a href="#" onclick="zeroAndRedirect();return false;" style="color: blue;">全行資訊網</a></u>
		</div>
	</div>
</body>
</html>
