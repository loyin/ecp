var api = frameElement.api,oper = api.data.oper,id=api.data.id,custParame=SYSTEM.custParame,typeList=custParame.typeList,
$_form=$("#base_form"),addNew=false,type=parent.type;
var model = avalon.define({
	$id:'ctrl',parameList:typeList,type:type,
	data:{id:"",is_deleted:0,submit_status:0,remark:"",bill_date:"",billsn:"自动生成",head_id:SYSTEM.user.id,head_name:SYSTEM.user.name,remark:"",depot_id:'',
			productlistlength:1,
			productlist:[{amount:0,amount2:0,product_id:"",unit:"",product_name: ""}]},
	depotList:[],userList:[],
    qryHead:function(v){
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,status:1,_sortField:"name",_sort:"asc"},function(json){
    		model.userList=json.data.list;
    	});
    },
    //添加商品
    productlist:[],productComboDiv:-1,
    qryProduct:function(v,idx){//自动完成查商品
    	model.productComboDiv=idx;
    	Public.ajaxPost(rootPath+"/scm/stock/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc",depot_id:model.data.depot_id},function(json){
    		model.productlist=json.data.list;
    	});
    },
    chooseProduct:function(e,idx){
    	model.data.productlist[idx].product_id=e.product_id;
    	model.data.productlist[idx].product_name=e.product_name;
    	model.data.productlist[idx].unit=e.unit;
    	model.data.productlist[idx].amount=e.amount;
    	model.productComboDiv=-1;
    },
    addRow:function(){
    	model.data.productlist.push({product_id:'',unit:"",amount:0,amount2:0,product_name:""});
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
var THISPAGE = {
	init : function() {
		model.qryHead();
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
		Public.ajaxPost(rootPath+"/scm/depot/list.json",{}, function(json){
			if(json.status==200){
				model.depotList=json.data;
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		});
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/stockCheck/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					if(model.data.productlistlength==undefined||model.data.productlistlength==0){
						model.data.productlistlength=1;
						model.data.productlist=[{amount:0,amount2:0,product_id: "",unit:"",product_name: ""}];
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
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		this.initValidator();
	},
	initValidator:function() {
		$_form.validator({
			valid : function() {
				postData();
			},
			ignore : ":hidden",
			theme : "yellow_top",
			timely : 1,
			stopOnError : true
		});
	}
};
function postData(){
	var e = "add" == oper ? "新增盘点" : "修改盘点";
	model.data.productlistlength=model.data.productlist.length;
	model.audit_status=0;
	Public.ajaxPost(rootPath+"/scm/stockCheck/save.json",model.data.$model
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