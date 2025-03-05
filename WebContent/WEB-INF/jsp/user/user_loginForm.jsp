<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<script src="/js/plugin/jquery/jquery-1.12.4.min.js"></script>
	<script src="/js/plugin/jquery/jquery-ui.min.js"></script>
	<link rel="stylesheet" href="/css/common.css" />
</head>

<script>
$(function() {
	
	$('#login_btn').click(function() {
		var user_id = $('#user_id').val();
		var user_pw = $('#user_pw').val();
		
		$.ajax({
			url : "../userCont/userLogInChk.do",
			type:"post",
			data: JSON.stringify({ user_id: user_id, user_pw: user_pw }),
		    contentType: "application/json",
			success: function(data) {
				if(data == 1) {
					$('#login_form').submit();
				}else {
					alert("아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요.");
				}
			}
		});
		
	});
	
	$(document).keypress(function(event) {
	    if (event.keyCode == 13) {
	        $('#login_btn').click();
	    }
	});
	
});

</script>

<body>
	<div id="login_wrap">
        <form id="login_form" method="post" action="/user/user_login.do">
            <div class="login_form">
                <div>
                    <div class="img_logo"><img src="/images/img_wiseplus.svg" alt="wiseplus"></div>
                    <div class="login_text">
                        <p><strong>Login</strong></p>
                        <p>Wiseplus Monitoring Server</p>
                    </div>
                    <div class="login_input">
                        <p class="input_id"><input type="text"  id="user_id" name="user_id" placeholder="아이디를 입력하세요"></p>
                        <p class="input_pw"><input type="password" name="user_pw" id="user_pw" placeholder="비밀번호를 입력하세요"></p>
                    	<button type="button" class="input_loginbtn" id="login_btn">로그인</button>
                    </div>
                </div>
            </div>
        </form>
        <jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>


