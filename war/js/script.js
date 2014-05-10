var array = [];
var tmp = 0;
function removeIt(id, str2) {
	$('#' + id).remove();
	tmp -= 1;
    var index = array.indexOf(str2);
    if (index > -1) {
    	array.splice(index, 1);
    }
	var str = "";
	for (var i = 0; i < array.length; i ++) {
		str += "<br/><span style='padding-left:20px;' id="+i+"><img width='12' height='12' src='/images/del.png' onclick=\"removeIt('"+i +"', '"+array[i]+"')\" style='cursor: pointer;' />　　　"+array[i]+"</span>";
	}
	$('#showArea').html(str);
	$('#showArea2').empty();
	console.log(array.toString());
}
$(function(){
	$('#mainframe, #introframe').shadow();
	$('#sDate, #eDate').datepicker({ dateFormat: 'mm/dd' });
	$('#append').click(function(){
		$('#showArea2').empty();
		if ($("input[name='oneline']:checkbox:checked").val() == 'true') {
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
					$('#showArea2').empty().append("<table width='440' border='1' style='word-break:break-all;' id='mt'><tr><th width='26%'><center>選項</center></th><th width='12%'><center>得點</center></th><th><center>投票人ID</center></th></tr>");
					for (var i = 0; i < response.length; i ++) {
						var voter = response[i].voter == null ? "" : response[i].voter ;
						$('#mt').append("<tr><td><center>"+response[i].keyword+"</center></td><td><center>"+response[i].count+"<br/></center></td><td style='padding:5px;'>" + voter + "</td></tr>");
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
    } 
}); 