<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<jsp:include page="/WEB-INF/jsp/include/include_header.jsp"/>
<style type="text/css"> @import url("/css/wdms.css");</style>
<script src="/js/plugin/clipboard/clipboard.min.js"></script>
<link rel="stylesheet" href="/js/plugin/flatpickr/flatpickr.min.css" />
<link rel="stylesheet" href="/js/plugin/flatpickr/style.css" />
<script src="/js/plugin/flatpickr/flatpickr.js" ></script>
<script src="/js/plugin/flatpickr/ko.js" ></script>
<script src="/js/plugin/flatpickr/index.js" ></script>
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

function meStnInfoList() {
	console.log("meStnInfoList active");
    var fileType = $('.fileType').val();
    var agcType = $('.agcType').val() || 'all';
    var init_dt = $('.init_dt').val() || new Date().getFullYear().toString() + '' + (new Date().getMonth() + 1).toString().padStart(2, '0') + '' + (new Date().getDate() - 1).toString().padStart(2, '0');

    var endObsCheck = $('.endObsCheck').val();
    var stateOrder = $('.stateOrder').val();

    $.ajax({
        url: '/wdmsCont/selectMeStnInfo.do',
        data: {fileType: fileType, agcType: agcType, init_dt: init_dt, endObsCheck: endObsCheck, stateOrder: stateOrder},
        success: function (data) {
        	$('.tbl_con_wrap .tbl_wrap2 .tbl_state tbody').empty();
            var tableRows = '';
            var tableType = 'rnStn';
            	
            for (var i = 0; i < data.length; i++) {
            	var row = data[i];
            		for (var key in row) {
            			row[key] = replaceNull(row[key]);
            		}
                var init_dt = data[i]['init_dt']||'';
                var init_dt_txt = init_dt.substring(0, 4) + '.' + init_dt.substring(4, 6) + '.' + init_dt.substring(6, 8);
                var yyyymm = data[i]['yyyymm']||'';
                var yyyymm_txt = yyyymm.substring(0, 4) + '.' + yyyymm.substring(4, 6);
                var rfobscd = data[i]['rfobscd'];
                var wlobscd = data[i]['wlobscd'];
                var dmobscd = data[i]['dmobscd'];
                var obscd = '';
					if (fileType == 'rnStn') {obscd = rfobscd; tableType = 'rnStn';} else if (fileType == 'wlStn') {obscd = wlobscd; tableType = 'wlStn';} else if (fileType == 'dam') {obscd = dmobscd; tableType = 'dam';} else if (fileType == 'asos') {tableType = 'asos';} else if (fileType == 'aws') {tableType = 'aws';}
                var obsnm = data[i]['obsnm'];
                var stn_id = data[i]['stn_id'];
                var stn_nm = data[i]['stn_nm'];
                var agcnm = data[i]['agcnm'];
                var addr = data[i]['addr'];
                var etcaddr = data[i]['etcaddr'];
                var etcaddrClass = (etcaddr == '-') ? '' : 't_left';
                var lat = data[i]['lat'];
                var lon = data[i]['lon'];
                var gdt = data[i]['gdt'];
                var attwl = data[i]['attwl'];
                var wrnwl = data[i]['wrnwl'];
                var almwl = data[i]['almwl'];
                var srswl = data[i]['srswl'];
                var pfh = data[i]['pfh'];
                var fstnyn = data[i]['fstnyn'];
                var fldlmtwl = data[i]['fldlmtwl'];
                var ht = data[i]['ht'];
                var flag = data[i]['flag'];
                var total_count = data[i]['total_count'] - i;
                var flagTxt = '';
                	if (flag == '0') {flagTxt = '-'} else if (flag == '1') {flagTxt = '신규'} else if (flag == '2') {flagTxt = '종료'} else if (flag == '3') {flagTxt = '관측소명 변경'} else if (flag == '4') {flagTxt = '기관 변경'} else if (flag == '5') {flagTxt = '주소 변경'} else if (flag == '6') {flagTxt = '위치 변경'}
                
                if (tableType == 'rnStn') {
					if (flag == '1') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state new">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td>';	
					} else if (flag == '2') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state end">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td>';	
					} else if (flag == '0') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state">' + flagTxt + '</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td>';
					} else {
						tableRows += '<tr><td>' + total_count + '</td><td class="state modify">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td>';	
					}
				} else if (tableType == 'wlStn') {
					if (flag == '1') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state new">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + gdt + '</td><td>' + attwl + '</td><td>' + wrnwl + '</td><td>' + almwl + '</td><td>' + srswl + '</td><td>' + pfh + '</td><td>' + fstnyn + '</td>';	
					} else if (flag == '2') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state end">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + gdt + '</td><td>' + attwl + '</td><td>' + wrnwl + '</td><td>' + almwl + '</td><td>' + srswl + '</td><td>' + pfh + '</td><td>' + fstnyn + '</td>';	
					} else if (flag == '0') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state">' + flagTxt + '</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + gdt + '</td><td>' + attwl + '</td><td>' + wrnwl + '</td><td>' + almwl + '</td><td>' + srswl + '</td><td>' + pfh + '</td><td>' + fstnyn + '</td>';
					} else {
						tableRows += '<tr><td>' + total_count + '</td><td class="state modify">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + gdt + '</td><td>' + attwl + '</td><td>' + wrnwl + '</td><td>' + almwl + '</td><td>' + srswl + '</td><td>' + pfh + '</td><td>' + fstnyn + '</td>';
					}
				} else if (tableType == 'dam') {
					if (flag == '1') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state new">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + fldlmtwl + '</td><td>' + pfh + '</td>';	
					} else if (flag == '2') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state end">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + fldlmtwl + '</td><td>' + pfh + '</td>';	
					} else if (flag == '0') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state">' + flagTxt + '</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + fldlmtwl + '</td><td>' + pfh + '</td>';
					} else {
						tableRows += '<tr><td>' + total_count + '</td><td class="state modify">' + flagTxt + ' (' + init_dt_txt + ')</td><td>' + obscd + '</td><td>' + obsnm + '</td><td>' + agcnm + '</td><td>' + addr + '</td><td class="' + etcaddrClass + '">' + etcaddr + '</td><td>' + lat + '</td><td>' + lon + '</td><td>' + fldlmtwl + '</td><td>' + pfh + '</td>';
					}
				} else if (tableType == 'asos') {
					if (flag == '1') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state new">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';	
					} else if (flag == '2') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state end">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';	
					} else if (flag == '0') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state">' + flagTxt + '</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';
					} else {
						tableRows += '<tr><td>' + total_count + '</td><td class="state modify">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';
					}
				} else if (tableType == 'aws') {
					if (flag == '1') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state new">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';	
					} else if (flag == '2') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state end">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';	
					} else if (flag == '0') {
						tableRows += '<tr><td>' + total_count + '</td><td class="state">' + flagTxt + '</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';
					} else {
						tableRows += '<tr><td>' + total_count + '</td><td class="state modify">' + flagTxt + ' (' + yyyymm_txt + ')</td><td>' + stn_id + '</td><td>' + stn_nm + '</td><td>-</td><td>-</td><td>' + lat + '</td><td>' + lon + '</td><td>' + ht + '</td>';
					}
				}
            }
            
	        if (tableType == 'rnStn') {$('.tbl_con_wrap .tbl_wrap2 .tbl_rnStn tbody').html(tableRows); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_rnStn').css('display','block'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_dam').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_wlStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_asos').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_aws').css('display','none');}
	        else if (tableType == 'wlStn') {$('.tbl_con_wrap .tbl_wrap2 .tbl_wlStn tbody').html(tableRows); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_wlStn').css('display','block'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_rnStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_dam').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_asos').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_aws').css('display','none');}
	        else if (tableType == 'dam') {$('.tbl_con_wrap .tbl_wrap2 .tbl_dam tbody').html(tableRows); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_dam').css('display','block'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_rnStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_wlStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_asos').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_aws').css('display','none');}
	        else if (tableType == 'asos') {$('.tbl_con_wrap .tbl_wrap2 .tbl_asos tbody').html(tableRows); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_asos').css('display','block'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_rnStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_wlStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_dam').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_aws').css('display','none');}
	        else if (tableType == 'aws') {$('.tbl_con_wrap .tbl_wrap2 .tbl_aws tbody').html(tableRows); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_aws').css('display','block'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_rnStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_wlStn').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_dam').css('display','none'); $('.tbl_con_wrap .tbl_wrap2.tbl_wrap_asos').css('display','none');}
	        
	        if (tableRows.length == 0) {$('.tbl_con_wrap .tbl_wrap2 .tbl_state tbody').html('<tr><td colspan="16">조건에 맞는 데이터가 없습니다.</td></tr>');}
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

function meAgcnmInfoList() {
	var agcType = $('.agcType').val();
    $('.fileTypeList').empty();
    $('.li_datepicker').css('display', 'none');
    $('.li_monthpicker').css('display', 'none');
    
    if (agcType == '기상청') {
    	$('.fileTypeList').html('<option value="asos">asos</option><option value="aws">aws</option>');
    	$('.fileType').val("asos");
    	$('.li_monthpicker').css('display', 'block');
    } else if (agcType == '환경부') {
    	$('.fileTypeList').html('<option value="rnStn">강우관측소</option><option value="dam">댐</option><option value="wlStn">수위관측소</option>');
    	$('.fileType').val("rnStn");
    	$('.li_datepicker').css('display', 'block');
    }
}

function changeFileType(select) {
    var fileType = select.value;
    $('.fileType').val(fileType);
    
    if (fileType == 'asos' || fileType == 'aws') {
    	createMonthpicker(fileType, function(){meStnInfoList();});
    } else {
    	meStnInfoList();
    }
}

function changeAgcType(select) {
    var agcType = select.value;
    	$('.agcType').val(agcType);
    	
    var today = new Date();
    	today.setDate(today.getDate() - 1); // 하루 전 날짜로 설정
    	
	var yyyymmList = [];
    var fileType = $('.fileType').val();

    var formattedYesterday = today.getFullYear().toString() + '' + (today.getMonth() + 1).toString().padStart(2, '0') + '' + today.getDate().toString().padStart(2, '0');
    var formattedMonth = '';
    	
    if (agcType == '기상청') {
    	$('.fileType').val("asos");
    	createMonthpicker('asos', function(){meStnInfoList();});
        meAgcnmInfoList();
    } else if (agcType == '환경부') {
    	$('.fileType').val("rnStn");
    	$('.init_dt').val(formattedYesterday);
    	meAgcnmInfoList();
        meStnInfoList();
    }
}

function changeEndObsCheck(check) {
	var endObsCheck;
	if ($(check).is(":checked")){
		endObsCheck = $('.endObsCheck').val(true);
	} else {
		endObsCheck = $('.endObsCheck').val(false);
	}
	meStnInfoList();
}

function changeStateOrder() {
    var stateOrder = $('.stateOrder').val();
    if (stateOrder == 'asc') {
    	$('.stateOrder').val('desc');
    } else if (stateOrder == 'desc') {
    	$('.stateOrder').val('asc');
    }
    meStnInfoList();
}

function createMonthpicker(fileType, callback) {
	var yyyymmList = [];
    
    $.ajax({
        url: '/wdmsCont/selectKmaYmList.do',
        data: {fileType: fileType},
        success: function (data) {
            for (var i = 0; i < data.length; i++){
				var yyyymm = data[i]['yyyymm'];
				yyyymmList.push(yyyymm);
			}

			var latestYyyymm = yyyymmList[0];
			var oldestYyyymm = yyyymmList[0];
			
			for (var j = 1; j < yyyymmList.length; j++){
				if (yyyymmList[j] > latestYyyymm) {
					latestYyyymm = yyyymmList[j];
				}
			}
			
			for (var k = 1; k < yyyymmList.length; k++){
				if (yyyymmList[k] < oldestYyyymm) {
					oldestYyyymm = yyyymmList[k];
				}
			}

            var latestYear = latestYyyymm.substring(0, 4);
            var latestMonth = latestYyyymm.substring(4, 6);
            var formattedMonth = latestYear + '.' + latestMonth;
            var oldestYear = parseInt(oldestYyyymm.substring(0, 4), 10);
			
            if ($(".monthpicker")[0]?._flatpickr) {
                $(".monthpicker")[0]._flatpickr.destroy();
            }

            $(".monthpicker").flatpickr({
            	locale: "ko",
                plugins: [new monthSelectPlugin({
                    shorthand: false,
                    dateFormat: "Y.m",
                    altFormat: "Y년 m월",
                    theme: "dark",
                   	monthNames: ["1월", "2월", "3월", "4월", "5월", "6월", "7월", "8월", "9월", "10월", "11월", "12월"]
                })],
                defaultDate: latestYear + '-' + latestMonth,
                minDate: oldestYear + "-01",
            	maxDate: latestYear + "-" + latestMonth
            });

            $(".monthpicker").val(formattedMonth);
            $(".init_dt").val(latestYyyymm);

            $(document).off("change", ".monthpicker").on("change", ".monthpicker", function () {
                var selected = $(this).val().replace(/\./g, "");
                $(".init_dt").val(selected);
                meStnInfoList();
            });

            if (typeof callback === 'function') {
                callback();
            }
        }
    });
}

function downloadCSV(){
	var fileType = $('.fileType').val();
    var agcType = $('.agcType').val() || 'all';
    var init_dt = $('.init_dt').val() || new Date().getFullYear().toString() + '' + (new Date().getMonth() + 1).toString().padStart(2, '0') + '' + (new Date().getDate() - 1).toString().padStart(2, '0');
    var endObsCheck = $('.endObsCheck').val();
    var stateOrder = $('.stateOrder').val();
    var stn_info = '';
    	if (fileType == 'rnStn'){stn_info = 'me_rn_stn_info'} else if (fileType == 'wlStn') {stn_info = 'me_wl_stn_info'} else if (fileType == 'dam') {stn_info = 'me_dam_stn_info'} else if (fileType == 'asos') {stn_info = 'kma_asos_stn_info'} else if (fileType == 'aws') {stn_info = 'kma_aws_stn_info'}

    window.location.href = "/wdmsCont/downloadCSV.do?fileType=" + fileType + "&agcType=" + agcType + "&init_dt=" + init_dt + "&endObsCheck=" + endObsCheck + "&stateOrder=" + stateOrder + "&stn_info=" + stn_info;
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
	var agcType = $('.agcType').val();
    var fileType = $('.fileType').val();
    
	var currentPage = window.location.pathname;
	if (currentPage == '/wdms/wdms_monitoring.do') {$('.mornitoring').addClass('active');} else if (currentPage == '/wdms/wdms_specMng.do') {$('.spec_mng').addClass('active');} else if (currentPage == '/wdms/wdms_metroStatus.do') {$('.metro_status').addClass('active');}
	
	todayErrorCount();
    hourlySchedule();
    createMonthpicker(fileType, function(){meStnInfoList();});
    meAgcnmInfoList();
});
</script>
<style>
.flatpickr-calendar {background-color: #1e1e1e; color: #fff; border: none; box-shadow: 0 4px 16px rgba(0, 0, 0, 0.5); font-size: 12px !important; width: 200px !important; min-width: 200px !important; height: 243.19px !important; min-height: 243.19px !important;}
.flatpickr-months .flatpickr-month {font-size: 14px !important; height: 30px; line-height: 30px;}
.flatpickr-months {background-color: #1e1e1e;color: #fff;}
.flatpickr-months .flatpickr-prev-month, .flatpickr-months .flatpickr-next-month {color: #fff; fill: #fff;}
.flatpickr-monthSelect-months {display: grid !important;grid-template-columns: repeat(3, 1fr); gap: 4px; padding: 4px; box-sizing: border-box;}
.flatpickr-monthSelect-theme-dark .flatpickr-monthSelect-month {height: 36px !important; width: 60px !important; text-align: center; font-size: 14px !important; border-radius: 6px; background-color: #666; transition: background 0.2s; text-align: center; justify-content: center;}
.flatpickr-monthSelect-theme-dark .flatpickr-monthSelect-month.selected {background-color: #004BFC; border: 0; color: white;}
.flatpickr-monthSelect-theme-dark .flatpickr-monthSelect-month:hover{background-color: #444B54; border: 0; color: white;}
.flatpickr-monthSelect-month.flatpickr-disabled {opacity: .35; cursor: default !important; pointer-events: none; background-color: #444;}
.flatpickr-calendar::before, .flatpickr-calendar::after {display: none !important;}
.flatpickr-monthSelect-month.today {border: none !important; outline: none !important;}
.flatpickr-monthSelect-theme-dark {background-color: #1e1e1e; color: white;}
.flatpickr-monthSelect-month {font-size: 13px !important; padding: 6px 0 !important; height: 24px; line-height: 24px;}
.flatpickr-monthSelect-theme-dark .flatpickr-monthSelect-month {height: 28px; margin: 4px 0;}
</style>
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
    <input type="hidden" class="agcType" value="기상청" />
	<input type="hidden" class="fileType" value="asos" />
	<input type="hidden" class="init_dt" value="" />
	<input type="hidden" class="endObsCheck" value="true" />
	<input type="hidden" class="stateOrder" value="asc" />
	<!-- contents //-->
	<div id="contents">
	    <div class="tbl_search_wrap">
	        <div class="search_wrap">
	            <ul>
	            	<li>
	                    <label>관할기관</label>
	                    <select name="agcnm" class="agcnmList" onChange="changeAgcType(this)">
	                    	<option value="기상청">기상청</option>
	                    	<option value="환경부">환경부</option>
	                    </select>
	                </li>
	                <li>
	                    <label>자료타입</label>
	                    <select name="fileType" class="fileTypeList" onChange="changeFileType(this)">
	                    </select>
	                </li>
	                <li class="li_datepicker" style="display: none;">
	                	<label>수집일자</label><input type="text" class="date datepicker" placeholder="날짜 선택">
	                </li>
	                <li class="li_monthpicker" style="display: none;">
	                	<label>수집일자</label><input type="text" class="date monthpicker" id="monthpicker" placeholder="날짜 선택">
	                </li>
	                <li class="listcheck">
	                    <label>종료 관측소 포함</label><input type="checkbox" onChange="changeEndObsCheck(this)" checked>
	                </li>
	            </ul>
	        </div>
	        <button class="download_btn" onClick="downloadCSV();">.csv 다운로드</button>
	    </div>
	    <div class="tbl_con_wrap">
	        <!--강우관측소 테이블-->
	        <div class="tbl_wrap2 tbl_wrap_rnStn">
	            <table class="tbl_state tbl_rnStn">
	                <caption>번호, 상태, 관측소코드, 관측소명, 관할기관, 주소, 나머지주소, 위도, 경도로 만들어진 제원관리 테이블 입니다.</caption>
	                <colgroup>
					    <col style="width:60px;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:140px;">
					    <col style="width:auto;">
					    <col style="width:auto;">
					    <col style="width:160px;">
					    <col style="width:160px;">
					</colgroup>
	                <thead>
	                    <tr>
	                        <th>번호</th>
	                        <th>상태<a onClick="changeStateOrder();" class="btn_state"><img src="/images/img_select.png" alt=""></a></th>
	                        <th>관측소코드</th>
	                        <th>관측소명</th>
	                        <th>관할기관</th>
	                        <th>주소</th>
	                        <th>나머지주소</th>
	                        <th>위도</th>
	                        <th>경도</th>
	                    </tr>
	                </thead>
	                <tbody></tbody>
	            </table>
	        </div>
	        <!--댐 테이블 -->
	        <div class="tbl_wrap2 tbl_wrap_dam" style="display: none;">
	            <table class="tbl_state tbl_dam">
	                <caption>번호, 상태, 댐코드, 댐명, 관할기관, 주소, 나머지주소, 위도, 경도, 계획홍수위(El.m), 제한수위(El.m)로 만들어진 제원관리 테이블 입니다.</caption>
	                <colgroup>
					    <col style="width:60px;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:140px;">
					    <col style="width:240px;">
					    <col style="width:auto;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:160px;">
					    <col style="width:160px;">
					</colgroup>
	                <thead>
	                    <tr>
	                        <th>번호</th>
	                        <th>상태<a onClick="changeStateOrder();" class="btn_state"><img src="/images/img_select.png" alt=""></a></th>
	                        <th>댐코드</th>
	                        <th>댐명</th>
	                        <th>관할기관</th>
	                        <th>주소</th>
	                        <th>나머지주소</th>
	                        <th>위도</th>
	                        <th>경도</th>
	                        <th>계획홍수위<small>(El.m)</small></th>
	                        <th>제한수위<small>(El.m)</small></th>
	                    </tr>
	                </thead>
	                <tbody></tbody>
	            </table>
	        </div>
	
	        <!--수위관측소 테이블 -->
	        <div class="tbl_wrap2 tbl_wrap_wlStn" style="display: none;">
	            <table class="tbl_state tbl_wlStn">
	                <caption>번호, 상태, 관측소코드, 관측소명, 관할기관, 주소, 나머지주소, 위도, 경도, 영점표고(El.m), 관심수위(m), 주의보수위(m), 겅보수위(m), 심각수위(m), 계획홍수위(m), 특보지점여부(Y,N)로 만들어진 제원관리 테이블 입니다.</caption>
	                <colgroup>
					    <col style="width:60px;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:auto;">
					    <col style="width:100px;">
					    <col style="width:100px;">
					    <col style="width:80px;">
					    <col style="width:80px;">
					    <col style="width:100px;">
					    <col style="width:80px;">
					    <col style="width:80px;">
					    <col style="width:90px;">
					    <col style="width:100px;">
					</colgroup>
	                <thead>
	                    <tr>
	                        <th>번호</th>
	                        <th>상태<a onClick="changeStateOrder();" class="btn_state"><img src="/images/img_select.png" alt=""></a></th>
	                        <th>관측소코드</th>
	                        <th>관측소명</th>
	                        <th>관할기관</th>
	                        <th>주소</th>
	                        <th>나머지주소</th>
	                        <th>위도</th>
	                        <th>경도</th>
	                        <th>영점표고<small>(El.m)</small></th>
	                        <th>관심수위<small>(m)</small></th>
	                        <th>주의보수위<small>(m)</small></th>
	                        <th>경보수위<small>(m)</small></th>
	                        <th>심각수위<small>(m)</small></th>
	                        <th>계획홍수위<small>(m)</small></th>
	                        <th>특보지점여부<small>(Y,N)</small></th>
	                    </tr>
	                </thead>
	                <tbody></tbody>
	            </table>
	        </div>
	        
	        <!--asos 테이블 -->
	        <div class="tbl_wrap2 tbl_wrap_asos" style="display: none;">
	            <table class="tbl_state tbl_asos">
	                <colgroup>
					    <col style="width:60px;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:180px;">
					    <col style="width:auto;">
					    <col style="width:160px;">
					    <col style="width:160px;">
					    <col style="width:100px;">
					</colgroup>
	                <thead>
	                    <tr>
	                        <th>번호</th>
	                        <th>상태<a onClick="changeStateOrder();" class="btn_state"><img src="/images/img_select.png" alt=""></a></th>
	                        <th>관측소코드</th>
	                        <th>관측소명</th>
	                        <th>주소</th>
	                        <th>나머지주소</th>
	                        <th>위도</th>
	                        <th>경도</th>
	                        <th>해발고도<small>(m)</small></th>
	                    </tr>
	                </thead>
	                <tbody></tbody>
	            </table>
	        </div>
	        
	        <!--aws 테이블 -->
	        <div class="tbl_wrap2 tbl_wrap_aws" style="display: none;">
	            <table class="tbl_state tbl_aws">
	                <colgroup>
					    <col style="width:60px;">
					    <col style="width:140px;">
					    <col style="width:140px;">
					    <col style="width:180px;">
					    <col style="width:180px;">
					    <col style="width:auto;">
					    <col style="width:160px;">
					    <col style="width:160px;">
					    <col style="width:100px;">
					</colgroup>
	                <thead>
	                    <tr>
	                        <th>번호</th>
	                        <th>상태<a onClick="changeStateOrder();" class="btn_state"><img src="/images/img_select.png" alt=""></a></th>
	                        <th>관측소코드</th>
	                        <th>관측소명</th>
	                        <th>주소</th>
	                        <th>나머지주소</th>
	                        <th>위도</th>
	                        <th>경도</th>
	                        <th>해발고도<small>(m)</small></th>
	                    </tr>
	                </thead>
	                <tbody></tbody>
	            </table>
	        </div>
	    </div>
	</div>
	<!-- contents -->
	<jsp:include page="/WEB-INF/jsp/include/include_footer.jsp"/>
<script>
$(function () {
    var today = new Date();
    	today.setDate(today.getDate() - 1); // 하루 전 날짜로 설정

    var agcType = $('.agcType').val();
    var fileType = $('.fileType').val();
    var formattedYesterday = today.getFullYear().toString() + (today.getMonth() + 1).toString().padStart(2, '0') + today.getDate().toString().padStart(2, '0');

    $(".datepicker").datepicker({
        firstDay: 1,
        showOtherMonths: true,
        changeMonth: false,
        changeYear: false,
        dateFormat: "yy.mm.dd",
        maxDate: today,
        monthNames: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
        monthNamesShort: ['1월','2월','3월','4월','5월','6월','7월','8월','9월','10월','11월','12월'],
        dayNames: ['일','월','화','수','목','금','토'],
        dayNamesShort: ['일','월','화','수','목','금','토'],
        dayNamesMin: ['일','월','화','수','목','금','토']
    });
    
    $(".datepicker").datepicker('setDate', today);

    $(".datepicker").on("change", function() {
        var formattedDate = $(this).val().replace(/\./g, ""); 
        $(".init_dt").val(formattedDate);
        meStnInfoList();
    });

    $(document).on("mousedown", ".date", function () {
        $(".ui-datepicker").addClass("active");
    });

    $(document).on("click", ".btn_state", function () {
        $(this).toggleClass("active");
    });
});
</script>