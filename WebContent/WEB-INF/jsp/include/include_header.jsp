<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Cache-Control" content="No-Cache"/>
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

    if (path.includes("/wsms/")) {
        activeMenu = document.getElementById("wsms");
    } else if (path.includes("/wdms/")) {
        activeMenu = document.getElementById("wdms");
    } else {
        activeMenu = document.getElementById("whms"); 
    }

    if (activeMenu) activeMenu.classList.add("active");
});

function movePage(idx) {
    var urls = [
        "/whms/whms_monitoring.do",
        "/wsms/wsms_monitoring.do",
        "/wdms/wdms_monitoring.do"
    ];
    location.href = urls[idx - 1];
}

</script>
<body>
	<input type="hidden" class="currentDateFormat" value="${currentDateFormat}" />
	<input type="hidden" class="endDateFormat" value="${endDateFormat}" />
<div id="wrap">
    <div class="header_top">
        <ul>
	        <li><a href="javascript:movePage(1);" id="whms">하드웨어</a></li>
	        <li><a href="javascript:movePage(2);" id="wsms">시스템</a></li>
	        <li><a href="javascript:movePage(3);" id="wdms">데이터</a></li>
	    </ul>
    </div>
	
