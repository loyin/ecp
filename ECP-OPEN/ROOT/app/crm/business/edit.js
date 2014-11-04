var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,custParame=SYSTEM.custParame,typeList=custParame.typeList,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({
	$id:'ctrl',parameList:typeList,
	data:{id:"",name:'',due_date:"",is_deleted:0,status:"",type:"",origin:"",gain_rate:"",estimate_price:0,remark:"",nextstep_date:"",nextstep:"",
			contacts_name:"",contacts_id:"",contacts_name:"",is_end:0,customer_id:"",customer_name:"",head_id:SYSTEM.user.id,head_name:SYSTEM.user.realname,
			productlistlength:1,productlist:[{amount:0,amt: null,description: "",id: "",product_id: "",product_name: "",purchase_price: null,quoted_price:0,
				sale_price:0,zhamt: null,zkl: null}]},
	init:function(){
		model.data={id:"",name:'',is_deleted:0,status:"",type:"",origin:"",gain_rate:"",estimate_price:0,remark:"",nextstep_date:"",nextstep:"",
				contacts_name:"",contacts_id:"",contacts_name:"",customer_id:"",customer_name:"",productlistlength:1,head_id:SYSTEM.user.id,head_name:SYSTEM.user.realname,
				productlist:[{amount:0,amt: null,description: "",id: "",product_id: "",product_name: "",purchase_price: null,quoted_price:0,
					sale_price:0,zhamt: null,zkl: null}]};
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
    userList:[],
    qryHead:function(v){
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,status:1,_sortField:"realname",rows:9999,_sort:"asc"},function(json){
    		model.userList=json.data.list;
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
    //添加商品
    productlist:[],productComboDiv:-1,
    qryProduct:function(v,idx){//自动完成查商品
    	model.productComboDiv=idx;
    	Public.ajaxPost(rootPath+"/scm/product/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc"},function(json){
    		model.productlist=json.data.list;
    	});
    },
    chooseProduct:function(e,idx){
    	model.data.productlist[idx].product_id=e.id;
    	model.data.productlist[idx].product_name=e.name;
    	model.data.productlist[idx].sale_price=e.sale_price;
    	model.data.productlist[idx].purchase_price=e.purchase_price;
    	model.productComboDiv=-1;
    },
    addRow:function(){
    	model.data.productlist.push({product_id:'',description:"",quoted_price:"",purchase_price:"",sale_price:"",amount:"",product_name:""});
    },//增加商品行;
    delRow:function(e){//删除商品行
    	if(model.data.productlist.length==1){
    		parent.parent.Public.tips({type : 1,content : "至少输入一个商品信息"});
    		return;
    	}
    	e.preventDefault()
        var item = this.$vmodel.$remove();
    	model.data.productlist.remove(item);
    }
});
function jisuan(v){//计算预计金额 注意：对于数组不能使用$watch 因为只能监听length。
	model.data.estimate_price=0;
	for(var i=0;i<model.data.productlist.length;i++){
		var el=model.data.productlist[i];
		model.data.estimate_price=new Number(model.data.estimate_price)+(new Number(el.amount)*new Number(el.quoted_price));
	}
};
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
		model.qryHead();
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/crm/business/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					model.data.productlist=json.data.productlist;
					model.data.productlistlength=json.data.productlistlength;
					if(json.data.productlistlength==0){
						model.data.productlistlength=1;
						model.data.productlist=[{amount:0,amt: null,description: "",id: "",product_id: "",product_name: "",purchase_price: null,quoted_price:0,
							sale_price:0,zhamt: null,zkl: null}];
					}
					THISPAGE.initEvent();
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
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
	var e = "add" == oper ? "新增商机" : "修改商机";
	model.data.productlistlength=model.data.productlist.length;
	model.data.is_end=custParame[model.data.status].is_end;
	Public.ajaxPost(rootPath+"/crm/business/save.json",model.data.$model
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
			parent.model.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();