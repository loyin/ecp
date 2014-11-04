var url=rootPath+"/fa/payReceivAbles",gridQryUrl=url+"/wlqkList.json",csttype=["供应商","个人客户","企业客户","经销商"];
var model = avalon.define({$id:'view',
	user:SYSTEM.user,
    query :{keyword:"",start_date:SYSTEM.beginDate,type:type,end_date:SYSTEM.endDate,csttype:'',customer_id:''},
    list:[],amt0_t:0,amt1_t:0,nprint:true,csttypeList:csttype,
    customerList:[],custComboV:false,
	chooseCust:function(e){
    	model.query.customer_id=e.id;
    	model.custComboV=false;
    },
    qryCustomer:function(v){//自动完成查询客户
    	model.custComboV=true;
    	Public.ajaxPost(rootPath+"/crm/customer/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc"},function(json){
    		model.customerList=json.data.list;
    	});
    },
	init:function(){
		$(".ui-datepicker-input").datepicker();
		model.loadData();
	},
	loadData:function(){
		Public.ajaxPost(gridQryUrl,model.query.$model, function(json){
			if(json.status==200){
				model.list=json.data;
				if(model.list.length>0){
					model.amt0_t=0;
					model.amt1_t=0;
					for(var i=0;i<model.list.length;i++){
						var a=model.list[i];
						model.amt0_t+=new Number(a.amt0);
						model.amt1_t+=new Number(a.amt1);
					}
				}
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