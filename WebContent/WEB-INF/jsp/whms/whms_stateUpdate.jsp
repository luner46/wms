<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="Pragma" content="no-cache"/>
	<meta http-equiv="Cache-Control" content="No-Cache"/>
    <link rel="stylesheet" href="/css/jquery.mCustomScrollbar.css" />
    <style type="text/css">@import url("/css/whms.css");</style>
	<script src="/js/plugin/jquery/jquery-1.12.4.min.js"></script>
	<script src="/js/plugin/jquery/jquery-ui.min.js"></script>
	<script src="/js/plugin/highcharts/highcharts.js"></script>
	<title>WMS - Wiseplus Monitoring Server</title>
</head>
<script>
function riskStateData(riskStateOrder){
	$.ajax({
		url: '/whmsCont/riskStateData.do',
		data: {riskStateOrder: riskStateOrder},
		success: function(data){
            $('.board_wrap tbody').empty();
            
            if (!data || data.length == 0) {
            	$('.board_wrap table').hide();
                $('.board_none_span').show();
                $('.board_wrap').addClass('board_none');
                $('.t_center').html('현재 <span class="c_blue">총 0건의 장애</span> 중 <span class="c_pink">0건의 처리</span>가 필요합니다.');
                
                topBoardData('asc');
                return;
            } else {
                $('.board_wrap').removeClass('board_none');
                $('.board_none_span').hide();
                $('.board_wrap table').show();
            }
            
			for(var i = 0; i < data.length; i++){
				var risk_check_count = data[i]["risk_check_count"];
				var risk_result_count = data[i]["risk_result_count"];
				var risk_check = data[i]["risk_check"];
				var risk_result = data[i]["risk_result"];
				var server_id = data[i]["server_id"];
                var server_nm = data[i]["server_nm"];
                var cpu_per = data[i]["cpu_per"];
                var mem_per = data[i]["mem_per"];
                var disk_per = data[i]["disk_per"];
                var net_per = data[i]["net_per"] || '-';
                var temp_val = data[i]["temp_val"] || '-';
                var init_tm = data[i]["init_tm"];
                var msg = data[i]["msg"];
                var st_cpu_per = data[i]["st_cpu_per"];
                var st_mem_per = data[i]["st_mem_per"];
                var st_disk_per = data[i]["st_disk_per"];
                var st_temp_val = data[i]["st_temp_val"];
                var st_net_per = data[i]["st_net_per"];
                
                var date = new Date(data[i].date);
				var formattedDate = date.getFullYear().toString() + '-' +(date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0') + ' ' + date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0');
				var currentTime = new Date();
                var formattedCurrentDate = currentTime.getFullYear().toString() + '-' +(currentTime.getMonth() + 1).toString().padStart(2, '0') + '-' + currentTime.getDate().toString().padStart(2, '0') + ' ' + currentTime.getHours().toString().padStart(2, '0') + ':' + currentTime.getMinutes().toString().padStart(2, '0');

                var riskTimeGap = '';
                
          		var riskTimeGapDay = (currentTime - date)/(1000*60*60*24);
          		var riskTimeGapHour = (currentTime - date)/(1000*60*60);
          		var riskTimeGapMinute = (currentTime - date)/(1000*60);
          		
          		/* if (riskTimeGapDay > 1){
          			riskTimeGap = Math.floor(riskTimeGapDay) + '일 전';
          		} */
          		if (riskTimeGapHour > 1) {
          			riskTimeGap = Math.floor(riskTimeGapHour) + '시간 전';
          		} else if (riskTimeGapMinute > 0) {
          			if (riskTimeGapMinute < 1){
          				riskTimeGap = '0분 전';
          			} else {
          				riskTimeGap = Math.floor(riskTimeGapMinute) + '분 전';
          			}
          		}
          		
          		var disk_per_MySQL = '';
            	var disk_per_PSQL = '';
            	
            	var disk_per_split = disk_per.split('_');
            		disk_per_MySQL = disk_per_split[0];
            		disk_per_PSQL = disk_per_split[1];
          		
          		var riskContent = '';
                
          		if (server_id == 2){
          			var riskIssue = ['CPU', 'MEMORY', 'DISK', 'NET', 'NET'];
          			var riskCriteria = [st_cpu_per + '%', st_mem_per + '%', st_disk_per + '%', st_net_per + '%', st_net_per + '%'];
          		} else {
          			var riskIssue = ['CPU', 'MEMORY', 'DISK', 'TEMP', 'NET'];
          			var riskCriteria = [st_cpu_per + '%', st_mem_per + '%', st_disk_per + '%', st_temp_val + '°C', st_net_per + '%'];
          		}
          		
          		var msgSplit = msg.split('_').map(Number);
          		var riskResult = [];
          		var eachRiskCriteria = [];
          		
          		if (server_id == 3){
          			var riskState = [cpu_per + '%', mem_per + '%', 'MySQL - ' + disk_per_MySQL + '%, PSQL - ' + disk_per_PSQL + '%', temp_val + '°C', net_per + '%'];
          		} else {
          			var riskState = [cpu_per + '%', mem_per + '%', disk_per + '%', temp_val + '°C', net_per + '%'];
          		}
          		if (server_id == 2){
          			var riskState = [cpu_per + '%', mem_per + '%', disk_per + '%', net_per + '%', net_per + '%'];
          		}
          		var eachRiskState = [];
          		
          		msgSplit.forEach(function(value, index) {
          		    if (value == 1) {
          		        riskResult.push(riskIssue[index]);
          		      	eachRiskCriteria.push(riskCriteria[index]);
          		      	eachRiskState.push(riskState[index]);
          		    }
          		});

          		if (riskResult.length > 0) {
          		    riskContent = '[' + riskResult.join(', ') + '] 에 문제가 발생하였습니다.<span class="comment">(현재 : ' + eachRiskState.join(', ') + ' / 기준 : ' + eachRiskCriteria.join(', ') + ')</span>';
          		}
          		
          		if (risk_result == 0){
          		    risk_result_msg = 'disabled>처리완료';
          		    risk_active = '>';
          		  	riskCheckBox = '';
          		} else {
          		    risk_result_msg = 'onClick="riskStateChange(' + server_id + ',' + init_tm + ')"> 처리요망';
          		    risk_active = ' class="active">';
          		  riskCheckBox = '<input type="checkbox" class="each_checkBox" value="' + server_id + ',' + init_tm + '" />';
          		}
                
                var riskData = `
                    <tr` + risk_active + `
                    	<td>` + riskCheckBox + `</td>
                		<td>` + riskTimeGap + `</td>
                        <td>` + server_nm + `</td>
                        <td class="t_left">` + riskContent + `</td>
                        <td><button type="button" class="btn_confirm" ` + risk_result_msg + `</button></td>
                    </tr>
                `;
                
                $('.board_wrap tbody').append(riskData);
			}

			$('.t_center').html('현재 <span class="c_blue">총 ' + risk_check_count + '건의 장애</span> 중 <span class="c_pink">' + risk_result_count + '건의 처리</span>가 필요합니다.');
		}
	});
}

function riskStateChange(server_id, init_tm){
	if (confirm("처리가 완료되었습니까?") == true){
		$.ajax({
			url: '/whmsCont/updateRiskStateData.do',
			data: {server_id: server_id, init_tm: init_tm},
			success: function(data){
				alert("처리 완료");
			}
		});
	} else {
		riskStateData('desc');
	}
}

function checkedStateChange(){
	var checkedData = [];
	
	$('.each_checkBox:checked').each(function(){
		var value = $(this).val();
		var eachValue = value.split(",");

		checkedData.push(eachValue);
	});
	if (confirm("전체 완료 처리 하시겠습니까?") == true){
		if (checkedData.length > 0){
			var successUpdateCnt = 0;
			var totalUpdateCnt = checkedData.length;
			
			function isCheckAllUpdate(){
				successUpdateCnt++;
				if(successUpdateCnt == totalUpdateCnt){
					location.reload();
					alert("처리 완료");
				}
			}
			
			checkedData.forEach(function(eachValue) {
				$.ajax({
					url: '/whmsCont/updateRiskStateData.do',
					data: {server_id: eachValue[0], init_tm: eachValue[1]},
					success: function(data){
						isCheckAllUpdate();
					}
				});
			});
		} else {
			alert("선택된 항목이 없습니다.");
		}
	} else {
		riskStateData('desc');
	}
}

$(function() {
	$('.all_checkBox').on('change', function() {
		$('.each_checkBox').prop('checked', $(this).is(':checked'));
	});
	
	$(document).on('change', '.each_checkBox', function() {
		if ($('.each_checkBox:checked').length == $('.each_checkBox').length) {
			$('.all_checkBox').prop('checked', true);
		} else {
			$('.all_checkBox').prop('checked', false);
		}
	});
		
	riskStateData('desc');
});
</script>
<style>
	body {padding: 8%;}
	h3 {font-size: 1.34rem; color: rgba(255,255,255, .9); font-weight: 600; height: 40px; line-height: 30px; text-align: left;}
	.con.board {width: 50%; margin: 0 auto; height: 100%;}
	.all_checkBox {color: #FFFFFF; margin-right: 10px;}
	.board_top_con{display: flex; margin: 10px 25px; gap: 538px;}
</style>
<body>
	<div class="con board">
        <h3 class="t_center"></h3>
        <div class="board_top_con">
        	<div>
        		<input type="checkbox" class="all_checkBox" />
        		<label>전체 선택</label>
        	</div>
        	<button type="button" class="btn_confirm active" onclick="checkedStateChange();">전체 처리</button>
        </div>
        <div class="board_wrap">
        	<div class="board_none_span">
           		<span>이상 없음</span>
           	</div>
            <table>
                <colgroup>
                	<col style="width:5%">
                    <col style="width:15%">
                    <col style="width:15%">
                    <col style="width:45%">
                    <col style="width:auto">
                </colgroup>
                <tbody>
                </tbody>
            </table>
        </div>
    </div>
    <script src="/js/plugin/jquery/jquery.mCustomScrollbar.concat.min.js"></script>
    <script>
        $(".board_wrap").mCustomScrollbar({
            theme:"minimal"
        });
    </script>
</body>
</html>
