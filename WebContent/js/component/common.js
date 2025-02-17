/* *******************************************************
 * filename : common
 * Author   : wise system kkh
 * description : gnb menu / menu active / web accessibility
 * date : 2024-02
******************************************************** */
$(function(){
	// text splitting
	Splitting();
		
	// gnb mobile
	$("#gnb > ul > li").on("focusin mouseenter",function(){
		$(this).children('.gnb-2dep').toggleClass('active');
	});
	$("#gnb > ul > li").on("focusout mouseleave",function(){
		$(this).children('.gnb-2dep').removeClass('active');
	});

	// gnb mobile
	$(".gnbM-btn").on("click",function(){
		$('html, body').toggleClass('ov_hidden');
		$(this).toggleClass('active');
		$('#gnbM').toggleClass('active');
	});

	// gnb mobile link unactive
	$(".nav > ul > li > a").on("click",function(){
		$('.gnbM-btn').toggleClass('active');
		$('#gnbM').toggleClass('active');
	});

	// gnb active
	$.initmenu = function(a,b,c){
		$('#gnb a').removeClass("active");
		
		var a = a - 1;
		var b = b - 1;
		
		if(c == 1) {
			$('#gnb > ul').find('>li:eq('+a+') > a').addClass('active'); //대메뉴
			
		} else {
			$('#gnb > ul').find('>li:eq('+a+') > div > ul > li:eq('+b+') > a').addClass('active'); //소메뉴
		}
		
		$('.gnb_nav').find(' ul >li:eq('+b+') > a').addClass('active'); //gnd
	}

	// GNB 웹접근성 처리 (탭키 이동) **important!!
	$('body').keyup(function(e) {
		var code = e.keyCode || e.which;
		if (code == '9') {
			$('.navigation > li:last-child > div > ul > li:last-child a').on('focusout',function(){ 
				$('.gnbM-btn').removeClass("active");
				$(this).parents().removeClass("active");
			});
		}
	});

});

/**
 * 쿠키
 * 
 * String cookieName 설정할 쿠키 이름
 * String saveId #아이디 저장 체크 박스
 * String user_id #사용자 아이디
 */

function getCookie(cookieName, saveIdCheckBox, userId) {
   	var cookies = document.cookie.match('(^|;)' + cookieName + '=([^;]*)');
   	var user_id_cookie = cookies ? cookies[2] : null;
   	if (user_id_cookie != null) {
       $(saveIdCheckBox).prop("checked", true);
       $(userId).val(user_id_cookie);
   	};
   	return user_id_cookie;
}

/**
 * 로그인
 * 
 * String user_id #사용자 아이디
 * String user_pw #사용자 비밀번호
 * String saveId #아이디 저장 체크 박스
 */

function Login(userId, userPw, saveIdCheckBox) {
    var userId_val = $(userId).val();
    var userPw_val = $(userPw).val();
    var saveIdCheckBox_val = $(saveIdCheckBox).prop("checked");

    function loginResult(msg, reset) {
        $('#target').html(msg);
        if (reset == true) {
        	$(userId).val("");
        	$(userPw).val("");
        	$(saveIdCheckBox).prop("checked", false);
        }
    }

    $.ajax({
        url: "/cont/login.do",
        data: {
			userId: userId_val,
			userPw: userPw_val,
			saveIdCheckBox: saveIdCheckBox_val
		},
        type: 'POST',
        success: function(data) {
			var status = data.status;
			if (status == "id_error") {
                loginResult("아이디 또는 비밀번호를 잘못 입력했습니다.<br />입력하신 내용을 다시 확인해주세요.", true);
            } else if (status == "success") {
                loginResult("로그인 성공<br />user_id : " + userId_val, false);
            } else if (status == "account_lock") {
                loginResult("계정을 사용할 수 없습니다.<br />관리자에게 문의 바랍니다.", true);
            } else if (status == "pw_error") {
                loginResult("비밀번호를 잘못 입력했습니다.<br />입력하신 내용을 다시 확인해주세요.", true);
            } else {
                loginResult("로그인 오류", true);
            }
        }
    });
}