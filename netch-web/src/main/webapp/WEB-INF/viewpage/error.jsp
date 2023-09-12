<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="none">
<title>錯誤頁面</title>
<link rel="stylesheet" href="../static/css/default.css?cache=20220329" />
<script>
  function backToIndex() {
    window.location.href = "index";
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
						<h3>網頁執行失敗</h3>
					</td>
				</tr>
			</table>			
		</div>
		<div style="text-align: center; ">
			回到<a onclick="backToIndex();return false;" style="cursor: pointer;">主功能頁</a>
		</div>
		<div style="text-align: center; ">
			錯誤訊息：
			<span id="errorMsgPage">${errorMessage}</span>
		</div>
	</div>
</body>
</html>


<%--
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="decorator" content="none">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>錯誤頁面</title>
<script>
// if (window.location.pathname!=url('page/error')){
//   window.location = url('page/error');
// }
</script>
</head>
<body>
	<h2>${errorMsg}error.jsp</h2>
	<div id="aaa"></div>
</body>
</html>
--%>
