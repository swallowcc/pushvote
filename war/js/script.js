var array = [];
var tmp = 0;
var ttt;
function removeIt(id, str2) {
	$('#' + id).remove();
	tmp -= 1;
    var index = array.indexOf(str2);
    if (index > -1) {
    	array.splice(index, 1);
    }
	var str = "";
	for (var i = 0; i < array.length; i ++) {
		str += "<br/><span style='padding-left:20px;' id="+i+"><img width='12' height='12' src='https://rawgit.com/swallowcc/pushvote/master/war/images/del.png' onclick=\"removeIt('"+i +"', '"+array[i]+"')\" style='cursor: pointer;' />　　　"+array[i]+"</span>";
	}
	$('#showArea').html(str);
	$('#showArea2').empty();
}
function userList(voter) {
//	var voter = response[id].voter == null ? "" : response[id].voter ;
	$('#userList').show();
	$('#userList').html(voter);
	$('#userList').append("<hr/><center><input type='button' value='close' onclick='hide()' /></center>");
}
function hide() {
	$('#userList').hide();
}
$(function(){
	$("#copys").zclip({
	      path:"ZeroClipboard.swf",
	      copy:function(){return $("input#dynamic").val();}
	});
	$('#userList').draggable();
	$('#mainframe, #introframe').shadow();
	$('#sDate, #eDate').datepicker({ dateFormat: 'mm/dd' });
	$('#append').click(function(){
		$('#showArea2').empty();
		if ($("input[name='input']:checked").val() == 'web' || $("input[name='input']:checked").val() == 'oneline') {
			if ($.trim($('#target').val()).length != 0) {
				array = [];
				var tmps = $('#target').val().split(",");
				var str = "";
				for (var i = 0; i < tmps.length; i ++) {
					array.push(tmps[i])
					$('#target').val("");
					str += "<br/><span style='padding-left:20px;' id="+i+"><img width='12' height='12' src='/images/del.png' onclick=\"removeIt('"+i +"', '"+tmps[i]+"')\" style='cursor: pointer;' />　　　"+tmps[i]+"</span>";
				}
				$('#showArea').html(str);
			}
		} else {
			if ($.trim($('#target').val()).length != 0) {
				array.push($('#target').val());
				$('#target').val("");
				tmp ++;
			}
			var str2 = "";
			for (var i = 0; i < array.length; i ++) {
				str2 += "<br/><span style='padding-left:20px;' id="+i+"><img width='12' height='12' src='/images/del.png' onclick=\"removeIt('"+i +"', '"+array[i]+"')\" style='cursor: pointer;' />　　　"+array[i]+"</span>";
			}
			$('#showArea').html(str2);
		}
	})
	$('#intro').click(function(){
		$('#introframe').fadeToggle("fast");
	});
	$('#btn').click(function(){
		$('#introframe').hide();
	});
	$('#send').click(function(){
		if ($("input[name='input']:checked").val() == 'single' && array.length == 0) {
			alert('抱歉, 投票選項未輸入!');
			$('#loading').hide();
			return false;
		}
		$('#loading').show();
		$('#hiddenValue').val(array.toString());
		$.ajax({
			url: "pushvote",
			type: "get",
			data: $('#myform').serialize(),
			success: function(data) {
				switch (parseInt(data)) {
				case 0:
					alert('抱歉, 查無此TAG!');
					$('#loading').hide();
					break;
				case 1:
					alert('抱歉, 投票選項未輸入!');
					$('#loading').hide();
					break;
				case 2:
					alert('抱歉, 頁面無法讀取!');
					$('#loading').hide();
					break;
				case 3:
					alert('抱歉, 日期選項有誤!');
					$('#loading').hide();
					break;				
				case 4:
					alert('抱歉, 網址有誤!');
					$('#loading').hide();
					break;
				default:
					var response = jQuery.parseJSON(data);
					$('#showArea').empty();
					$('#showArea2').empty().append("<div id='t1' style='text-align:right; width:440px; height:20px;'>總投票人數: "+response[response.length - 1].tu+" / 總投票數: "+response[response.length - 1].tv+" / <a href='#' id='copyss'>複製結果</a></div><table width='440' border='1' style='word-break:break-all; text-align:center;' id='mt'><tr style='background-color: silver; text-align:center;'><th width='40%' style='text-align:center;'>選項</th><th width='15%' style='text-align:center;'>得點</th><th width='15%' style='text-align:center;'>得票率</th><th width='15%' style='text-align:center;'>得票分佈</th><th style='text-align:center;'>投票人ID</th></tr>");
					for (var i = 0; i < response.length - 1; i ++) {
						var voter = response[i].voter == null ? "" : response[i].voter ;
						var myvote = 0;
						var myvote2 = 0;
						if (!response[i].count == 0) {
							myvote = ((response[i].count/response[response.length - 1].tv) * 100).toFixed(1) + "%"; 	//得票率
							myvote2 = ((response[i].count/response[response.length - 1].tu) * 100).toFixed(1) + "%";	//得票分佈
						}
						$('#mt').append("<tr><td>"+response[i].keyword+"</td><td>"+response[i].count+"</td><td>"+myvote+"</td><td>"+myvote2+"</td><td><img src='/images/user.png' onclick=\"userList('"+ voter +"')\" style='cursor: pointer; width:30px; height:30px;'></td></tr>");
					}
					$('#mt').append("</table>");
					$('#loading').hide();
					break;
				}
			}
		});
	});
}).keydown(function(e) {
    if(e.which == 27){ 
        $('#introframe').hide(); 
        $('#userList').hide(); 
    } 
}); 