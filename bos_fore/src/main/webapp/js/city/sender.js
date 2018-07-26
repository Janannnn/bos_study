
// 百度地图API功能
function G(id) {
	return document.getElementById(id);
}

var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
	{"input" :"sendAddress"
	
});
/*以下是为了自动补全后详细地址和省市区一致*/
ac.addEventListener("onhighlight", function(e) {  //鼠标放在下拉列表上的事件
var str = "";
	var _value = e.fromitem.value;
	var value = "";
	if (e.fromitem.index > -1) {
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str = "FromItem<br />index = " + e.fromitem.index + "<br />value = " + value;
	
	value = "";
	if (e.toitem.index > -1) {
		_value = e.toitem.value;
		value = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	}    
	str += "<br />ToItem<br />index = " + e.toitem.index + "<br />value = " + value;
	G("searchResultPanel").innerHTML = str;
});

var myValue;
ac.addEventListener("onconfirm", function(e) {//鼠标点击下拉列表后的事件
	
	var _value = e.item.value;
	myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
	G("searchResultPanel").innerHTML ="onconfirm<br />index = " + e.item.index + "<br />myValue = " + myValue;
	var address = encodeURIComponent(myValue);
	$.getJSON(
		"http://api.map.baidu.com/cloudgc/v1?ak=tDcueAbqeKvbdIHMn7LlBM04MBDuZZ58&address="+address+"&callback=?",
		function(data){
			if(data.status==0&&data.result.length>0){
				$('#sendAreaInfo').citypicker('reset');
				$('#sendAreaInfo').citypicker('destroy');
					
				$('#sendAreaInfo').citypicker({
						province: data.result[0].address_components.province ,
						city: data.result[0].address_components.city ,
						district: data.result[0].address_components.district
					});
				}
			}
		);
	});
