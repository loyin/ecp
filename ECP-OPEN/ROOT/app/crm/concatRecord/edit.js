var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({
	$id:'ctrl',parameList:typeList,
	data:{id:"",subject:'',concat_datetime:"",cust_status:"",concat_type:"",content:"",sale_stage:"",next_datetime:'',business_id:'',business_name:'',
			contacts_name:"",contacts_id:"",customer_id:"",customer_name:""},
	init:function(){
		model.data={id:"",subject:'',concat_datetime:"",cust_status:"",concat_type:"",content:"",sale_stage:"",next_datetime:'',business_id:'',business_name:'',
				contacts_name:"",contacts_id:"",customer_id:"",customer_name:""};
	},
	customerList:[],custComboV:false,
	chooseCust:function(e){
    	model.data.customer_id=e.id;
    	model.data.customer_name=e.name;
    	model.custComboV=false;
    },
    qryCustomer:function(v){//自动完成查询客户
    	model.custComboV=true;
    	Public.ajaxPost(rootPath+"/crm/customer/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc"},function(json){
    		model.customerList=json.data.list;
    	});
    },
    contactsList:[],contactsComboV:false,
    chooseContacts:function(e){
    	model.data.contacts_id=e.id;
    	model.data.contacts_name=e.name;
    	model.contactsComboV=false;
    },
    qryContacts:function(v){//自动完成查询联系人
    	if(model.data.customer_id==""){
    		parent.parent.Public.tips({type : 1,content : "请先选择客户！"});
    		return;
    	}
    	model.contactsComboV=true;
    	Public.ajaxPost(rootPath+"/crm/contacts/dataGrid.json",{keyword:v,customer_id:model.data.customer_id,_sortField:"name",_sort:"asc"},function(json){
    		model.contactsList=json.data.list;
    	});
    },
    bizList:[],bizComboV:false,
    chooseBiz:function(e){
    	model.data.business_id=e.id;
    	model.data.business_name=e.name;
    	model.bizComboV=false;
    },
    qryBiz:function(v){//自动完成查询商机
    	if(model.data.customer_id==""){
    		parent.parent.Public.tips({type : 1,content : "请先选择客户！"});
    		return;
    	}
    	model.bizComboV=true;
    	Public.ajaxPost(rootPath+"/crm/business/dataGrid.json",{keyword:v,customer_id:model.data.customer_id,_sortField:"name",_sort:"asc"},function(json){
    		model.bizList=json.data.list;
    	});
    }
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/crm/concatRecord/qryOp.json",{id:id}, function(json){
					avalon.mix(model.data,json.data);
					THISPAGE.initEvent();
			});
		}else{
			THISPAGE.initEvent();
		}
	},
	initBtn:function(){
		var e = "add" ==  api.data.oper ? [ "<i class='fa fa-save mrb'></i>保存", "关闭" ] : [ "<i class='fa fa-save mrb'></i>确定", "取消" ];
		api.button({
			id : "confirm",
			name : e[0],
			focus : !0,
			callback : function() {
				addNew=false;
				$_form.trigger("validate");
				return false
			}
		}, {
			id : "saveAndNew",
			name :"保存并新建",
			focus :false,
			callback : function() {
				addNew=true;
				$_form.trigger("validate");
				return false
			}
		}, {
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		this.initValidator();
	},
	initValidator:function() {
		$_form.validator({
			messages : {
				required : "请填写{0}"
			},
			display : function(e) {
				return $(e).closest(".row-item").find("label").text()
			},
			valid : function() {
				postData();
			},
			ignore : ":hidden",
			theme : "yellow_bottom",
			timely : 1,
			stopOnError : true
		});
	}
};
function postData(){
	var e = "add" == oper ? "新增联系记录" : "修改联系记录";
	Public.ajaxPost(rootPath+"/crm/concatRecord/save.json",model.data.$model
			, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			if(addNew){
				model.init();
			}else{
				model.data.id=(t.data);
			}
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();