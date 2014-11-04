var api = frameElement.api,id=api.data.id;
var model = avalon.define({$id:'ctrl',
	data:{id:"",name:'',is_deleted:0,status:"",type:"",origin:"",gain_rate:"",estimate_price:0,remark:"",nextstep_date:"",nextstep:"",
		contacts_name:"",contacts_id:"",contacts_name:"",customer_id:"",customer_name:"",productlistlength:1,head_id:SYSTEM.user.id,head_name:SYSTEM.user.realname,
		productlist:[{amount:0,amt: null,description: "",id: "",product_id: "",product_name: "",purchase_price: null,quoted_price:0,
			sale_price:0,zhamt: null,zkl: null}]},
	init:function() {
		if(id!='')
			Public.ajaxPost(rootPath+"/crm/business/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					model.data.productlist=json.data.productlist;
					model.data.productlistlength=json.data.productlistlength;
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			})
		}});
model.init();