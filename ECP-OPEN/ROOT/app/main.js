var model=avalon.define({
	$id:"mainCtrl",mainInfo:{},dailyPhrase:{},
	user:SYSTEM.user,month_over:0,concatRecordList:[],noticeList:[],
	init:function(){
		Public.ajaxPost(rootPath+"/sso/dailyPhrase/random.json",{},function(json){
			model.dailyPhrase=json.data;
		});
		
		if(showByRights('A1_1_S')){//客户模块查看权限
			Public.ajaxPost(rootPath+"/sso/user/mainInfo.json",{},function(json){
				model.mainInfo=json.data;
			});
			Public.ajaxPost(rootPath+"/crm/concatRecord/dataGrid.json",{rows:10},function(json){
				model.concatRecordList=json.data.list;
			});
			//计算本月剩余天数
			var date=new Date(SYSTEM.date);
			var year=date.getFullYear(),month=date.getMonth()+1,day=date.getDate();
			if(month==2){
				if(yeay%4!=0){
					model.month_over=28-day;
				}else{
					model.month_over=29-day;
				}
			}else if(month==1||month==3||month==5||month==7||month==8||month==10||month==12){
				model.month_over=31-day;
			}else if(month==4||month==6||month==9||month==11){
				model.month_over=30-day;
			}
		}
		if(showByRights('A3_1_S')){//销售
			/**销售漏斗*/
			require.config({
			    paths:{ 
			        'echarts':rootPath+'/assets/js/echarts/echarts',
			        'echarts/chart/funnel': rootPath+'/assets/js/echarts/echarts'
			    }
			});
			Public.ajaxPost(rootPath+"/scm/salefunnel/my.json",{},function(json){
				var option = {title:{text: '我的销售漏斗',subtext: '本月',x:'center'},
					    tooltip:{trigger: 'item',formatter: "{a}<br>{b}:{c}"},
					    calculable:true,
					    series:[{name:"数量",width:'40%',type:'funnel',data:json.data[0]},
					            {name:"金额(万元)",x:'50%',width:'40%',sort:'ascending',type:'funnel',data:json.data[1]}]
					};
				require(
				    ['echarts','echarts/chart/funnel'],
				    function (ec) {
				    	var chart=ec.init(document.getElementById("salefunnel"));
				    	chart.setOption(option);
				    });
			});
		}
	},
	mdfSaleGoal:function(){//管理我的销售目标
		$.dialog({
			title:"管理我的销售目标",
			content:"url:"+rootPath+"/em/saleGoal/edit.html",
			data:{rowId:'-1'},
			width:1000,
			height:300,
			max:true,resize:true,
			min:false,
			cache:false,
			lock:true
		})
	}
});
model.init();