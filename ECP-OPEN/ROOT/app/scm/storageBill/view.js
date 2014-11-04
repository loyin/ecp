var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,type=parent.type;
var model = avalon.define({
	$id:'ctrl',parameList:typeList,type:type,
	data:{id:"",is_deleted:0,submit_status:0,remark:"",bill_date:"",billsn:"自动生成",head_id:"",head_name:"",pact:"",remark:"",depot_name:'',type:0,
		productlistlength:1,
		productlist:[{amount:0,amount2:0,product_id: "",unit:"",product_name: ""}]}
	,billType:parent.billType,totalAmount:0,
	init:function(){
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/storageBill/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					if(model.data.productlistlength==undefined||model.data.productlistlength==0){
						model.data.productlistlength=1;
						model.data.productlist=[{amount:0,product_id: "",unit:"",product_name: ""}];
					}
					jisuan();
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
	}
});
function jisuan(){
	model.totalAmount=0;
	for(var i=0;i<model.data.productlist.length;i++){
		model.totalAmount+=new Number(model.data.productlist[i].amount);
	}
}
model.init();