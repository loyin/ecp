require.config({
	paths : {
		'echarts' : rootPath + '/assets/js/echarts/echarts',
		'echarts/chart/funnel' : rootPath + '/assets/js/echarts/echarts'
	}
});
var model = avalon.define({
	$id : 'ctrl',
	userList:[],userComboV:false,
    chooseUser:function(e){
    	model.query.head_id=e.id;
    	model.query.head_name=e.realname;
    	model.userComboV=false;
    },
    qryUser:function(v){//自动完成查询用户
    	model.userComboV=true;
    	model.query.head_id='';
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,_sortField:"realname",rows:9999,_sort:"asc"},function(json){
    		model.userList=json.data.list;
    	});
    },
	query : {start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,head_id:'',head_name:''},
	qry : function() {
		Public.ajaxPost(rootPath + "/scm/salefunnel/qry.json",
				model.query.$model, function(json) {
			if(json.data=='')
				return;
			var ct=0;amt=0;
			for(var i=0;i<json.data[0].length;i++){
				ct+=json.data[0][i].value;
			}
			for(var i=0;i<json.data[1].length;i++){
				amt+=json.data[1][i].value;
			}
			var option = {title:{text: '销售漏斗',subtext: model.query.start_date+'至'+model.query.end_date+"<br>总数量："+ct+"，总金额："+fixedNum(amt,6)+"万元",x:'center'},
				    tooltip:{trigger: 'item',formatter: "{a}<br>{b}:{c}"},
				    calculable:true,
				    series:[{name:"数量",width:'40%',type:'funnel',data:json.data[0]},
				            {name:"金额(万元)",x:'50%',width:'40%',sort:'ascending',type:'funnel',data:json.data[1]}]
				};
			require(
			    ['echarts','echarts/chart/funnel'],
			    function (ec) {
			    	var chart=ec.init(document.getElementById("chart"));
			    	chart.setOption(option);
			    });
				});
	},
	init:function(){
		$(".ui-datepicker-input").datepicker();
		/**部门 combo START*//*
		var r1= $("#department");
		var i1=Public.comboTree(r1,{width:260,offset:{left:284,top:0},url:rootPath+'/sso/position/departMentTree.json',postData:{},
					callback : {
						beforeClick : function(e, t) {
							r1.val(t.name);model.query.department_id=(t.id);i1.hide();
						}
					}
				});
		*//**部门 combo END*/
		model.query={start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,head_id:'',head_name:''};
		model.qry();
	}
});
model.init();