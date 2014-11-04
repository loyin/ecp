var url=rootPath+"/scm/order",gridQryUrl=url+"/rptList.json",csttype=["供应商","个人客户","企业客户","经销商"],
ordertype=["采购","采购退货","销售","销售退货"];
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{keyword:"",start_date:SYSTEM.beginDate,type:type,end_date:SYSTEM.endDate,custtype:'',status:'',customer_name:'',customer_id:''},
    list:[],userData:{},nprint:true,
    customerList:[],custComboV:false,type_:type,
	chooseCust:function(e){
    	model.query.customer_id=e.id;
    	model.query.customer_name=e.name;
    	model.custComboV=false;
    },
    qryCustomer:function(v){//自动完成查询客户
    	model.custComboV=true;
    	Public.ajaxPost(rootPath+"/crm/customer/dataGrid.json",{keyword:v,type:type==0?0:'',_sortField:"name",_sort:"asc"},function(json){
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
				model.list=json.data.list;
				model.userData=json.data.userData;
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