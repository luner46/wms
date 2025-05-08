<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link rel="stylesheet" href="/css/jquery.mCustomScrollbar.css" />
	<link rel="stylesheet" href="/css/jquery-ui.min.css" />
	<script src="/js/plugin/clipboard/clipboard.min.js"></script>
	<script src="/js/plugin/jquery/jquery-1.12.4.min.js"></script>
	<script src="/js/plugin/jquery/jquery-ui.min.js"></script>
<title>WMS - Wiseplus Mornitoring System</title>
</head>
<script>
document.addEventListener("DOMContentLoaded", function() {
    var path = window.location.pathname;
    var activeMenu = null;
    
    if(path.includes("/wsms/")) activeMenu = document.getElementById("wsms");
    if(path.includes("/wdms/")) activeMenu = document.getElementById("wdms");
    if(path.includes("/whms/")) activeMenu = document.getElementById("whms");
    
    if (!activeMenu) activeMenu = document.getElementById("whms");
    if (activeMenu) activeMenu.classList.add("active");

});
</script>
<body>
	<input type="hidden" class="currentDateFormat" value="${currentDateFormat}" />
	<input type="hidden" class="endDateFormat" value="${endDateFormat}" />
<div id="wrap">
    <div class="header_top">
        <ul>
        	<c:choose>
        		<c:when test="${sessionScope.user_id != null && sessionScope.user_id == 'admin'}">
        			<li><a href="/whms/whms_monitoring.do" id="whms">하드웨어</a></li>
			        <li><a href="/wsms/wsms_monitoring.do" id="wsms">시스템</a></li>
			        <li><a href="/wdms/wdms_monitoring.do" id="wdms">데이터</a></li>
			        <li><a href="/user/user_logout.do">로그아웃</a></li>
        		</c:when>
        		<c:when test="${sessionScope.user_id != null && sessionScope.user_id == 'guest'}">
        			<li><a href="/whms/whms_monitoring.do" id="whms">하드웨어</a></li>
			        <li><a href="/wsms/wsms_monitoring.do" id="wsms">시스템</a></li>
			        <li><a href="/wdms/wdms_monitoring.do" id="wdms">데이터</a></li>
			        <li><a href="/user/user_loginForm.do">로그인</a></li>
        		</c:when>
        		<c:when test="${sessionScope.user_id == null}">
        		</c:when>
        	</c:choose>
	    </ul>
    </div>
	
