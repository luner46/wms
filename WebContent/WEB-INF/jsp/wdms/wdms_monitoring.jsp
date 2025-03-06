<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
	<style type="text/css">@import url("/css/wdms.css");</style>
	<title>WDMS - Wiseplus Data Mornitoring System</title>
<script>
function dateSelect(selectedDate) {
	var selectedDateFormat = new Date(selectedDate);
    var boardTime = selectedDateFormat.getFullYear().toString() + '-' + (selectedDateFormat.getMonth() + 1).toString().padStart(2, '0') + '-' + selectedDateFormat.getDate().toString().padStart(2, '0') + ' ' + selectedDateFormat.getHours().toString().padStart(2, '0') + ':' + selectedDateFormat.getMinutes().toString().padStart(2, '0');
    var currentTime = new Date();
    var dayParam = '';

    var startOfToday = new Date(currentTime);
    startOfToday.setHours(0, 0, 0, 0);

    var startOfYesterday = new Date(startOfToday);
    startOfYesterday.setDate(startOfToday.getDate() - 1);

    // 과거 데이터
    if (selectedDateFormat < startOfYesterday) {
        var pastDate = new Date(selectedDateFormat);
        pastDate.setHours(23, 0, 0);
        dayParam = 'p';
        boardTime = pastDate.getFullYear().toString() + '-' + (pastDate.getMonth() + 1).toString().padStart(2, '0') + '-' + pastDate.getDate().toString().padStart(2, '0') + ' ' + pastDate.getHours().toString().padStart(2, '0') + ':' + pastDate.getMinutes().toString().padStart(2, '0');
    
    // 전일 데이터
    } else if (selectedDateFormat >= startOfYesterday && selectedDateFormat < startOfToday) {
        var ydayDate = new Date(selectedDateFormat);
        ydayDate.setHours(currentTime.getHours(), 0, 0);
        dayParam = 'y';
        boardTime = ydayDate.getFullYear().toString() + '-' + (ydayDate.getMonth() + 1).toString().padStart(2, '0') + '-' + ydayDate.getDate().toString().padStart(2, '0') + ' ' + ydayDate.getHours().toString().padStart(2, '0') + ':' + ydayDate.getMinutes().toString().padStart(2, '0');
    
    // 당일 데이터
    } else if (selectedDateFormat >= startOfToday) {
    	if (currentTime.getMinutes() < 1) {
    		currentTime.setHours(currentTime.getHours() - 1);
    	}
    	currentTime.setMinutes(0);
        dayParam = 't';
        boardTime = currentTime.getFullYear().toString() + '-' + (currentTime.getMonth() + 1).toString().padStart(2, '0') + '-' + currentTime.getDate().toString().padStart(2, '0') + ' ' + currentTime.getHours().toString().padStart(2, '0') + ':' + currentTime.getMinutes().toString().padStart(2, '0');
    }

    return [boardTime, dayParam];
}

function createCalendar(mm, yy) {
    var date = new Date();
    var currentYear = yy ? parseInt(yy, 10) : date.getFullYear();
    var currentMonth = mm ? parseInt(mm, 10) : date.getMonth() + 1;
    var prevMonth = currentMonth - 1;
    var prevYear = currentYear;
	    if (prevMonth < 1) {
	        prevMonth = 12;
	        prevYear -= 1;
	    }
    var nextMonth = currentMonth + 1;
    var nextYear = currentYear;
	    if (nextMonth > 12) {
	        nextMonth = 1;
	        nextYear += 1;
	    }
    var dd = date.getDate();

    var firstDay = new Date(currentYear, currentMonth - 1, 1).getDay();
    var firstDate = new Date(currentYear, currentMonth - 1, 1).getDate();
    var lastDate = new Date(currentYear, currentMonth, 0).getDate();
    var calendarHeader = '<ul class="calendar head"><li class="sun">Sun</li><li>Mon</li><li>Tue</li><li>Wed</li><li>Thu</li><li>Fri</li><li class="sat">Sat</li></ul>';
    var calendarBody = "";

    var dayCount = 1;

    for (var i = 0; i < 6; i++) {
        var week = '<ul class="calendar cnt">';
        for (var j = 0; j < 7; j++) {
            if ((i == 0 && j < firstDay) || (dayCount > lastDate)) {
                // 날짜 없는 데이터
                week += '<li></li>';
            } else {
                // 오늘 데이터
                if (dayCount == dd && mm == (date.getMonth() + 1) && currentYear == date.getFullYear()) {
                    week += '<li class="today day' + dayCount.toString().padStart(2, '0') + '" onclick="openBoard(' + currentYear + ', ' + currentMonth + ', ' + dayCount.toString().padStart(2, '0') + ');"><span class="date">' + dayCount.toString().padStart(2, '0') + '</span><ul class="list"><li class="li_khnp">KHNP</li><li class="li_rnd">RND</li></ul></li>';
                // 해당 월 오늘 이전 데이터
                } else if (dayCount < dd && mm == (date.getMonth() + 1) && currentYear == date.getFullYear()) {
                   	week += '<li class="past day' + dayCount.toString().padStart(2, '0') + '" onclick="openBoard(' + currentYear + ', ' + currentMonth + ', ' + dayCount.toString().padStart(2, '0') + ');"><span class="date">' + dayCount.toString().padStart(2, '0') + '</span><ul class="list"><li class="li_khnp">KHNP</li><li class="li_rnd">RND</li></ul></li>';
                // 전 월 데이터
                } else if ((currentMonth < (date.getMonth() + 1) && currentYear == date.getFullYear()) || currentYear < date.getFullYear()) {
                	week += '<li class="past day' + dayCount.toString().padStart(2, '0') + '" onclick="openBoard(' + currentYear + ', ' + currentMonth + ', ' + dayCount.toString().padStart(2, '0') + ');"><span class="date">' + dayCount.toString().padStart(2, '0') + '</span><ul class="list"><li class="li_khnp">KHNP</li><li class="li_rnd">RND</li></ul></li>';
                // 오늘 이후 데이터
                } else {
                    week += '<li><span class="date">' + dayCount.toString().padStart(2, '0') + '</span><ul class="list"></ul></li>';
                }
                dayCount++;
            }
        }
        week += "</ul>";
        calendarBody += week;
    }

    $('.date_tt').html('<button type="button" class="btn_month_prev" onclick="createCalendar(' + prevMonth + ', ' + prevYear + ')"></button><span class="calendarYear">' + currentYear.toString() + '년 ' + currentMonth.toString().padStart(2, '0') + '월</span><button type="button" class="btn_month_next" onclick="createCalendar(' + nextMonth + ', ' + nextYear +')"></span>');
    $('.calendar_list').html(calendarHeader + calendarBody);
    
    if (currentYear < date.getFullYear() || (currentYear == date.getFullYear() && currentMonth < date.getMonth() + 1)) {
        createCalendarIcon(1, lastDate, currentYear, currentMonth);
    } else if (currentYear == date.getFullYear() && currentMonth == date.getMonth() + 1) {
        createCalendarIcon(1, dd, currentYear, currentMonth);
    }

    $('.calendar.cnt').each(function () {
        $(this).children('li:first-child').addClass('sun');
        $(this).children('li:last-child').addClass('sat');
    });
}

function openBoard(openYear, openMonth, openDate) {
    $('.calendar.cnt li').removeClass('active');
    $('.day' + openDate.toString().padStart(2, '0')).addClass('active');
    var now = new Date();
    fileCount(new Date(openYear, openMonth - 1, openDate, now.getHours(), now.getMinutes()));
}

function createCalendarIcon(firstDate, lastDate, yy, mm) {
    for (var i = firstDate; i <= lastDate; i++) {
        (function (i) {
            var date = new Date(yy, mm - 1, i);
            var [boardTime, dayParam] = dateSelect(date);

            $.ajax({
                url: '/wdmsCont/fileListData.do',
                data: {boardTime: boardTime, dayParam: dayParam},
                success: function (data) {
                    var isError = {khnp: false, rnd: false};
                    var isCorrection = {khnp: false, rnd: false};

                    for (var j = 0; j < data.length; j++) {
                        var server_nm = data[j]["server_nm"];
                        var repo_nm = data[j]["repo_nm"];
                        var error_flag = data[j]["error_flag"];

                        if (server_nm == 'khnp' && repo_nm == '/home/outer/data') {
                            if (error_flag == 'y') {isError.khnp = true;} else if (error_flag == 'c') {isCorrection.khnp = true;}
                        } else if (server_nm == 'rnd' && repo_nm == '/nas/met') {
                            if (error_flag == 'y') {isError.rnd = true;} else if (error_flag == 'c') {isCorrection.rnd = true;}
                        }
                    }

                    if (isError.khnp) {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_khnp').addClass("error");
                    } else if (!isError.khnp && isCorrection.khnp) {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_khnp').addClass("reprod");
                    } else {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_khnp').removeClass("error");
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_khnp').removeClass("reprod");
                    }
                    
                    if (isError.rnd) {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_rnd').addClass("error");
                    } else if (!isError.rnd && isCorrection.rnd) {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_rnd').addClass("reprod");
                    } else {
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_rnd').removeClass("error");
                    	$('.day' + date.getDate().toString().padStart(2, '0') + ' .list .li_rnd').removeClass("reprod");
                    }
                }
            });
        })(i);
    }
}

function fileCount(date) {
    var [boardTime, dayParam] = dateSelect(date);

    var selectedDate = new Date(boardTime);
    var selectedFormattedDate = '';
    if (dayParam == 'y') {
        selectedFormattedDate = selectedDate.getFullYear().toString() + '년 ' +(selectedDate.getMonth() + 1).toString().padStart(2, '0') + '월 ' + (selectedDate.getDate() + 1).toString().padStart(2, '0') + '일 ' + selectedDate.getHours().toString().padStart(2, '0') + '시 ' + selectedDate.getMinutes().toString().padStart(2, '0') + '분';
    } else {
        selectedFormattedDate = selectedDate.getFullYear().toString() + '년 ' +(selectedDate.getMonth() + 1).toString().padStart(2, '0') + '월 ' + selectedDate.getDate().toString().padStart(2, '0') + '일 ' + selectedDate.getHours().toString().padStart(2, '0') + '시 ' + selectedDate.getMinutes().toString().padStart(2, '0') + '분';
    }

    $.ajax({
        url: '/wdmsCont/fileListData.do',
        data: {boardTime: boardTime, dayParam: dayParam},
        success: function (data) {
            $('.cnt_wrap .cnt .tbl-wrap tbody').empty();
            $('.cnt_wrap .cnt h3').empty();
            
			var server_error_count = {khnp: 0, rnd: 0};
            var tbl_rows = {khnp: '', rnd: ''};
            var total_count = {khnp: 0, rnd: 0};
            var error_flag_std = {khnp: false, rnd: false};

            for (var i = 0; i < data.length; i++){
            	if (data[i]["server_nm"] == 'khnp'){
            		if (data[i]["error_flag"] == 'y' || data[i]["error_flag"] == 'c'){
                        total_count.khnp++;
                        if (data[i]["error_flag"] == 'y') {
                            error_flag_std.khnp = true;
                        }
                    }
            	} else if (data[i]["server_nm"] == 'rnd'){
            		if (data[i]["error_flag"] == 'y' || data[i]["error_flag"] == 'c'){
                        total_count.rnd++;
                        if (data[i]["error_flag"] == 'y') {
                            error_flag_std.rnd = true;
                        }
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
                $('.cnt_wrap .tt p').html('기준 시간 : ' + selectedFormattedDate);
                
                if (error_flag == 'y' || error_flag == 'c') {
                    if (server_nm == 'khnp' && repo_nm == '/home/outer/data') {
                    	server_error_count.khnp++;
                        tbl_rows.khnp += createTableRow(total_count.khnp, file_nm, file_attr, file_desc, file_count, std_count, file_path, error_flag_desc);
                        total_count.khnp--;
                    } else if (server_nm == 'rnd' && repo_nm == '/nas/met') {
                    	server_error_count.rnd++;
                        tbl_rows.rnd += createTableRow(total_count.rnd, file_nm, file_attr, file_desc, file_count, std_count, file_path, error_flag_desc);
                        total_count.rnd--;
                    }
                }
            }
            updateTable(error_flag_std.khnp, '.cnt_wrap .cnt_khnp h3','.cnt .etc_khnp', 'KHNP' , '.tbl-wrap.tbl_khnp tbody', tbl_rows.khnp, boardTime);
            updateTable(error_flag_std.rnd, '.cnt_wrap .cnt_rnd h3','.cnt .etc_rnd', 'RND' , '.tbl-wrap.tbl_rnd tbody', tbl_rows.rnd, boardTime);
            createErrorMsg(boardTime, server_error_count, error_flag_std);
            errorState(server_error_count, error_flag_std);
        }
    });

    function createTableRow(total_count, file_nm, file_attr, file_desc, file_count, std_count, file_path, error_flag_desc) {
        if (file_count == '-') {
            return '<tr><td>' + total_count.toString().padStart(2, '0') + '</td><td class="t_left"><a class="copy-link" data-path="' + file_path + '" onfocus="this.blur()"><p>' + file_nm + '</p></a></td><td>' + file_attr + '</td><td>-</td><td>' + error_flag_desc + '</td></tr>';
        } else {
            return '<tr><td>' + total_count.toString().padStart(2, '0') + '</td><td class="t_left"><a class="copy-link" data-path="' + file_path + '" onfocus="this.blur()"><p>' + file_nm + '</p></a></td><td>' + file_attr + '</td><td><strong>' + file_count + '</strong> / ' + std_count + '</td><td>' + error_flag_desc + '</td></tr>';
        }
    }

    function updateTable(error_flag_std, tbl_title, tbl_btn, tbl_nm, tbl_list, tbl_rows, stdDate) {
        if (tbl_nm == 'KHNP') {
        	if (tbl_rows.length == 0) {
                $(tbl_title).html(tbl_nm + '<span class="normal">정상</span>');
                $(tbl_title).addClass('khnp');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'khnp\');">scan</button><button type="submit" class="btn_remake disabled">재생산</button>');
                tbl_rows = '<tr><td colspan="5" style="height: 260px;">이상 없음</td></tr>';
            } else if (tbl_rows.length != 0 && error_flag_std == false) {
                $(tbl_title).html(tbl_nm + '<span class="normal">정상</span>');
                $(tbl_title).addClass('khnp');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'khnp\');">scan</button><button type="submit" class="btn_remake disabled">재생산</button>');
            } else {
               $(tbl_title).html(tbl_nm + '<span class="error">문제발생</span>');
                $(tbl_title).addClass('khnp');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'khnp\');">scan</button><button type="submit" class="btn_remake" onclick="reproduction(2);">재생산</button>');
                
            }
        }
        if (tbl_nm == 'RND') {
        	if (tbl_rows.length == 0) {
                $(tbl_title).html(tbl_nm + '<span class="normal">정상</span>');
                $(tbl_title).addClass('rnd');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'rnd\');">scan</button><button type="submit" class="btn_remake disabled" disabled>재생산</button>');
                tbl_rows = '<tr><td colspan="5" style="height: 260px;">이상 없음</td></tr>';
            } else if (tbl_rows.length != 0 && error_flag_std == false) {
                $(tbl_title).html(tbl_nm + '<span class="normal">정상</span>');
                $(tbl_title).addClass('rnd');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'rnd\');">scan</button><button type="submit" class="btn_remake disabled" disabled>재생산</button>');
            } else {
               $(tbl_title).html(tbl_nm + '<span class="error">문제발생</span>');
                $(tbl_title).addClass('rnd');
                $(tbl_btn).html('<button type="submit" onclick="insertFileCount(' + selectedDate.getTime() + ', \'rnd\');">scan</button><button type="submit" class="btn_remake" onclick="reproduction(1);">재생산</button>');
            }
        }
            
        $(tbl_list).html(tbl_rows);
        
        $(tbl_list).off('click').on('click', '.copy-link', function () {
            var file_path = $(this).data('path');
            var year = selectedDate.getFullYear().toString();
            var month = (selectedDate.getMonth() + 1).toString().padStart(2, '0');
            var day = selectedDate.getDate().toString().padStart(2, '0');
    
            var fullPath = file_path.replace("yyyy", year).replace("mm", String(month).padStart(2, '0')).replace("dd", String(day).padStart(2, '0'));
            $(this).attr('data-clipboard-text', fullPath);
        });
    }

    function errorState(server_error_count, error_flag_std) {
        if (error_flag_std.khnp == true || error_flag_std.rnd == true) {
        	$('.cnt_wrap').addClass('active');
        } else {
        	$('.cnt_wrap').removeClass('active');
        }
    }
}

function createErrorMsg(boardTime, server_error_count, error_flag_std){
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

function insertFileCount(date, serverParam){
	var selectedDate = new Date(date);
	var [boardTime, dayParam] = dateSelect(selectedDate);

	$.ajax({
		url: '/wdmsCont/insertFileCount.do',
		data: {boardTime: boardTime, dayParam: dayParam, serverParam: serverParam},
		success: function(data){
			openBoard(selectedDate.getFullYear(), selectedDate.getMonth() + 1, selectedDate.getDate());
			createCalendarIcon(selectedDate.getDate(), selectedDate.getDate(), selectedDate.getFullYear(), selectedDate.getMonth() + 1);
		},
		beforeSend: function() {
			$('.loadingMsg').show();
			$('.loadingMsg').css('display','flex');
		},
		complete: function(){
			$('.loadingMsg').hide();
			$('.loadingMsg').css('display','none');
		}
	});
}

// 클립보드 기능
function clipBoard() {
	new ClipboardJS('.copy-link', {
        text: function (trigger) {
            return trigger.getAttribute('data-clipboard-text');
        }
    }).on('success', function (e) {
        alert("클립보드에 복사가 완료되었습니다.");
        e.clearSelection();
    }).on('error', function (e) {
        console.error('복사 실패:', e.toString());
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
    	openBoard(new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate());
    	createCalendarIcon(1, new Date().getDate(), new Date().getFullYear(), new Date().getMonth() + 1);
        setInterval(function() {
        	openBoard(new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate());
        	createCalendarIcon(1, new Date().getDate(), new Date().getFullYear(), new Date().getMonth() + 1);
        }, 60 * 60 * 1000);
    }, nextTimeTarget);
}

function dailySchedule() {
    var now = new Date();
    var targetTime = new Date();
    
    targetTime.setHours(0);
    targetTime.setMinutes(1);
    targetTime.setSeconds(0);
    targetTime.setMilliseconds(0);
    
    if (now > targetTime) {
        targetTime.setDate(targetTime.getDate() + 1);
    }

    var nextTimeTarget = (targetTime - now);
    
    setTimeout(function() {
		createCalendar(date.getFullYear(), date.getMonth() + 1);
    	setInterval(function() {
    		createCalendar(date.getFullYear(), date.getMonth() + 1);
        }, 24 * 60 * 60 * 1000);
    }, nextTimeTarget);
}

function nowDate() {
    var now = new Date();

    var year = now.getFullYear();
    
    var month = now.getMonth() + 1;
    month = month < 10 ? '0' + month : month; 
    
    var day = now.getDate();
    day = day < 10 ? '0' + day : day;
    
    var hours = now.getHours();
    hours = hours < 10 ? '0' + hours : hours;

    var minutes = now.getMinutes();
    minutes = minutes < 10 ? '0' + minutes : minutes;

    //minutes = minutes.toString();
    //minutes = minutes.substring(0, 1) + '0';
    minutes = '00';

    var formattedDate = hours + minutes;
    
    return formattedDate;
}

function reproduction(repoId) {
	var issue_date = nowDate();
	
	var alarmText = $('header .alarm.active').html();
	
	var dateMatch = alarmText.match(/\[(\d{4})-(\d{2})-(\d{2}) \d{2}:\d{2}\]/);
	if (dateMatch) {
	    var formattedDate = dateMatch[1] + dateMatch[2] + dateMatch[3] + issue_date; 
	    
	} 
	
    var correctionData = [];
    $('.tbl-wrap.tbl_' + (repoId == 2 ? 'khnp' : 'rnd') + ' tbody tr').each(function () {
    	var rowData = [];
    	
    	$(this).find('td').each(function () {
            rowData.push($(this).text().trim());
        });
    	
        var fileType = rowData[1];
        
        console.log(fileType);
        
        var errorFlag = $(this).find('#not_prblm').text().includes("이상 없음");
        
        if (fileType && !errorFlag) {
            correctionData.push({
            	fileType: fileType,
                issuedate: formattedDate,
                repoId: repoId
            });
        }
        
    });
    
    if (correctionData.length == 0) {
        alert('보정할 데이터가 없습니다.');
        return; 
    }
    
    $.ajax({
        url: '/wdmsCont/updateCorrectionData.do',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(correctionData),
        success: function (response) {
        	alert('보정이 완료되었습니다!');

            var selectedDate = new Date();

            if ($('.calendar.cnt li.active').length > 0) {
                var activeDay = $('.calendar.cnt li.active .date').text(); 
                var currentYear = $('.calendarYear').text().split('년')[0].trim();
                var currentMonth = $('.calendarYear').text().split('년 ')[1].split('월')[0].trim();
                
                selectedDate = new Date(currentYear, currentMonth, activeDay);
                
                createCalendar(parseInt(currentMonth), parseInt(currentYear));
                openBoard(parseInt(currentYear), parseInt(currentMonth), parseInt(activeDay));
                
            }
        },
        beforeSend: function() {
			$('.loadingMsg').show();
			$('.loadingMsg').css('display','flex');
		},
		complete: function(){
			$('.loadingMsg').hide();
			$('.loadingMsg').css('display','none');
		},
        error: function (xhr, status, error) {
            alert('보정 중 문제가 발생했습니다.');
        }
    });
}

$(function() {
	var currentPage = window.location.pathname;
	if (currentPage == '/wdms/wdms_monitoring.do') {$('.mornitoring').addClass('active');} else if (currentPage == '/wdms/wdms_specMng.do') {$('.spec_mng').addClass('active');} else if (currentPage == '/wdms/wdms_metroStatus.do') {$('.metro_status').addClass('active');}
	clipBoard();
	createCalendar(new Date().getMonth() + 1, new Date().getFullYear());
	openBoard(new Date().getFullYear(), new Date().getMonth() + 1, new Date().getDate());
	hourlySchedule();
	dailySchedule();
});

</script>
<div class="loadingMsg" style="display: none; justify-content: center; align-items: center; width: 100%; height: 100%; background-color: rgba(49,49,49,0.6); position: absolute; z-index: 9999; text-align: center; top: 0; left: 0;">
	<h3 style="margin: 0; font-size: 25px;">잠시만 기다려주세요.</h3>
</div>
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
        <div class="alarm active"></div>
        <nav class="nav_wrap">
               <ul>
                   <li><a href="/wdms/wdms_monitoring.do" class="mornitoring">모니터링</a></li>
                   <li><a href="/wdms/wdms_metroStatus.do" class="metro_status">기상자료 현황</a></li>
                   <li><a href="/wdms/wdms_specMng.do" class="spec_mng">제원관리</a></li>
               </ul>
           </nav>
    </header>
    <!--// header -->
	<!-- contents //-->
	<div id="contents">
		<div class="calendar_wrap">
			<div class="date_tt"></div>
			<div class="calendar_list"></div>
		</div>
		<div class="cnt_wrap">
			<div class="tt">
				<h3>자료 현황</h3>
				<p></p>
			</div>
			<div class="cnt br_b cnt_khnp" data-server-type="2">
				<h3></h3>
				<div class="etc etc_khnp"></div>
				<div class="tbl-wrap tbl_khnp">
					<table>
						<colgroup>
							<col style="width: 5%">
							<col style="width: auto">
							<col style="width: 10%">
							<col style="width: 17%">
							<col style="width: 15%">
						</colgroup>
						<thead>
							<tr>
								<th>No</th>
								<th>자료명</th>
								<th>속성</th>
								<th>자료현황</th>
								<th>자료상태</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
			<div class="cnt br_b cnt_rnd" data-server-type="1">
				<h3></h3>
				<div class="etc etc_rnd"></div>
				<div class="tbl-wrap tbl_rnd">
					<table>
						<colgroup>
							<col style="width: 5%">
							<col style="width: auto">
							<col style="width: 10%">
							<col style="width: 17%">
							<col style="width: 15%">
						</colgroup>
						<thead>
							<tr>
								<th>No</th>
								<th>자료명</th>
								<th>속성</th>
								<th>자료현황</th>
								<th>자료상태</th>
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!-- contents -->
    <jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>