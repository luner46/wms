<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
    <style type="text/css"> @import url("/css/wsms.css");</style>
	<script src="/js/plugin/jquery/jquery-1.12.4.min.js"></script>
	<title>WSMS - Wiseplus System Monitoring Server</title>

<script>

function inputSystemInfo() {
	var system_ord = $("#system_ord").val();
	
	if (!system_ord) {
        alert("순서 번호를 입력해주세요.");
        return;
    }
	
	// 중복 순서 입력 방지
	var duplicate_ord = [];
	
	// 중복 순서 입력 방지
	$("input[name='up_system_ord']").each(function() {
		duplicate_ord.push($(this).val());
    });
	
	// 중복 순서 입력 방지
    if (duplicate_ord.includes(system_ord)) {
        alert("중복순서 번호입니다. 다른 번호를 입력해주세요.");
        return;
    }
	
 	// system_req 값을 가져와서 정수로 변환 후 1000을 곱함
    var system_req = parseInt($("#system_req").val()) * 1000;
	
    var systemData = {
        system_ord: system_ord,
        system_nm: $("#system_nm").val(),
        system_url: $("#system_url").val(),
        system_req: system_req,
        system_res: $("#system_res").val(),
        system_agc: $("#system_agc").val(),
        system_plc: $("#system_plc").val()
    };
    
    var answer = confirm("등록하시겠습니까?");
    
    if(answer == true) {
        $.ajax({
            url: '/wsmsCont/inputSystemInfo.do',
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            dataType: 'json', 
            data: JSON.stringify(systemData), 
            success: function(response) {
            	location.reload(true);
            },
            error: function(xhr, status, error) {
            	event.preventDefault();
            }
        });
    }
}

function updateSystemInfo(num) {
	var system_ord = $("#up_system_ord_" + num).val();
	var system_req = parseInt($("#up_req_time_" + num).val()) * 1000;
	
	var systemData = {
        system_id: num,
        system_ord: system_ord,
        system_nm: $("#up_system_nm_" + num).val(),
        system_url: $("#up_system_url_" + num).val(),
        system_req: system_req,
        system_res: $("#up_res_time_" + num).val(),
        system_agc: $("#up_order_agency_" + num).val(),
        system_plc: $("#up_server_place_" + num).val()
    };
	
	// 중복 순서 입력 방지
    var duplicate_ord = [];
    var isDuplicate = false;

    $("input[name='up_system_ord']").each(function () {
        var ordValue = $(this).val();
        if (ordValue === system_ord && $(this).attr("id") !== "up_system_ord_" + num) {
            isDuplicate = true;
        }
        duplicate_ord.push(ordValue);
    });

    if (isDuplicate) {
        alert("중복된 순서 번호입니다. 다른 번호를 입력해주세요.");
        return;
    }

	
	var answer = confirm("수정하시겠습니까?");
	
	if(answer == true) {
        $.ajax({
            url: '/wsmsCont/updateSystemInfo.do',
            type: 'POST',
            contentType: 'application/json; charset=UTF-8',
            dataType: 'json', 
            data: JSON.stringify(systemData), 
            success: function(response) {
            	location.reload(true);
            },
            error: function(xhr, status, error) {
            	event.preventDefault();
            }
        });
    }
	
}

function getCurrentDateTime() {
    var now = new Date();
    var year = now.getFullYear();
    var month = (now.getMonth() + 1).toString().padStart(2, '0'); 
    var day = now.getDate().toString().padStart(2, '0');
    var hours = now.getHours().toString().padStart(2, '0');
    var minutes = now.getMinutes().toString().padStart(2, '0');

    return year + '-' + month + '-' + day + ' ' + hours + ':' + minutes;
}

$(function(){
	
	document.getElementById('menu2').classList.add('active');

    function updateDateTime() {
        $('.alarm').html('<p>' + getCurrentDateTime() + ' : 전체 시스템이 정상입니다.</p>');
    }

    updateDateTime();

    setInterval(updateDateTime, 30000);
    
});

</script>
    <header>
        <h1><img src="/images/img_wsms.svg" alt="">
            <div>
            	<a href="/wsms/wsms_monitoring.do">
                 <p>WSMS</p>
                 <small>Wiseplus System Monitoring Server</small>
             </a>
            </div>
        </h1>
        <div class="alarm">
            <p>2024-08-07 12:00 : 전체 서버가 정상입니다.</p>
        </div>
        <!--  
        <div class="alarm">
            <p>2024-08-07 12:00 : <span>홍수량 분석 시스템</span> 에 문제가 발생했습니다.</p>
        </div>-->
        <nav class="nav_wrap">
            <ul>
                <li><a id="menu1" href="/wsms/wsms_monitoring.do">모니터링</a></li>
                <li><a id="menu2" href="/wsms/wsms_management.do">시스템 관리</a></li>
            </ul>
        </nav>
    </header>
    <!--// header -->
    <!-- contents //-->
    <div id="setting">
        <div class="setting_wrap">
            <h2>시스템 관리</h2>
            <div class="tbl_wrap">
                <table class="tbl_basic">
                    <colgroup>
                        <col style="width: 80px;">
                        <col>
                        <col>
                        <col style="width: 140px;">
                        <col style="width: 140px;">
                        <col>
                        <col>
                        <col style="width: 120px;">
                    </colgroup>
                    <thead>
                        <tr>
                            <th>순서</th>
                            <th>시스템명</th>
                            <th>URL</th>
                            <th>작동시간</th>
                            <th>응답시간</th>
                            <th>담당기관</th>
                            <th>서버위치</th>
                            <th>등록/수정</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="e" items="${dataList}">
						    <tr>
						        <td>
						            <div class="form_wrap"><input type="text" id="up_system_ord_${e.system_id}" name="up_system_ord" value="${e.system_ord}"></div>
						        </td>
						        <td>
								    <div class="form_wrap"><input type="text" id="up_system_nm_${e.system_id}" name="up_system_nm" value="${e.system_nm}"></div>
								</td>
								<td>
								    <div class="form_wrap"><input type="text" id="up_system_url_${e.system_id}" name="up_system_url" value="${e.system_url}"></div>
								</td>
								<td>
								    <div class="form_wrap"><input type="text" id="up_req_time_${e.system_id}" name="up_req_time" value="${(e.req_time * 0.001).intValue()}"><span>min</span></div>
								</td>
								<td>
								    <div class="form_wrap"><input type="text" id="up_res_time_${e.system_id}" name="up_res_time" value="${e.res_time}"><span>ms</span></div>
								</td>
								<td>
								    <div class="form_wrap"><input type="text" id="up_order_agency_${e.system_id}" name="up_order_agency" value="${e.order_agency}"></div>
								</td>
								<td>
								    <div class="form_wrap"><input type="text" id="up_server_place_${e.system_id}" name="up_server_place" value="${e.server_place}"></div>
								</td>
						        <td>
						            <div class="form_wrap"><button class="btn btn_modify" onclick="updateSystemInfo(${e.system_id});">수정</button></div>
						            <!--  <button class="btn" onclick="deleteSystemInfo(${e.system_id});">삭제</button> -->
						        </td>
						    </tr>
						</c:forEach>
							<tr class="form_input">
								<td><div class="form_wrap"><input type="text" id="system_ord" name="system_ord" placeholder="순서"></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_nm" name="system_nm" placeholder="시스템명"></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_url" name="system_url" placeholder="URL" ></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_req" name="system_req" placeholder="작동시간"><span>min</span></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_res" name="system_res" placeholder="응답시간"><span>ms</span></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_agc" name="system_agc" placeholder="담당기관"></div></td>
                                <td><div class="form_wrap"><input type="text" id="system_plc" name="system_plc" placeholder="서버위치"></div></td>
                                <td><div class="form_wrap"><button class="btn btn_confirm" id="system_input" onclick="javascript:inputSystemInfo();">등록</button></div></td>
                            </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
    <!--// contents -->
    <jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>