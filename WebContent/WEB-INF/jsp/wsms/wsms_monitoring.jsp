<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
    <style type="text/css"> @import url("/css/wsms.css");</style>
	<script src="/js/plugin/highcharts/highcharts.js"></script>
	<!--  <script src="/js/plugin/highcharts/data.js"></script> -->
	<title>WSMS - Wiseplus System Monitoring Server</title>
<script>

function selectSystemInfo() {
    $.ajax({
        url: '/wsmsCont/selectSystemInfo.do',
        success: function (data) {
            var html = "";
            
            for (var i = 0; i < data.length; i++) {
                var system_id = data[i].system_id;
                var system_nm = data[i].system_nm;
                var server_place = data[i].server_place;
                var system_url = data[i].system_url;
                var res_time = data[i].res_time;
                var return_res_time = data[i].return_res_time;
                var current_status = data[i].current_status;
                var val = data[i].val;

                // current_status 값을 기반으로 버튼 표시 조정
                var playButtonDisplay = current_status == 0 ? 'inline-block' : 'none';
                var pauseButtonDisplay = current_status == 1 ? 'inline-block' : 'none';

                html += '<div class="con" id="con_' + system_id + '">' +
                            '<div class="item">' +
                                '<div class="item_status">' +
                                    '<span class="status00" id="status_val"></span>' +
                                '</div>' +
                                '<div class="item_text">' +
                                    '<div class="tt">' + system_nm + '</div>' +
                                    '<div class="link">' +
                                        '<span>' + server_place + '</span>' +
                                        '<span><a href="' + system_url + '" target="_blank">' + system_url + '</a></span>' +
                                    '</div>' +
                                '</div>' +
                                '<div class="item_network">' +
                                    '<div class="network_con" id="network_con' + system_id + '"">' +
                                        '<div class="contents" id="container_' + system_id + '"></div>' +
                                    '</div>' +
                                    '<div class="network_response" id="network_response_' + system_id + '">' +
                                    '</div>' +
                                    '<div class="network_btn">' +
                                        '<a class="btn_play" style="display:' + playButtonDisplay + ';" data-system-id="' + system_id + '"><img src="/images/img_play.png" alt="play"></a>' +
                                        '<a class="btn_pause on" style="display:' + pauseButtonDisplay + ';" data-system-id="' + system_id + '"><img src="/images/img_pause.png" alt="pause"></a>' +
                                    '</div>' +
                                '</div>' +
                            '</div>' +
                        '</div>';
            }

            document.getElementById('contents').innerHTML = html;
            
         	// 각 시스템의 current_status가 1일 경우 자동으로 start 함수를 실행
            data.forEach(system => {
                if (system.current_status == 1) {
                    startLiveChart(system.system_id, system.req_time,return_res_time);
                }
            });
            
            $('.btn_play').on('click', function() {
                var system_id = $(this).data('system-id');
                var system = data.find(item => item.system_id === system_id);
                var req_time = system.req_time;

                var container = $(this).closest('.con');
                container.find('.btn_play').hide();
                container.find('.btn_pause').show().addClass('on');
                
                ControllShellScript(system_id);
                startLiveChart(system_id, req_time, return_res_time);
            });

            $('.btn_pause').on('click', function() {
                var system_id = $(this).data('system-id');
                var system = data.find(item => item.system_id === system_id);
                var val = system.val;
                
                var container = $(this).closest('.con');
                container.find('.btn_pause').hide();
                container.find('.btn_play').show();
                
                ControllShellScript(system_id);
                stopLiveChart(system_id);
                
                $('#con_' + system_id + ' #status_val').removeClass('status00 status01 status02 status03').addClass('status00');
            });
        },
        error: function (xhr, status, error) {
            console.error('데이터를 가져오는 중 오류 발생:', error);
        }
    });
}

function selectPlusTimeProc(req_time) {
	var minutes = (req_time * 60) + 60000;
	return minutes;
}

function selectAddTimeProc(req_time) {
	var minutes = (req_time * 60);
	return minutes;
}

var charts = {}; // 각 시스템의 차트를 저장할 객체

function stopLiveChart(system_id) {
	// 간격(interval)을 정리하고 삭제
    if (charts[system_id] && charts[system_id].intervalId) {
        clearInterval(charts[system_id].intervalId);
        delete charts[system_id].intervalId; 
    }

    /* 차트를 완전히 파괴하고 객체에서 삭제
    if (charts[system_id] && charts[system_id].chart) {
        charts[system_id].chart.destroy();
        delete charts[system_id].chart;
    }*/
    $('#network_con' + system_id).removeClass('active');
    $('#network_response_' + system_id).empty();
    //$('#con_' + system_id).removeClass('active');
}

var status_val = {};

function fetchLatestData(system_id) {
    $.ajax({
        url: '/wsmsCont/getLatestData.do?system_id=' + system_id,
        success: function (data) {
            var chartData = [];

            if (!status_val[system_id]) {
                status_val[system_id] = [];
            } else {
                status_val[system_id] = []; // 이전 데이터 초기화
            }
            
            for (var i = 0; i < data.length; i++) {
            	var sys_id = data[i].system_id;
                var init_tm = data[i].init_tm;
                var formattedTime = init_tm;
                var system_nm = data[i].system_nm;
                var req_time = data[i].req_time;
                var res_time = parseFloat(data[i].res_time);
                var return_res_time = parseFloat(data[i].return_res_time);
                
                var limit_center_line = res_time / 1000;
                var percent_res_time = return_res_time / 1000;
                
                var val = data[i].val;
                status_val[system_id].push(val);
                
                statusMain(status_val);
                
                chartData.push([init_tm, percent_res_time, limit_center_line, formattedTime]);
                
                var responseHTML = '<p class="tt">현재 응답속도</p>' +
                '<p class="text" id="' + system_id + '_return_res_time"><strong>'+ return_res_time.toFixed(2) +'ms</strong></p>';

				// 동적으로 추가
				document.getElementById('network_response_' + system_id).innerHTML = responseHTML;

            }
            
            if (charts[system_id] && charts[system_id].chart) {
            	
            	const chartSeries = charts[system_id].chart.series[0];
                const latestData = chartData[chartData.length - 1];
                
                chartSeries.addPoint([latestData[0], latestData[1], latestData[2], latestData[3]], true, chartSeries.data.length >= 60);
                
                const showPlotLine = latestData[1] >= limit_center_line * 0.8;
                charts[system_id].chart.update({
                    yAxis: {
                        plotLines: showPlotLine ? [{
                            color: 'red',
                            width: 2,
                            value: limit_center_line,
                            dashStyle: 'solid',
                            zIndex: -1
                        }] : []
                    },
                    tooltip: {
                        formatter: function() {
                            const point = this.point;
                            const initTime = point.category;
                            const res_text = point.options.y;
                            
                            var now = new Date();
                            
                            var month = now.getMonth() + 1;
                            month = month < 10 ? '0' + month : month; 
                            
                            var day = now.getDate();
                            day = day < 10 ? '0' + day : day;
                            
                            var hours = now.getHours();
                            hours = hours < 10 ? '0' + hours : hours;

                            var minutes = now.getMinutes();
                            minutes = minutes < 10 ? '0' + minutes : minutes;
                            
                            var hour = "";
                            var minute = "";
                            
                            const latestData = chartData.find(dataPoint => dataPoint[0] === point.category);
                            
                            /*if (typeof initTime === 'number') {
                                hour = point.options.name.substring(8, 10);
                                minute = point.options.name.substring(10, 12);
                            } else {
                            	hour = initTime.substring(8, 10);
                            	minute = initTime.substring(10, 12);
                            }*/

                            return '시간 : ' + hours + ':' + minutes + '<br>응답속도 : ' + (res_text * 1000).toFixed(2) + 'ms';
                            
                        }
                    },
                });
                
            } else {
            	const showPlotLine = percent_res_time >= limit_center_line * 0.8;
                charts[system_id].chart = Highcharts.chart('container_' + system_id, {
                	chart: {
                        type: 'line',
                        //spacing: [0, 0, 0, 0]
                        spacingRight: 0,
                        spacingLeft: 0,
                        marginLeft: -70 
                    },
                    title: {
                        text: ''
                    },
                    xAxis: {
                        type: 'datetime',
                        categories: chartData.map(point => point[0]),
                        labels: {
                            enabled: false
                        },
                        gridLineWidth: 0,
                        lineWidth: 0
                    },
                    yAxis: {
                        title: {
                            text: ''
                        },
                        min: 0,
                        max: Math.max(limit_center_line, Math.max(...chartData.map(point => point[1]))) * 1.2,
                        labels: {
                            enabled: false
                        },
                        gridLineWidth: 0,
                        plotLines: showPlotLine ? [{
                            color: 'red',
                            width: 2,
                            value: limit_center_line,
                            dashStyle: 'solid',
                            zIndex: -1
                        }] : []
                    },
                    series: [{
                        name: '',
                        data: null
                        //data: Array(60).fill(null)
                    }],
                    tooltip: {
                        formatter: function() {
                            const point = this.point;
                            const initTime = point.category;
                            const res_text = point.options.y;
                            
							var now = new Date();
                            
                            var month = now.getMonth() + 1;
                            month = month < 10 ? '0' + month : month; 
                            
                            var day = now.getDate();
                            day = day < 10 ? '0' + day : day;
                            
                            var hours = now.getHours();
                            hours = hours < 10 ? '0' + hours : hours;

                            var minutes = now.getMinutes();
                            minutes = minutes < 10 ? '0' + minutes : minutes;
                            
                            var hour = "";
                            var minute = "";
                            
                            const latestData = chartData.find(dataPoint => dataPoint[0] === point.category);
                            
                            /*if (typeof initTime === 'number') {
                                hour = point.options.name.substring(8, 10);
                                minute = point.options.name.substring(10, 12);
                            } else {
                            	hour = initTime.substring(8, 10);
                            	minute = initTime.substring(10, 12);
                            }*/

                            return '시간 : ' + hours + ':' + minutes + '<br>응답속도 : ' + (res_text * 1000).toFixed(2) + 'ms';
                            
                        }
                    },
                    legend: {
                        enabled: false
                    },
                    credits: {
                        enabled: false
                    },
                    plotOptions: {
                        series: {
                            marker: {
                                enabled: false
                            },
                            lineWidth: 3
                        }
                    }
                });
            }
        },
        error: function (xhr, status, error) {
            console.error('데이터 가져오기 오류:', error);
        }
    });
}

function statusMain(val) {
    var allStatusValues = [];
    for (var key in val) {
        if (val.hasOwnProperty(key)) {
            // 해당 시스템의 .btn_play 버튼이 활성 상태인지 확인
            var playButtonVisible = $('#con_' + key + ' .btn_play').css('display') == 'none';

            if (!playButtonVisible) {
                // .btn_play가 작동 중이지 않으면 해당 시스템을 건너뜀
                continue;
            }

            allStatusValues = allStatusValues.concat(val[key]);

            var systemStatus = val[key][0];
            
            var systemVal = val[key][0];
            
            var statusClass = "status00"; // 기본값 (정상)

            if (systemVal == 1) {
                statusClass = "status02"; // 경고
            } else if (systemVal == 2) {
                statusClass = "status03"; // 오류
            } else {
            	statusClass = "status01";
            }

            // 기존 클래스 제거 후 새로운 클래스 추가
            $('#con_' + key + ' #status_val')
                .removeClass('status00 status01 status02 status03') // 기존 클래스 제거
                .addClass(statusClass); // 새로운 클래스 추가
            
            if (systemStatus == '1') {
                $('#con_' + key).addClass('active'); // 문제가 있는 시스템에 active 클래스 추가
            } else if(systemStatus == '2') {
            	$('#con_' + key).addClass('active'); // 문제가 있는 시스템에 active 클래스 추가
            } else{
                $('#con_' + key).removeClass('active'); // 정상 시스템에서 active 클래스 제거
            }
        }
    }

    // 전체 시스템 상태 판단
    var hasIssue = allStatusValues.indexOf('1') !== -1 || allStatusValues.indexOf('2') !== -1; // 문제 있는 상태 확인
    var issueCount = 0;

    for (var i = 0; i < allStatusValues.length; i++) {
        if (allStatusValues[i] == '1' || allStatusValues[i] == '2') {
            issueCount++;
        }
    }

    if (hasIssue) {
        $('.alarm').addClass('active');
        $('.alarm').html('<p>' + getCurrentDateTime() + ' : <span>' + issueCount + '</span> 개의 시스템에 문제가 발생했습니다.</p>');
        
        var audio = new Audio('/sound/risk_audio.mp3');
        audio.play();
        
    } else {
        $('.alarm').removeClass('active');
        $('.alarm').html('<p>' + getCurrentDateTime() + ' : 전체 시스템이 정상입니다.</p>');
    }
}

function startLiveChart(system_id, req_time, return_res_time) {
	
	$('#network_con' + system_id).addClass('active'); 
	
    var plus_minute = selectPlusTimeProc(req_time);
    var issue_minute = selectAddTimeProc(req_time);
    var isFirstRun = true;

    if (charts[system_id] && charts[system_id].intervalId) {
        clearInterval(charts[system_id].intervalId);
    }
    
    //fetchLatestData(system_id);
    
    // 첫번째만 req_time + 1분 , 그이후로 req_time
    charts[system_id] = {
        intervalId: setInterval(function () {
        	
            fetchLatestData(system_id);

            if (isFirstRun) {
                isFirstRun = false; 
                clearInterval(charts[system_id].intervalId);
                
                charts[system_id].intervalId = setInterval(function () {
                    fetchLatestData(system_id);
                //}, issue_minute);
                }, 4000);
            }
        //}, plus_minute) 
        }, 5000) 
    };
}

function ControllShellScript(system_id) {
	
    var systemData = {
        system_id: system_id
    };

    $.ajax({
        url: '/wsmsCont/executeShellScript.do',
        type: 'POST',
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json',
        data: JSON.stringify(systemData),
        success: function(response) {
            //console.log('Shell script executed:', response);
        },
        error: function(xhr, status, error) {
            //console.error('Shell script 실행 중 오류 발생:', error);
        }
    });
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

function checkAllSystemsStatus() {
    var allSystemsStopped = true;

    // 모든 시스템이 정지상태일때
    $('.btn_play').each(function() {
        if ($(this).css('display') == 'none') {
            allSystemsStopped = false;
        }
    });

    if (allSystemsStopped) {
    	$('.alarm').removeClass('active');
        $('.alarm').html('<p>' + getCurrentDateTime() + ' : 전체 시스템이 정상입니다.</p>');
    }
}

$(function() {
    document.getElementById('menu1').classList.add('active');
    selectSystemInfo();
    checkAllSystemsStatus();
    
    setInterval(checkAllSystemsStatus, 500);

})

</script>
<body>
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
    <div id="contents">
    
    </div>
    <!-- contents -->
    <jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>