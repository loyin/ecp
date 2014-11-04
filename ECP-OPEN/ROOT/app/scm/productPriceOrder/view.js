var categoryData,api = frameElement.api,oper = api.data.oper,id=api.data.id,
$_form=$("#base_form"),addNew=false;
var model=avalon.define({$id:"ctrl",
	data:{company_id: "",create_datetime: "",creater_id: "",creater_realname: "",end_date: "",id: "",remark: "",start_date: "",subject: "",submit_status: 0,
		productlistlength:1,productlist:[{amount:"",id: "",product_id:"",product_name: "",cost:"",price:""}]}
});
var THISPAGE = {
	init: function() {
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/productPriceOrder/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					model.data.productlist=json.data.productlist;
					model.data.productlistlength=json.data.productlistlength;
					if(json.data.productlistlength==0){
						model.data.productlistlength=1;
						model.data.productlist=[{amount:"",id: "",product_id:"",product_name: "",cost:"",price:""}];
					}
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
	}
};
THISPAGE.init();