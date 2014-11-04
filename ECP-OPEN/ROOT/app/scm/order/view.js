var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,type=parent.type,
s=["","","通过","拒绝"];
var model = avalon.define({
	$id:'ctrl',parameList:typeList,type:type,audit_hidden:parent.audit_hidden,
	data:{id:"",name:'',is_deleted:0,status:"",ordertype:type,remark:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,sign_date:"",billsn:"",delivery_date:"",
			head_id:"",head_name:"",creater_name:"",create_datetime:"",updater_name:"",update_datetime:"",
			contacts_name:"",contacts_id:"",customer_id:"",customer_name:"",productlistlength:1,rebate:0,rebate_amt:0,order_amt:0,pact:"",
			productlist:[{amount:0,amt: null,description: "",id: "",product_id: "",unit:"",product_name: "",purchase_price:0,quoted_price:0,
				sale_price:0,zhamt:0,zkl:0,tax_rate:0,tax:0}]},
	sumzhamt:0,sumAmount:0,sumAmt:0,sumTax:0,sumPTax:0,auditDetailList:[],
	init:function(){
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/order/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					jisuan();
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
			if(parent.audit_hidden==false)
			Public.ajaxPost(rootPath+"/wf/auditDetail/list.json",{id:id}, function(json){
				if(json.status==200){
					model.auditDetailList=json.data;
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
		}
	}
});
avalon.filters.audit_status=function(v){
	return s[v];
}
function jisuan(){//计算合计
	model.sumzhamt=0;
	model.sumAmount=0;
	model.sumAmt=0;
	model.sumTax=0;
	model.sumPTax=0;
	for(var i=0;i<model.data.productlist.length;i++){
		var el=model.data.productlist[i];
		model.sumzhamt=new Number(model.sumzhamt)+new Number(el.zhamt);
		model.sumAmount=new Number(model.sumAmount)+new Number(el.amount);
		model.sumAmt=new Number(model.sumAmt)+new Number(el.amt);
		model.sumTax=new Number(model.sumTax)+new Number(el.tax);
	}
	model.sumPTax=model.sumAmt+model.sumTax;
};
model.init();