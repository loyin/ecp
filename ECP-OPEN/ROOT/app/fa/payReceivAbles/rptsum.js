var url=rootPath+"/fa/payReceivAbles",gridQryUrl=url+"/rptSumList.json",csttype=["供应商","个人客户","企业客户","经销商"],
ordertype=["采购","采购退货","销售","销售退货"];
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{keyword:"",//start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,
    	type:type,custtype:'',status:'',customer_name:'',customer_id:''},
    list:[],userData:{amt0:0,amt1:0,amt2:0,amt3:0,amt4:0,amt5:0},nprint:true,
    customerList:[],custComboV:false,type_:type,
    setTyp:function(v){
    	model.typ=v;
    },
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
				model.list=json.data;
				model.userData.amt0=0;
				model.userData.amt1=0;
				model.userData.amt2=0;
				for(var i=0;i<model.list.length;i++){
					var o=model.list[i];
					model.userData.amt0+=o.amt;
					model.userData.amt1+=o.pay_amt;
					model.userData.amt2+=o.amt-o.pay_amt;
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