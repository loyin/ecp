var api = frameElement.api,oper = api.data.oper,id=api.data.id,$_form=$("#base_form"),
custParame=SYSTEM.custParame,typeList=custParame.typeList,url=rootPath+"/crm/contacts";
var model = avalon.define({$id:'view',
	data:{name:"",sex:1,mobile:"",is_main:1,post:"",department:"",saltname:"",telephone:"",idcard:"",qq:"",email:"",address:"",zip_code:"",contacts_id:"",description:"",id:"",customer_id:"",customer_name:"",custType:1},
    parameList:typeList,customerList:[],custComboV:false,
    init:function(){},
    setSex:function(v){
    	model.data.sex=v;
    },
    chooseCust:function(e){
    	model.data.customer_id=e.id;
    	model.data.customer_name=e.name;
    	model.custComboV=false;
    },
    customerDialog:function(){
    	
    },
    qryCustomer:function(v){//自动完成查询客户
    	model.custComboV=true;
    	Public.ajaxPost(rootPath+"/crm/customer/dataGrid.json",{keyword:v},function(json){
    		model.customerList=json.data.list;
    	});
    }
});
avalon.filters.type=function(v){
	var sex=["供应商","客户","客户"];
	return sex[v];
}
model.data.$watch("$all",function(name,a,b){
	if(a==null||a=="null"){
		model.data[name]="";
	}
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(url+"/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
		/**客户*/
		var r=$("#headCombo");
		/*var i=Public.comBox(r,{url:rootPath+'/sso/customer/combo.json',postData:{type:1},callback:{
			beforeClick:function(e, t) {
				if(t.type==10){
					r.val(t.name);model.data.head_id=t.id;i.hide();
				}else{
					r.val("");model.data.head_id="";
				}
			}}
		});*/
		/**客户combo END*/
		THISPAGE.initEvent();
	},
	initBtn:function(){
		var e = "add" ==  api.data.oper ? [ "<i class='fa fa-save mrb'></i>保存", "关闭" ] : [ "<i class='fa fa-save mrb'></i>确定", "取消" ];
		api.button({
			id : "confirm",
			name : e[0],
			focus : !0,
			callback : function() {
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
	var e = "add" == oper ? "新增联系人" : "修改联系人";
	Public.ajaxPost(url+"/save.json",model.data.$model, function(json) {
		if (200 == json.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=json.data.id;
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + json.msg
			});
	});
}
THISPAGE.init();