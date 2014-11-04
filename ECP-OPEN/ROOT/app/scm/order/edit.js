var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,
$_form=$("#base_form"),addNew=false,type=parent.type,editor;
var model = avalon.define({
	$id:'ctrl',parameList:typeList,type:type,
	data:{id:"",name:'',is_deleted:0,status:"",ordertype:type,remark:"",start_date:"",end_date:"",sign_date:SYSTEM.date,billsn:"自动生成",delivery_date:SYSTEM.date,
			head_id:SYSTEM.user.id,head_name:SYSTEM.user.realname,pact:"",remark:"",audit_status:0,ordersn:"",
			contacts_name:"",contacts_id:"",customer_id:"",customer_name:"",productlistlength:1,rebate:0,rebate_amt:0,order_amt:0,
			productlist:[{amount:0,amt: null,description: "",id: "",product_id: "",unit:"",product_name: "",purchase_price:0,quoted_price:0,
				sale_price:0,zhamt:0,zkl:0,tax_rate:0,tax:0}]},
	sumzhamt:0,sumAmount:0,sumAmt:0,sumTax:0,sumPTax:0,
	userList:[],
    qryHead:function(v){
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,status:1,_sortField:"realname",rows:9999,_sort:"asc",rows:9999},function(json){
    		model.userList=json.data.list;
    	});
    },
	customerList:[],custComboV:false,
	chooseCust:function(e){
    	model.data.customer_id=e.id;
    	model.data.customer_name=e.name;
    	model.custComboV=false;
    },
    qryCustomer:function(v){//自动完成查询客户
    	model.custComboV=true;
    	Public.ajaxPost(rootPath+"/crm/customer/dataGrid.json",{keyword:v,_sortField:"realname",_sort:"asc",type:type>1?-1:0},function(json){
    		model.customerList=json.data.list;
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
    	model.data.productlist[idx].product_id=e.product_id;
    	model.data.productlist[idx].product_name=e.product_name;
    	if(type>1)//销售
    	model.data.productlist[idx].sale_price=e.sale_price;
    	else//采购
    	model.data.productlist[idx].sale_price=e.purchase_price;
    	model.data.productlist[idx].purchase_price=e.purchase_price;//将采购价记录到订单中
    	model.data.productlist[idx].unit=e.unit;
    	model.productComboDiv=-1;
    },
    addRow:function(){
    	model.data.productlist.push({product_id:'',description:"",quoted_price:0,unit:"",purchase_price:0,zhamt:0,zkl:0,tax_rate:0,tax:0,amt:"",sale_price:"",amount:"",product_name:""});
    },//增加商品行;
    delRow:function(e){//删除商品行
    	if(model.data.productlist.length==1){
    		parent.parent.Public.tips({type : 1,content : "至少输入一个商品信息"});
    		return;
    	}
    	e.preventDefault()
        var item = this.$vmodel.$remove();
    	model.data.productlist.remove(item);
    	jisuan();
    }
});
model.data.productlist.$watch("length",function(name,a,b){
	jisuan();
});
function jisuan(v){//计算合计 注意：对于数组不能使用$watch 因为只能监听length。
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
	if(v==0)
		model.data.rebate_amt =(model.sumPTax*model.data.rebate/100).toFixed(2);
	else{
		if(model.sumPTax>0)
		model.data.rebate=(model.data.rebate_amt/model.sumPTax*100).toFixed(3);
	}
	model.data.order_amt=model.sumPTax-model.data.rebate_amt;
};
var THISPAGE = {
	init : function() {
		model.qryHead();
		this.initDom();
		this.initBtn();
//		setTimeout(jisuan,300);
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
		THISPAGE.initEvent();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/order/qryOp.json",{id:id}, function(json){
				if(json.status==200){
//					avalon.mix(model.data,json.data);
					model.data=json.data;
					model.data.productlist=json.data.productlist;
					model.data.productlistlength=json.data.productlistlength;
//					setTimeout(function(){editor.setContent(json.data.pact);},500);
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
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
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		editor=UE.getEditor("editor",{});
		this.initValidator();
		editor.ready(function() {
			jisuan();
		   editor.setContent(model.data.pact);
		});
	},
	initValidator:function() {
		$_form.validator({
//			messages : {
//				required : "请填写{0}"
//			},
//			display : function(e) {
//				return $(e).data("msg");
//			},
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
	var e = "add" == oper ? "新增订单" : "修改订单";
	model.data.productlistlength=model.data.productlist.length;
	model.audit_status=0;
	if(model.data.productlistlength==0){
		parent.parent.Public.tips({type : 1,content : "商品必须选择"});
		return;
	}
	Public.ajaxPost(rootPath+"/scm/order/save.json",model.data.$model
			, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=t.data.id;
			model.data.billsn=t.data.sn;
			parent.model.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();