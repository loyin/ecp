var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,type=parent.type,
s=["","","通过","拒绝"];
var model = avalon.define({
	$id:'ctrl',parameList:typeList,type:type,
	data:{id:"",name:'',is_deleted:0,status:"",ordertype:type,remark:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,sign_date:"",billsn:"",delivery_date:"",
			head_id:"",head_name:"",creater_name:"",create_datetime:"",updater_name:"",update_datetime:"",
			contacts_name:"",contacts_id:"",customer_id:"",customer_name:"",productlistlength:1,rebate:0,rebate_amt:0,order_amt:0,pact:"",
			productlist:[{amount:0,amt: null,description: "",id:"",product_id:"",unit:"",product_name: "",purchase_price:0,quoted_price:0,
				sale_price:0,zhamt:0,zkl:0,tax_rate:0,tax:0}]},
	auditData:{remark:"",audit_status:2,id:id,table:"scm_order"},
	sumzhamt:0,sumAmount:0,sumAmt:0,sumTax:0,sumPTax:0,auditDetailList:[],
	init:function(){
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/order/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					jisuan();
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
			Public.ajaxPost(rootPath+"/wf/auditDetail/list.json",{id:id}, function(json){
				if(json.status==200){
					model.auditDetailList=json.data;
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
		}
		api.button({
			id:"btn1",
			name :"通过",
			focus :true,
			callback:function() {
				model.auditData.audit_status=2;
				postData();
				return false
			}
		}, {
			id:"btn2",
			name :"拒绝",
			focus:false,
			callback:function() {
				model.auditData.audit_status=3;
				postData();
				return false
			}
		}, {
			id:"cancel",
			name :"取消"
		});
	}
});
avalon.filters.auditor_status=function(v){
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
function postData(){
	Public.ajaxPost(rootPath+"/scm/order/saveAudit.json",model.auditData.$model,function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({content:s[model.auditData.audit_status]+"成功！"});
			model.data.id=t.data.id;
			model.data.billsn=t.data.sn;
			api.button({id:"cancel",name :"取消"	});
			parent.model.reloadData(null);
		} else
			parent.parent.Public.tips({type:1,content:s[model.auditData.audit_status]+"失败！" + t.msg});
	});
}
model.init();