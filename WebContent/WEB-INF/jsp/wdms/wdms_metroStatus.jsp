<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
	<style type="text/css"> @import url("/css/wdms.css");</style>
	<script src="/js/plugin/clipboard/clipboard.min.js"></script>
	<title>WDMS - Wiseplus Data Mornitoring System</title>
<script>
function todayErrorCount() {
	var currentTime = new Date();
	if (currentTime.getMinutes() < 1) {
		currentTime.setHours(currentTime.getHours() - 1);
	}
	currentTime.setMinutes(0);
    var dayParam = 't';
    var boardTime = currentTime.getFullYear().toString() + '-' + (currentTime.getMonth() + 1).toString().padStart(2, '0') + '-' + currentTime.getDate().toString().padStart(2, '0') + ' ' + currentTime.getHours().toString().padStart(2, '0') + ':' + currentTime.getMinutes().toString().padStart(2, '0');
    
    $.ajax({
        url: '/wdmsCont/fileListData.do',
        data: {boardTime: boardTime, dayParam: dayParam},
        success: function (data) {
        	var server_error_count = {khnp: 0, rnd: 0};
        	var error_flag_std = {khnp: false, rnd: false};
        	
        	for (var i = 0; i < data.length; i++){
            	if (data[i]["server_nm"] == 'khnp'){
	                if (data[i]["error_flag"] == 'y') {
	                    error_flag_std.khnp = true;
                    }
            	} else if (data[i]["server_nm"] == 'rnd'){
                    if (data[i]["error_flag"] == 'y') {
                        error_flag_std.rnd = true;
                    }
            	}
            }

            for (var i = 0; i < data.length; i++) {
                var server_nm = data[i]["server_nm"];
                var repo_nm = data[i]["repo_nm"];
                var file_nm = data[i]["file_nm"];
                var file_count = data[i]["file_count"];
                var std_count = data[i]["std_count"];
                var error_flag = data[i]["error_flag"];
                var error_flag_desc = "";
                	if (error_flag == 'y') {error_flag_desc = '이상'} else if (error_flag == 'c') {error_flag_desc = '재생산'} else {error_flag_desc = '정상'}
                var file_path = data[i]["file_path"];
                var file_desc = data[i]["file_desc"];
                var file_attr = data[i]["file_attr"];
                    if (file_attr == '1'){file_attr = '원시'} else if (file_attr == '2') {file_attr = '생산'};
                
                    if (error_flag == 'y' || error_flag == 'c') {
                        if (server_nm == 'khnp' && repo_nm == '/home/outer/data') {
                        	server_error_count.khnp++;
                        } else if (server_nm == 'rnd' && repo_nm == '/nas/met') {
                        	server_error_count.rnd++;
                        }
                    }
            }
            createTdayErrorMsg(boardTime, server_error_count, error_flag_std);
        }
    });
}

function createTdayErrorMsg(boardTime, server_error_count, error_flag_std){
	var alarmTime = new Date(boardTime);
    var formattedAlarmTime = alarmTime.getFullYear().toString().padStart(2, '0') + '-' + (alarmTime.getMonth() + 1).toString().padStart(2, '0') + '-' + alarmTime.getDate().toString().padStart(2, '0') + ' ' + alarmTime.getHours().toString().padStart(2, '0') + ':' + alarmTime.getMinutes().toString().padStart(2, '0');
	
	var errorMsg = [];
	var alarmMsg = '';
	
    if (server_error_count.khnp > 0 && error_flag_std.khnp == true) {errorMsg.push('<span class="khnp" style="color: #FFC21F">KHNP</span>');}
    if (server_error_count.rnd > 0 && error_flag_std.rnd == true) {errorMsg.push('<span class="rnd" style="color: #7a62f8">RND</span>');}

    if (errorMsg.length > 0) {
    	alarmMsg = '[' + formattedAlarmTime + '] ' + errorMsg.join(', ') + ' 자료에 <span>문제</span>가 발생하였습니다.';
    } else {
    	alarmMsg = '[' + formattedAlarmTime + '] 모든 자료가 정상입니다.';
    }

    $('header .alarm.active').html('<p>' + alarmMsg + '</p>');
}

function replaceNull(value) {
    if (value == null || value == undefined || value.toString().trim() == '') {
        return '-';
    } else {
        return value;
    }
}

function selectMetroStatus(){
	$.ajax({
		url: '/wdmsCont/selectMetroStatus.do',
		success: function(data){
			var metro_status_con = '';
			for (var i = 0; i < data.length; i++){
				var row = data[i];
        		for (var key in row) {
        			row[key] = replaceNull(row[key]);
        		}
        		
        		var type_id = row["type_id"];
				var type_nm = row["type_nm"];
				var file_nm = row["file_nm"];
				var file_desc = row["file_desc"];
				var file_id = row["file_id"];
				var file_start_year = row["file_start_year"];
				var file_end_year = row["file_end_year"];
				var db_start_year = row["db_start_year"];
				var db_end_year = row["db_end_year"];
				var ins_date_check = row["ins_date_check"];
				var ins_size_check = row["ins_size_check"];
				var ins_val_check = row["ins_val_check"];
				var last_update = row["last_update"];
				var last_update_txt = last_update.substring(0,4) + '-' + last_update.substring(4,6) + '-' + last_update.substring(6,8);
				var msg = row["msg"];
				var data_source = row["data_source"];
				metro_status_con +=	`
					<tr>
						<input type="hidden" class="hidden_file_id" value="${'${file_id}'}" />
						<input type="hidden" class="hidden_type_id" value="${'${type_id}'}" />
						<input type="hidden" class="hidden_ins_date_check" value="${'${ins_date_check}'}" />
						<input type="hidden" class="hidden_ins_size_check" value="${'${ins_size_check}'}" />
						<input type="hidden" class="hidden_ins_val_check" value="${'${ins_val_check}'}" />
						<td>${'${type_nm}'}</td>
	                    <td>${'${file_desc}'}</td>
	                    <td><input type="text" value="${'${file_start_year}'}" class="situ_input file_start_year" /></td>
	                    <td><input type="text" value="${'${file_end_year}'}" class="situ_input file_end_year" /></td>
	                    <td><input type="text" value="${'${db_start_year}'}" class="situ_input db_start_year" /></td>
	                    <td><input type="text" value="${'${db_end_year}'}" class="situ_input db_end_year" /></td>
	                    <td>
	                        <select class="ins_date_check situ_select">
	                            <option value="y">O</option>
	                            <option value="n">X</option>
	                        </select>
	                    </td>
	                    <td>
		                    <select class="ins_size_check situ_select">
		                        <option value="y">O</option>
		                        <option value="n">X</option>
		                    </select>
		                </td>
		                <td>
		                    <select class="ins_val_check situ_select">
		                        <option value="y">O</option>
		                        <option value="n">X</option>
		                    </select>
		                </td>
	                    <td>${'${data_source}'}</td>
	                    <td><input type="text" value="${'${last_update}'}" class="date datepicker last_update_picker situ_input last_update" placeholder="날짜 선택" data-lastUpdate="${'${last_update_txt}'}" /></td>
	                    <td><input type="text" value="${'${msg}'}" class="msg t_left" /></td>
	                </tr>`;
			}
			$('.tbl_con_wrap .tbl_wrap2 .tbl_state tbody').html(metro_status_con);

			$(".tbl_state tbody tr").each(function () {
			    var ins_date_check = $(this).find(".hidden_ins_date_check").val();
			    var ins_size_check = $(this).find(".hidden_ins_size_check").val();
			    var ins_val_check = $(this).find(".hidden_ins_val_check").val();
			    
			    $(this).find(".ins_date_check").val(ins_date_check);
			    $(this).find(".ins_size_check").val(ins_size_check);
			    $(this).find(".ins_val_check").val(ins_val_check);

			    if ($(this).find(".ins_date_check option:selected").text() == 'O') {
			    	$(this).find(".ins_date_check").addClass('font_pink');
			    	$(this).find(".ins_date_check option").css('color',"#FFFFFF");
			    }
			    if ($(this).find(".ins_size_check option:selected").text() == 'O') {
			    	$(this).find(".ins_size_check").addClass('font_pink');
			    	$(this).find(".ins_size_check option").css('color',"#FFFFFF");
			    }
			    if ($(this).find(".ins_val_check option:selected").text() == 'O') {
			    	$(this).find(".ins_val_check").addClass('font_pink');
			    	$(this).find(".ins_val_check option").css('color',"#FFFFFF");
			    }
			});
			
            $('.last_update_picker').datepicker({
		        firstDay: 1,
		        showOtherMonths: true,
		        changeMonth: false,
		        changeYear: false,
		        maxDate: new Date(),
		        dateFormat: "yy-mm-dd",
		        monthNames: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
		        monthNamesShort: ['1월', '2월', '3월', '4월', '5월', '6월', '7월', '8월', '9월', '10월', '11월', '12월'],
		        dayNames: ['일', '월', '화', '수', '목', '금', '토'],
		        dayNamesShort: ['일', '월', '화', '수', '목', '금', '토'],
		        dayNamesMin: ['일', '월', '화', '수', '목', '금', '토'],
		    });

			$(".last_update_picker").each(function() {
				var lastUpdate = $(this).val();
				if (lastUpdate == '-') {
					$(this).html('-');
				} else {
					$(this).datepicker('setDate', $(this).attr("data-lastUpdate"));
				}
			});

			$(document).on("mousedown", ".date", function () {
                $(".ui-datepicker").addClass("active");
            });

            $(document).on("click", ".btn_state", function () {
                $(this).toggleClass("active");
            });

            $(document).on("change", ".datepicker", function () {
                $(this).val($(this).val());
            });
		}
	});
}

function createTblDataList(){
	var tbl_data = [];
	$(".tbl_state tbody tr").each(function(){
		var file_id = $(this).find(".hidden_file_id").val();
		var type_id = $(this).find(".hidden_type_id").val();
		var row_data = {
			file_id: file_id,
			type_id: type_id,
            file_start_year: $(this).find(".file_start_year").val()||"",
            file_end_year: $(this).find(".file_end_year").val()||"",
            db_start_year: $(this).find(".db_start_year").val()||"",
            db_end_year: $(this).find(".db_end_year").val()||"",
            ins_date_check: $(this).find(".ins_date_check").val()||"",
            ins_size_check: $(this).find(".ins_size_check").val()||"",
            ins_val_check: $(this).find(".ins_val_check").val()||"",
            last_update: ($(this).find(".last_update").val()||"").replace(/\-/g, ""),
            msg: $(this).find(".msg").val()||""
		};
		tbl_data.push(row_data);
	});
	return tbl_data;
}

function saveChangeValue(){
	var metroStatusData = createTblDataList();
	$.ajax({
        url: '/wdmsCont/updateMetroStatus.do',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(metroStatusData),
        success: function (data) {
        	if(confirm("저장하시겠습니까?")){
        		alert("저장되었습니다.");
        		location.reload();
        	} else {
        		return;
        	}
        }
    });
}

function hourlySchedule() {
    var now = new Date();
    var targetTime = new Date();
    
    targetTime.setHours(now.getHours() + (now.getMinutes() >= 1 ? 1 : 0));
    targetTime.setMinutes(1);
    targetTime.setSeconds(0);
    targetTime.setMilliseconds(0);

    var nextTimeTarget = (targetTime - now);
    
    setTimeout(function() {
    	todayErrorCount();
        setInterval(function() {
        	todayErrorCount();
        }, 60 * 60 * 1000);
    }, nextTimeTarget);
}

$(function () {
	var currentPage = window.location.pathname;
	if (currentPage == '/wdms/wdms_monitoring.do') {$('.mornitoring').addClass('active');} else if (currentPage == '/wdms/wdms_specMng.do') {$('.spec_mng').addClass('active');} else if (currentPage == '/wdms/wdms_metroStatus.do') {$('.metro_status').addClass('active');}
	todayErrorCount();
	selectMetroStatus();
	hourlySchedule();
});
</script>
<style>
	.tbl_state tbody tr td input[type=text]:focus {color: #000000; background-color: rgba(255, 255, 255, 0.5);}
	.tbl_state tbody tr td input[type=text].datepicker:focus {color: #FFFFFF; background-color: #505050;}
	.tbl_state tbody .msg {width: 100%;}
	.tbl_state tbody .situ_select.font_pink {color: #F98C11;}
	.tbl_state tbody tr td:nth-child(7) {background-color: rgba(0, 0, 0, 0.3);}
	.tbl_state tbody tr td:nth-child(n+12):nth-child(-n+14) {background-color: rgba(0, 0, 0, 0.3);}
</style>
    <header>
        <h1>
        	<a href="/wdms/wdms_monitoring.do" title="WDMS 홈으로">
	        	<img src="/images/img_wdms.svg" alt="">
	            <div>
	                <p>WDMS</p>
	                <small>Wiseplus Data Monitoring Server</small>
	            </div>
            </a>
        </h1>
        <c:choose>
        	<c:when test="${sessionScope.user_id == 'admin'}">
				<div class="alarm active"></div>
	        </c:when>
		</c:choose>
        <nav class="nav_wrap">
			<ul>
                <li><a href="/wdms/wdms_monitoring.do" class="mornitoring">모니터링</a></li>
                <li><a href="/wdms/wdms_metroStatus.do" class="metro_status">기상자료 현황</a></li>
                <li><a href="/wdms/wdms_specMng.do" class="spec_mng">제원관리</a></li>
            </ul>
        </nav>
    </header>
	<!-- contents //-->
    <div id="contents">
        <div class="tbl_comment_wrap">
            <ul>
                <li>날짜체크 : 비어있는 날짜 유무 검수</li>
                <li>용량체크 : 데이터가 없는 파일 검수</li>
                <li>극값체크 : 값의 이상유무를 판단해서 결과를 보정하는 작업 (예: 10분 강우 50mm 초과시 0.0mm 변환)</li>
            </ul>
        </div>
        <div class="tbl_con_wrap">
            <!--기상자료현황 테이블-->
            <div class="tbl_wrap2 tbl_situation">
                <table class="tbl_state">
                    <caption>관할기관, 자료명, 보유파일, 보유DB, 검수여부, 특이사항으로 만들어진 기상자료현황 테이블 입니다.</caption>
                    <colgroup>
                        <col style="width:5%;">
                        <col style="width:10%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:5%;">
                        <col style="width:10%;">
                        <col style="width:5%;">
                        <col style="width:auto;">
                    </colgroup>
                    <thead>
                        <tr>
                            <th rowspan="2" class="b_right">관할기관</th>
                            <th rowspan="2" class="b_right">자료명</th>
                            <th colspan="2" class="b_right b_bottom">보유파일</th>
                            <th colspan="2" class="b_right b_bottom">보유DB</th>
                            <th colspan="3" class="b_right b_bottom">검수여부</th>
                            <th rowspan="2" class="b_right">자료출처</th>
                            <th rowspan="2" class="b_right">최종 업데이트일</th>
                            <th rowspan="2" class="b_right">특이사항</th>
                        </tr>
                        <tr>
                            <th class="b_right">시작연도</th>
                            <th class="b_right">종료연도</th>
                            <th class="b_right">시작연도</th>
                            <th class="b_right">종료연도</th>
                            <th class="b_right">날짜체크</th>
                            <th class="b_right">용량체크</th>
                            <th class="b_right">극값체크</th>
                        </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <c:choose>
	        	<c:when test="${sessionScope.user_id == 'admin'}">
					<div class="t_right"><button type="button" class="btn_situation" onclick="saveChangeValue();">저장</button></div>
		        </c:when>
			</c:choose>
        </div>
    </div>
    <!-- contents -->
	<jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>