<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
    <style type="text/css">@import url("/css/whms.css");</style>
    <script src="/js/plugin/highcharts/highcharts.js"></script>
	<title>WHMS - Wiseplus Hardware Monitoring Server</title>
<script>
function updateCurrentDate() {
    $.ajax({
        url: '/whms/whms_monitoring.do',
        success: function(data) {
            $('.currentDateFormat').val($(data).filter('.currentDateFormat').val());
            $('.endDateFormat').val($(data).filter('.endDateFormat').val());
        }
    });
}

function totalData() {
    var currentDateFormat = $('.currentDateFormat').val();
    $.ajax({
        url: '/whmsCont/currentServerData.do',
        data: { currentDateFormat: currentDateFormat },
        success: function (data) {
            data.forEach(function (serverData, index) {
                var server_nm = serverData["server_nm"];
                var cpu_per = serverData["cpu_per"];
                var mem_per = serverData["mem_per"];
                var disk_per = serverData["disk_per"];
                var net_per = serverData["net_per"];
                var temp_val = serverData["temp_val"] || '-';
                var st_cpu_per = serverData["st_cpu_per"];
                var st_mem_per = serverData["st_mem_per"];
                var st_disk_per = serverData["st_disk_per"];
                var st_temp_val = serverData["st_temp_val"];
                var boot = serverData["boot"];
                var server_id = serverData["server_id"];

                function resetElements(server_nm) {
                    $('#' + server_nm + ' #cpu_per').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #mem_per').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #disk_per').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #disk_per_MySQL').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #disk_per_PSQL').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' .temp_wrap p').html(' ').removeClass('active caution');
                    $('#' + server_nm + ' .temp_wrap div').removeClass('active caution');
                }
                
                function setHeightAndClass(element, value, standardValue) {
                    element.animate({ height: value + "%" }, 1000);
                    if (value > standardValue) {
                        element.addClass('active');
                    } else if (value > (standardValue * 0.7)) {
                        element.addClass('caution');
                    }
                }
                
                function setTempAndClass(element, elementDiv){
                	if(temp_val == 'not_connection') {
                		elementDiv.removeClass('active caution');
                     	elementDiv.html('<p>-</p>');
                    } else {
                    	element.html(temp_val);
                    	
                    	 if (temp_val > st_temp_val) {
                    	 	elementDiv.addClass('active');
                         } /* else if (temp_val > (st_temp_val * 0.7)) {
                        	elementDiv.addClass('caution');
                         } */
                    }
                }
                
                resetElements(server_nm);
                
                if (cpu_per == 'not_connection' || mem_per == 'not_connection' || disk_per == 'not_connection' || net_per == 'not_connection') {
                	$('#' + server_nm + ' #cpu_per').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #mem_per').css('height', '0').removeClass('active caution');
                    $('#' + server_nm + ' #disk_per').css('height', '0').removeClass('active caution');
                    
                    $('#' + server_nm + ' .last_wrap p').html('-');
                    
                    if (temp_val == 'not_connection') {
                        $('#' + server_nm + ' .temp_wrap div').html('<p>-</p>');
                    } else if (temp_val > st_temp_val) {
                        $('#' + server_nm + ' .temp_wrap p').html(temp_val);
                        $('#' + server_nm + ' .temp_wrap div').addClass('active');
                    } else if (temp_val > (st_temp_val * 0.7)) {
                    	$('#' + server_nm + ' .temp_wrap p').html(temp_val);
                        $('#' + server_nm + ' .temp_wrap div').addClass('caution');
                	} else {
                        $('#' + server_nm + ' .temp_wrap p').html(temp_val);
                    }
                    
                    $('#networkChart_' + server_nm).html('<div class="network_con none"><p>not connection</p></div>');
                    
                } else {
                	if(server_nm == 'DB'){
                		var disk_per_split = disk_per.split('_');
                		var disk_per_MySQL = disk_per_split[0];
                		var disk_per_PSQL = disk_per_split[1];
	                        
                		setHeightAndClass($('#' + server_nm + ' #cpu_per'), cpu_per, st_cpu_per);
	                    setHeightAndClass($('#' + server_nm + ' #mem_per'), mem_per, st_mem_per);
	                    setHeightAndClass($('#' + server_nm + ' #disk_per_MySQL'), disk_per_MySQL, st_disk_per);
	                    setHeightAndClass($('#' + server_nm + ' #disk_per_PSQL'), disk_per_PSQL, st_disk_per);
	                    
	                    setTempAndClass($('#' + server_nm + ' .temp_wrap p'), $('#' + server_nm + ' .temp_wrap div'));
	                    
	                    networkData(server_id, '#networkChart_' + server_nm);
                	} else {
	                    setHeightAndClass($('#' + server_nm + ' #cpu_per'), cpu_per, st_cpu_per);
	                    setHeightAndClass($('#' + server_nm + ' #mem_per'), mem_per, st_mem_per);
	                    setHeightAndClass($('#' + server_nm + ' #disk_per'), disk_per, st_disk_per);
	                    
	                    setTempAndClass($('#' + server_nm + ' .temp_wrap p'), $('#' + server_nm + ' .temp_wrap div'));
	                    	
	                    networkData(server_id, '#networkChart_' + server_nm);
                	}
                }
            });
        }
    });
}

function networkData(server_id, chartContainer){
	$.ajax({
		url: '/whmsCont/networkStateData.do',
		data: {server_id: server_id},
		success: function(data){
			var netData = [];
			var yAxisData = [];
			var seriesData = [];
			var timeData = [];
			var graphCautionColor = getComputedStyle(document.documentElement).getPropertyValue('--color-yellow').trim();
			var graphStableColor = getComputedStyle(document.documentElement).getPropertyValue('--color-blue').trim();
			var graphPlotColor = getComputedStyle(document.documentElement).getPropertyValue('--color-pink').trim();

			for(var i=0; i < data.length; i++){
				var st_net_per = parseInt(data[i]["st_net_per"]);
				var init_tm = data[i]["init_tm"];
					timeData.push(init_tm.slice(8,10) + ':' + init_tm.slice(10,12));
				
				if(data[i]["net_per"] != undefined){
					var net_per = JSON.parse(data[i]["net_per"]);
					netData.push(net_per);
				}

			};

			var array_max_val = 0.0;
			var yaxis_max_val = 0.0;
					
			for (var j = 0; j < netData.length; j++) {
			    var array_val = netData[j];
			    if (array_max_val < array_val) {
			        array_max_val = array_val;
			    }
			}
			
			if (array_max_val > 50.0){
				yaxis_max_val = 105;
				
				var yAxisItem = {
					title: {
		                text: ''
		            },
		            labels: {
		                enabled: false
		            },
		            plotLines: [{
		                value: st_net_per,
		                color: graphPlotColor,
		                width: 1,
		                zIndex: 999
		            }],
		            gridLineWidth: 0,
		            max: yaxis_max_val,
		            min: 0
	            };
				
				var seriesItem = {
					name : '',
	                data : netData,
	                color : graphCautionColor,
	                marker: {
	                    enabled: false
	                },
	                showInLegend: false
				};
				
			} else {
				yaxis_max_val = (array_max_val * 2) || 10;
				
				var yAxisItem = {
					title: {
		                text: ''
		            },
		            labels: {
		                enabled: false
		            },
		            plotLines: [{
		                value: st_net_per,
		                color: graphPlotColor,
		                width: 1,
		                zIndex: 999
		            }],
		            gridLineWidth: 0,
		            max: yaxis_max_val,
		            min: 0
	            };
				
				var seriesItem = {
					name : '',
	                data : netData,
	                color : graphStableColor,
	                marker: {
	                    enabled: false
	                },
	                showInLegend: false
				};
			}
			
			yAxisData.push(yAxisItem);
			seriesData.push(seriesItem);
			$(chartContainer).highcharts({
	            title: {
	                text: ''
	            },
	                
	            series: seriesData,
	            
	            credits: {
	                enabled: false
	            },
	                
	            yAxis: yAxisData,
	            
	            xAxis: [{
	                title: {
	                    text: ''
	                },
	                labels: {
	                    enabled: false
	                }
	            }],
	            tooltip: {
	            	formatter: function(){
	            		var index = this.point.index;
	            		var value = this.y;
	            		var currentTime = timeData[index];            		
	            		return currentTime + '<br /><b>' + value + '%</b>';
	            	}
	            },
	            chart: {
	                type: 'area',
	                margin: 0,
	                backgroundColor: null
	            }
			});
		}
	});
}

function bootData(){
	var currentDateFormat = $('.currentDateFormat').val();
    $.ajax({
        url: '/whmsCont/currentServerData.do',
        data: { currentDateFormat: currentDateFormat },
        success: function (data) {
            data.forEach(function (serverData, index) {
            	var server_nm = serverData["server_nm"];
                var cpu_per = serverData["cpu_per"];
                var mem_per = serverData["mem_per"];
                var disk_per = serverData["disk_per"];
                var net_per = serverData["net_per"];
                var temp_val = serverData["temp_val"] || '-';
                var boot = serverData["boot"];
                var server_id = serverData["server_id"];

                function resetElements(server_nm) {
                    $('#' + server_nm + ' .last_wrap p').html(' ');
                }

                resetElements(server_nm);
                
                if (cpu_per == 'not_connection' || mem_per == 'not_connection' || disk_per == 'not_connection' || serverData["net_per"] == 'not_connection') {
                    $('#' + server_nm + ' .last_wrap p').html('-');
                } else {
                    $('#' + server_nm + ' .last_wrap p').html(boot);
                }
            });
        }
    });
}

function createRankData(xAxisTitle, chartType, containerId) {
    var currentDateFormat = $('.currentDateFormat').val();
    $.ajax({
        url: '/whmsCont/rankData.do',
        data: { chartType: chartType, currentDateFormat: currentDateFormat },
        success: function(data) {
            $(containerId).empty();
            var content = `
                <h4>` + xAxisTitle + `</h4>
                <div class="item_wrap"></div>
            `;
            
            $(containerId).append(content);
            
            var itemWrap = $(containerId).find('.item_wrap');
            
            for (var i = 0; i < data.length; i++) {
                if (data[i][chartType] !== undefined) {
                    var value = JSON.parse(data[i][chartType]);
                    var serverName = data[i].server_nm.toLowerCase();
                    var date = data[i].date;
                    var rankFormattedDate = date.slice(4,6) + '월 ' + date.slice(6,8) + '일';
                    
                    if (chartType == 'temp_val') {
                    	var rankData = `<div class="item"><div class="` + serverName + `" style="width: ` + value + `%"></div><span>` + value.toFixed(1) + `°C</span></div>`;
                    } else {
                    	var rankData = `<div class="item"><div class="` + serverName + `" style="width: ` + value + `%"></div><span>` + value.toFixed(1) + `%</span></div>`;
                    }
                    
                    itemWrap.append(rankData);
                }
            }
            /* $('.con.used h3').html('<p>전일 사용량 랭크 <span class="c_blue">(' + rankFormattedDate + ')</span></p>'); */
        }
    });
}

function cpuRankData(){
	createRankData('CPU', 'cpu_per', '#cpuRank');
}

function memRankData(){
	createRankData('MEM', 'mem_per', '#memRank');
}

function diskRankData(){
	createRankData('DISK', 'disk_per', '#diskRank');
}

function tempRankData(){
	createRankData('TEMP', 'temp_val', '#tempRank');
}

function riskCheckData(riskCheckDay, containerId) {
	var currentDateFormat = $('.currentDateFormat').val();
		currentDateFormat = currentDateFormat.slice(0,10) + ' 00:00'
	var endDateFormat = $('.endDateFormat').val();
		endDateFormat = endDateFormat.slice(0,8) + '0000';
    $.ajax({
        url: '/whmsCont/riskCheckData.do',
        data: {riskCheckDay: riskCheckDay, endDateFormat: endDateFormat, currentDateFormat: currentDateFormat},
        success: function (data) {
            var serverNames = [];
            var dateData = [];
            var serverData = {};
            var serverTextData={};
            
            var array_max_val = 0.0;
            var yaxis_max_val = 0.0;
            
            var serverColor = {
            		'RND' : getComputedStyle(document.documentElement).getPropertyValue('--color-rnd').trim(), 
            		'KHNP' : getComputedStyle(document.documentElement).getPropertyValue('--color-khnp').trim(), 
            		'DB': getComputedStyle(document.documentElement).getPropertyValue('--color-db').trim(), 
            		'WORK' : getComputedStyle(document.documentElement).getPropertyValue('--color-work').trim(), 
            		'HIS' : getComputedStyle(document.documentElement).getPropertyValue('--color-his').trim(), 
            		'GIS' : getComputedStyle(document.documentElement).getPropertyValue('--color-gis').trim()
            };

            var dateDisplayData = [];
            for (var i = 0; i < data.length; i++) {
                var serverName = data[i].server_nm;
                var color = serverColor[serverName];
                var riskCheckCount = data[i].risk_checkCount;
                var riskCheckText = data[i].risk_checkCount;
                var date = data[i].date;
                var dateFormatted = new Date(date.slice(0, 4) + '-' + date.slice(4, 6) + '-' + date.slice(6, 8) + ' ' + date.slice(8, 10) + ':' + date.slice(10, 12));
                var formattedDate = (dateFormatted.getMonth() + 1).toString().padStart(2, '0') + '.' + dateFormatted.getDate().toString().padStart(2, '0');
                
                if (!serverNames.includes(serverName)) {
                    serverNames.push(serverName);
                    serverData[serverName] = {};
                    serverTextData[serverName] = {};
                }
                
                if (!dateData.includes(formattedDate)) {
                    dateData.push(formattedDate);
                }
                
                /* if (riskCheckCount > 50){
                	riskCheckCount = 50 + (riskCheckCount * 0.01);
                } */
                serverData[serverName][formattedDate] = riskCheckCount;
                serverTextData[serverName][formattedDate] = riskCheckText;
                
                if (riskCheckCount > array_max_val) {
                    array_max_val = riskCheckCount;
                }
                
                yaxis_max_val = (array_max_val * 1.2) || 50;
            }
            
            if (riskCheckDay == '29') {
                var xAxisData = { 
                    title: {
                        text: ''
                    },
                    categories: dateData,
                    labels: {
                        step: 1,
                        formatter: function() {
                            if (this.pos == dateData.length - 1) {
                                return this.value;
                            }
                            return (this.pos % 3 == 0) ? this.value : null;
                        }
                    }
                };
            } else {
            	var xAxisData = {
	                title: {
	                    text: ''
	                },
	                categories: dateData
	            };
            }
            
            for (var i = 0; i < serverNames.length; i++) {
                var serverName = serverNames[i];
                var dataArray = [];
                var textArray = [];

                for (var j = 0; j < dateData.length; j++) {
                    var date = dateData[j];
                    if (serverData[serverName][date] !== undefined) {
                        dataArray.push(serverData[serverName][date]);
                        textArray.push(serverTextData[serverName][date]);
                    } else {
                        dataArray.push(null);
                        textArray.push('0');
                    }
                }
                serverData[serverName] = dataArray;
                serverTextData[serverName] = textArray;
            }

            var seriesData = [];
            
            for (var j = 0; j < serverNames.length; j++) {
                var serverName = serverNames[j];
                var seriesItem = {
                    name: serverName,
                    data: serverData[serverName],
                    color: serverColor[serverName],
                    marker: {
                        enabled: false
                    },
                    showInLegend: false,
                    dataLabels: {
                    	color: '#FFFFFF',
                    	enabled: true,
                    	formatter: function () {
                            var index = this.point.index;
                            var currentServerName = this.series.name;
                            var currentDate = dateData[index];
                            var text = serverTextData[currentServerName][index];
                            if (text != '0') {
                                return '<span style="text-shadow: 0 1px rgba(0, 0, 0, 1)">' + text + '</span>';
                            }
                            return null;
                        },
	                    style: {
	                        fontSize: '0.7rem',
	                        fontWeight: '600',
	                        textOutline: 'none'
	                    }
                    }
                };
                seriesData.push(seriesItem);
            }

            var riskCheckHighchart = {
                title: {
                    text: ''
                },
                series: seriesData,
                credits: {
                    enabled: false
                },
                yAxis: [{
                    title: {
                        text: ''
                    },
                    /* gridLineWidth: 0, */
                    gridLineColor: '#666666',
                    min: 0,
                    max: yaxis_max_val,
                    labels: {
                        enabled: true,
                        formatter: function(){
                            var value = this.value; // 현재 값을 가져옵니다.
                            var sliceValue = value.toString().slice(0, 2);
                            
                            if (value > 999){
                            	value = sliceValue + '00';
                            }
                            
                            return value;
                        }
                    }
                }],
                xAxis: xAxisData,
                tooltip: {
                    formatter: function () {
                        var index = this.point.index;
                        var currentServerName = this.series.name;
                        var currentDate = this.x;
                        var text = serverTextData[currentServerName][index];
                        var color = this.series.color;
                        
                        return currentDate + '<br/>' + 
                               '<span style="color:' + color + '">\u25CF</span> ' + currentServerName + ' : <b>' + text + '건</b>';
                    }
                },
                chart: {
                    type: 'line',
                    backgroundColor: null
                }
            };
            
            $(containerId).highcharts(riskCheckHighchart);
        }
    });
}

function tenDaysRiskCheckData(){
	riskCheckData('9', '.error_day10 .error_wrap');
}

function thirtyDaysRiskCheckData(){
	riskCheckData('29', '.error_day30 .error_wrap');
}

function playAudio(){
	var audio = new Audio('/sound/risk_audio.mp3');
    audio.currentTime = 0;
    audio.play();
}

var latestInitTm;

function riskStateData(riskStateOrder){
	$.ajax({
		url: '/whmsCont/riskStateData.do',
		data: {riskStateOrder: riskStateOrder},
		success: function(data){
            $('.board_wrap tbody').empty();
            $('.t_center').empty();
            
            if (!data || data.length == 0) {
            	$('.board_wrap table').hide();
                $('.board_none_span').show();
                $('.board_wrap').addClass('board_none');
                $('.t_center').html('현재 <span class="c_blue">총 0건의 장애</span> 중 <span class="c_pink">0건의 처리</span>가 필요합니다.');
                
                topBoardData('asc');
            } else {
                $('.board_wrap').removeClass('board_none');
                $('.board_none_span').hide();
                $('.board_wrap table').show();
            
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
	          		
	          		if (riskTimeGapDay > 1){
	          			riskTimeGap = Math.floor(riskTimeGapDay) + '일 전';
	          		}
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
	                	risk_result_msg = 'disabled>처리완료'
	                	risk_active = '>'
	                } else {
	                	risk_result_msg = 'onClick="riskStateChange(' + server_id + ',' + init_tm + ')"> 처리요망'
	                	risk_active = ' class="active">'
	                }
	                
	                var riskData = `
	                    <tr` +risk_active+ `
	                		<td>` + riskTimeGap + `</td>
	                        <td>` + server_nm + `</td>
	                        <td class="t_left">` + riskContent + `</td>
	                        <td><button type="button" class="btn_confirm" ` + risk_result_msg + `</button></td>
	                    </tr>
	                `;
	                
	                $('.board_wrap tbody').append(riskData);
	
	                if (risk_result == '1') {
	                    var currentInitTm = new Date();
	
	                    if (latestInitTm) {
	                        var minutesGap = (currentInitTm - latestInitTm) / (1000 * 60);
	
	                        if (minutesGap > 10) {
	                            playAudio();
	                            latestInitTm = currentInitTm;
	                        }
	                    } else {
	                        playAudio();
	                        latestInitTm = currentInitTm;
	                    }
	                }
				}
	
				$('.t_center').html('현재 <span class="c_blue">총 ' + risk_check_count + '건의 장애</span> 중 <span class="c_pink">' + risk_result_count + '건의 처리</span>가 필요합니다.');
				
				topBoardData('asc');
            }
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

function topBoardData(riskStateOrder){
    $.ajax({
        url: '/whmsCont/riskStateData.do',
        data: {riskStateOrder: riskStateOrder},
        success: function(data){
            var serverGroups = [];
            var noRiskDate = new Date();
            	noRiskDate.setMinutes(noRiskDate.getMinutes() - 1);
            var noRiskFormattedDate = noRiskDate.getFullYear().toString() + '-' +(noRiskDate.getMonth() + 1).toString().padStart(2, '0') + '-' + noRiskDate.getDate().toString().padStart(2, '0') + ' ' + noRiskDate.getHours().toString().padStart(2, '0') + ':' + noRiskDate.getMinutes().toString().padStart(2, '0');

            if (!data || data.length == 0) {
            	$('.con_wrap .con').removeClass('server_active');
            	$('.alarm').html('<p>[' + noRiskFormattedDate + '] 전체 서버가 정상입니다.</p>');
            	$('.alarm').removeClass('active');
            	return;
            }
            
            for(var i = 0; i < data.length; i++){
                var server_nm = data[i]["server_nm"];
                var lowerServerName = server_nm.toLowerCase();
                var risk_result = data[i]["risk_result"];
                var risk_check = data[i]["risk_check"];
                var init_tm = data[i]["init_tm"];
                var date = new Date(data[i].date);
                var formattedDate = date.getFullYear().toString() + '-' +(date.getMonth() + 1).toString().padStart(2, '0') + '-' + date.getDate().toString().padStart(2, '0') + ' ' + date.getHours().toString().padStart(2, '0') + ':' + date.getMinutes().toString().padStart(2, '0');
                var currentTime = new Date();
                	currentTime.setMinutes(currentTime.getMinutes() - 1);
                var formattedCurrentDate = currentTime.getFullYear().toString() + '-' +(currentTime.getMonth() + 1).toString().padStart(2, '0') + '-' + currentTime.getDate().toString().padStart(2, '0') + ' ' + currentTime.getHours().toString().padStart(2, '0') + ':' + currentTime.getMinutes().toString().padStart(2, '0');

                if(risk_check == 1 && risk_result == 1){
                    var existingGroup = null;

                    for (var j = 0; j < serverGroups.length; j++) {
                        if (serverGroups[j].init_tm == init_tm) {
                            existingGroup = serverGroups[j];
                            break;
                        }
                    }

                    if (existingGroup) {
                        existingGroup.servers.push(server_nm);
                    } else {
                        serverGroups.push({
                            init_tm: init_tm,
                            servers: [server_nm]
                        });
                    }
                }
            }
            $('.alarm').empty();
            
            if(serverGroups.length > 0){
                for (var k = 0; k < serverGroups.length; k++) {
                    var group = serverGroups[k];
                    $('.con_wrap .con').removeClass('server_active');
                    if(group.servers.length > 0){
                        for (var l = 0; l < group.servers.length; l++) {
                            var server_nm = group.servers[l];
                            $('.con_wrap #' + server_nm).addClass('server_active');
                        }
                        var serverList = group.servers.map(function(server_nm) {
                            return '<span class="c_' + server_nm.toLowerCase() + '">' + server_nm + '</span>';
                        }).join('<span>, </span>');
                        
                        var alarmServerList =  group.servers.map(function(server_nm) {
                            return server_nm;
                        }).join(', ');
                        
                        $('.alarm').html('<p>[' + formattedDate + '] ' + serverList + ' 서버에 문제가 발생했습니다.</p>');
                        $('.alarm').addClass('active');
                    }
                }
            } else {
            	$('.con_wrap .con').removeClass('server_active');
            	$('.alarm').html('<p>[' + formattedCurrentDate + '] 전체 서버가 정상입니다.</p>');
            	$('.alarm').removeClass('active');
            }
        }
    });
}

function selectWmsRemoteSessions(){
    $.ajax({
        url: '/whmsCont/selectWmsRemoteSessions.do',
        success: function(data){
            let userLabel, userName, startedAt, endedAt, status, hostName, createdAt, updatedAt;

            data.forEach(item => {
                userLabel = item.userLabel;
                userName = item.userName;
                startedAt = item.startedAt;
                endedAt = item.endedAt;
                status = item.status;
                hostName = item.hostName;
                createdAt = item.createdAt;
                updatedAt = item.updatedAt;

                const startedDt = new Date(startedAt);
                const displayStartedDt = startedDt.getHours().toString().padStart(2, '0') + ':' + startedDt.getMinutes().toString().padStart(2, '0');

                const usingTm = new Date() - startedDt;
                const usingHour = usingTm / (1000 * 60 * 60);
                const usingMin = usingTm / (1000 * 60);
                let displayUsingTm;

                if (usingHour > 1) {
                    displayUsingTm = '<p>' + Math.floor(usingHour) + '</p><small> HOUR</small>';
                } else if (usingMin > 0) {
                    if (usingMin < 1) {
                        displayUsingTm = '<p>0</p><small> MIN</small>';
                    } else {
                        displayUsingTm = '<p>' + Math.floor(usingMin) + '</p><small> MIN</small>';
                    }
                }
                
                let userIp;
                
                if (userName === '강태훈') {userIp = '192.168.0.60';}
                if (userName === '길경혜') {userIp = '192.168.0.213';}
                if (userName === '김민수') {userIp = '192.168.0.204';}
                if (userName === '최준규') {userIp = '192.168.0.161';}
                if (userName === '김지영') {userIp = '192.168.0.27';}

                if (status.toLowerCase() === 'start') {
                    $('.status-indicator').addClass('active');
                    
                    $('.con#device .c_device').addClass('c_gis');
                    $('.con#device .status-badge').addClass('active');
                    $('.con#device .status-badge .blink').text('');
                    $('.con#device .status-badge').html('<span class="blink"></span> 사용 중');
                    $('.con#device .user-name').addClass('active').html(userName + ' <small style="font-size: .7rem;">(' + userIp + ')</small>');
                    $('.con#device .device-name').addClass('active').text(hostName);

                    $('.con#device .server_con_wrap .server_temp .temp_wrap div').html('<p>' + displayStartedDt + '</p><small> ~</small>'); 
                    $('.con#device .server_con_wrap .server_temp .last_wrap div').html(displayUsingTm);
                } else {
                    $('.status-indicator').removeClass('active');
                    
                    $('.con#device .c_device').removeClass('c_gis');
                    $('.con#device .status-badge').removeClass('active');
                    $('.con#device .status-badge').html('<span class="blink"></span> 미사용');
                    $('.con#device .user-name').removeClass('active').text('No Session');
                    $('.con#device .device-name').removeClass('active').text(' ');

                    $('.con#device .server_con_wrap .server_temp .temp_wrap div').html('<p style="color: rgba(255, 255, 255, .5);">-</p>');
                    $('.con#device .server_con_wrap .server_temp .last_wrap div').html('<p style="color: rgba(255, 255, 255, .5);">-</p>');
                }
            });
        }
    });
}

function currentHour(){
	var currentTime = new Date();                
	var formattedCurrentDate = currentTime.getFullYear().toString() + '년 ' +(currentTime.getMonth() + 1).toString().padStart(2, '0') + '월 ' + currentTime.getDate().toString().padStart(2, '0') + '일 ' + currentTime.getHours().toString().padStart(2, '0') + '시 ' + currentTime.getMinutes().toString().padStart(2, '0') + '분 ' + currentTime.getSeconds().toString().padStart(2, '0') + '초';

	$('#wrap header .time').empty();
	$('#wrap header .time').append('<span>현재 시간 : </span>' + formattedCurrentDate);
}

//1일 간격으로 09시에 실행
function scheduleDailyTask() {
    var now = new Date();
    var targetTime = new Date();
    
    targetTime.setHours(9);
    targetTime.setMinutes(0);
    targetTime.setSeconds(0);
    targetTime.setMilliseconds(0);

    if (now > targetTime) {
        targetTime.setDate(targetTime.getDate() + 1);
    }
    
    var nextTimeTarget = targetTime - now;
    
    setTimeout(function() {
   		bootData();
        tempRankData();
        diskRankData();
        memRankData();
        cpuRankData();
        tenDaysRiskCheckData();
        thirtyDaysRiskCheckData();
        
        setInterval(function() {
            bootData();
            tempRankData();
            diskRankData();
            memRankData();
            cpuRankData();
            tenDaysRiskCheckData();
            thirtyDaysRiskCheckData();
        }, 24 * 60 * 60 * 1000);
        
    }, nextTimeTarget);
}

//1분 간격으로 00시 00분부터 실행
function scheduleHourlyTask() {
    var now = new Date();
    var targetTime = new Date();
    
    targetTime.setHours(0);
    targetTime.setMinutes(0);
    targetTime.setSeconds(1);
    targetTime.setMilliseconds(0);

    if (now > targetTime) {
    	targetTime.setHours(now.getHours());
    	targetTime.setMinutes(now.getMinutes() + 1);
    }
    
    var nextTimeTarget = (targetTime - now)/*  + (15 * 1000) */;
    
    setTimeout(function() {
    	
        updateCurrentDate();
        totalData();
        riskStateData('desc');
        selectWmsRemoteSessions();
        
        setInterval(function() {
            updateCurrentDate();
            totalData();
            riskStateData('desc');
            selectWmsRemoteSessions();
        }, 60 * 1000);
        
    }, nextTimeTarget);
}

$(function() {
    updateCurrentDate();
    totalData();
    bootData();
    tempRankData();
    diskRankData();
    memRankData();
    cpuRankData();
    tenDaysRiskCheckData();
    thirtyDaysRiskCheckData();
    riskStateData('desc');
    selectWmsRemoteSessions();
    
    currentHour();
    scheduleDailyTask();
    scheduleHourlyTask();
    
    // 1초 간격 실행 (현재 시간 표시)
    setInterval(function() {
        currentHour();
    }, 1000);
});

</script>
    <header>
        <h1><img src="/images/img_whms.svg" alt="">
            <div>
            	<a href="/whms/whms_monitoring.do">
                 <p>WHMS</p>
                 <small>Wiseplus Hardware Monitoring Server</small>
             </a>
            </div>
        </h1>
        <div class="alarm"></div>
        <div class="time"></div>
        <div class="speakerContorller"></div>
    </header>
    <div id="contents">
        <div class="server_wrap">
            <h3>&nbsp;</h3>
            <div class="label">
                <ul>
                    <li class="rnd">RND</li>
                    <li class="khnp">KHNP</li>
                    <li class="db">DB</li>
                    <li class="work">WORK</li>
                    <li class="his">HIS</li>
                    <!-- <li class="gis">GIS</li> -->
                </ul>                    
            </div><!-- 
            <div class="notice_wrap">
            	<h3>전일 서버 현황<span></span></h3>
            	<div class="notice_con">
            	</div>
            </div> -->
            <!-- 서버현황 //-->
            <div class="con_wrap">
                <!-- RND -->
                <div class="con" id="RND">
                    <h3 class="c_rnd">RND</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap">
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="cpu_per"></div></div>
                                    <label>CPU</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="mem_per"></div></div>
                                    <label>MEM</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="disk_per"></div></div>
                                    <label>DISK</label>
                                </div>
                            </div>
                            <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_RND"></div>
                            </div>
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>TEMP</h4>
                                <div>
                                    <p></p>
                                    <small>°C</small>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>LAST</h4>
                                <div>
                                    <p></p>
                                    <small>days</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- GIS -->
                <div class="con" id="KHNP">
                    <h3 class="c_khnp">KHNP</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap">
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="cpu_per"></div></div>
                                    <label>CPU</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="mem_per"></div></div>
                                    <label>MEM</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="disk_per"></div></div>
                                    <label>DISK</label>
                                </div>
                            </div>
                            <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_KHNP"></div>
                            </div>
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>TEMP</h4>
                                <div>
                                    <p></p>
                                    <small>°C</small>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>LAST</h4>
                                <div>
                                    <p></p>
                                    <small>days</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- DB -->
                <div class="con" id="DB">
                    <h3 class="c_db">DB</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap">
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="cpu_per"></div></div>
                                    <label>CPU</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="mem_per"></div></div>
                                    <label>MEM</label>
                                </div>
                                
                                <div class="range_con range_row">
                                    <div>
                                        <div class="range"><div class="range_h" id="disk_per_MySQL"></div></div>
                                        <label>mysql</label>
                                    </div>
                                    <div>
                                        <div class="range"><div class="range_h"  id="disk_per_PSQL"></div></div>
                                        <label>psql</label>
                                    </div>
                                </div>
                            </div>
                            <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_DB"></div>
                            </div>
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>TEMP</h4>
                                <div>
                                    <p></p>
                                    <small>°C</small>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>LAST</h4>
                                <div>
                                    <p></p>
                                    <small>days</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- WORK -->
                <div class="con" id="WORK">
                    <h3 class="c_work">WORK</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap">
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="cpu_per"></div></div>
                                    <label>CPU</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="mem_per"></div></div>
                                    <label>MEM</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="disk_per"></div></div>
                                    <label>DISK</label>
                                </div>
                            </div>
                            <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_WORK"></div>
                            </div>
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>TEMP</h4>
                                <div>
                                    <p></p>
                                    <small>°C</small>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>LAST</h4>
                                <div>
                                    <p></p>
                                    <small>days</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- HIS -->
                <div class="con" id="HIS">
                    <h3 class="c_his">HIS</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap">
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="cpu_per"></div></div>
                                    <label>CPU</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="mem_per"></div></div>
                                    <label>MEM</label>
                                </div>
                                <div class="range_con">
                                    <div class="range"><div class="range_h" id="disk_per"></div></div>
                                    <label>DISK</label>
                                </div>
                            </div>
                            <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_HIS"></div>
                            </div>
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>TEMP</h4>
                                <div>
                                    <p></p>
                                    <small>°C</small>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>LAST</h4>
                                <div>
                                    <p></p>
                                    <small>days</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- GIS -->
                <div class="con" id="device">
                    <h3 class="c_device c_gis">DEVICE</h3>
                    <div class="server_con_wrap">
                        <div class="server_range">
                            <div class="range_wrap border_wrap" style="width: 100%;">
                            	<p class="status-indicator">
									<span class="status-badge">
										<span class="blink"></span>
									</span>
								</p>
								<span class="user-info">
									<span class="user-text">
										<span class="user-name"></span>
										<span class="device-name"></span>
									</span>
								</span>
                            </div>
                            <!-- <div class="network_wrap border_wrap">
                                <h4>Network</h4>
                                <div class="network_con" id="networkChart_GIS"></div>
                            </div> -->
                        </div>
                        <div class="server_temp">
                            <div class="temp_wrap border_wrap">
                                <h4>START</h4>
                                <div>
                                </div>
                            </div>
                            <div class="last_wrap border_wrap">
                                <h4>ELAPSED</h4>
                                <div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <!--// 서버현황 -->
        </div>
        <div class="result_wrap">
            <!-- 전일 사용량 랭크 //-->
            <div class="con used">
                <h3>전일 사용량 랭크</h3>
                <div class="used_wrap">
                    <div class="con_used" id="cpuRank">
                    </div>
                    <div class="con_used" id="memRank">
                    </div>
                    <div class="con_used" id="diskRank">
                    </div>
                    <div class="con_used" id="tempRank">
                    </div>
                </div>
            </div>
            <!--// 전일 사용량 랭크 -->
            <!-- 장애건수 추이 //-->
            <div class="con error">
                <div class="error_day10"><h3>최근 10일 장애 현황(건)</h3><div class="error_wrap"></div></div>
                <div class="error_day30"><h3>최근 30일 장애 현황(건)</h3><div class="error_wrap"></div></div>
            </div>
            <!--// 장애건수 추이 -->
            <!-- 장애처리 게시판 //-->
            <div class="con board">
                <h3 class="t_center"></h3>
                <div class="board_wrap">
                	<div class="board_none_span">
                		<span>이상 없음</span>
                	</div>
                    <table>
                        <colgroup>
                            <col style="width:18%">
                            <col style="width:18%">
                            <col style="width:auto">
                            <col style="width:18%">
                        </colgroup>
                        <tbody>
                        </tbody>
                    </table>
                </div>
            </div>
            <!--// 장애처리 게시판 -->
        </div>
    </div>
    <!-- contents -->
    <jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>
    <script src="/js/plugin/jquery/jquery.mCustomScrollbar.concat.min.js"></script>
    <script>
        $(".board_wrap").mCustomScrollbar({
            theme:"minimal"
        });
    </script>

