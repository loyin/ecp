var url=rootPath+"/fa/account",gridQryUrl=url+"/rptList.json";
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,custtype:'',account_id:""},
    list:[],nprint:true,accountList:[],
	init:function(){
		$(".ui-datepicker-input").datepicker();
		Public.ajaxPost(url+"/dataGrid.json",{}, function(json){
			if(json.status==200){
				model.accountList=json.data.list;
			}
		});
//		model.loadData();
	},
	loadData:function(){
		Public.ajaxPost(gridQryUrl,model.query.$model, function(json){
			if(json.status==200){
				model.list=json.data;
			}
		});
	},
	printRpt:function(){
		model.nprint=false;
		window.print();
		model.nprint=true;
	}
});
model.init();
avalon.filters.ordertype=function(v){
	return ordertype[v];
}
avalon.filters.csttype=function(v){
	return csttype[v];
}