var api = frameElement.api,oper = api.data.oper,id=api.data.id,$_form=$("#base_form"),
area=SYSTEM.area,custRating=SYSTEM.custRating,custParame=SYSTEM.custParame,typeList=custParame.typeList,url=rootPath+"/crm/customer";
var model = avalon.define({$id:'view',
	data:{address: "",amt: 0,city: "",email: "",ent_cate: "",ent_stage: "",fax: "",head_id:SYSTEM.user.id,head_name:SYSTEM.user.realname,id: "",industry: "",integral: 0,is_deleted: 0,member_card: "",mobile: "",name: "",origin: "",ownership: "",province: "",province_name: "",rating: "",remark: "",sn: "",staff_size: "",status: 0,telephone: "",type: 1,year_turnover: "",zip_code: ""},
	//首要联系人
	contacts:{name:"",type:1,sex:1,mobile:"",is_main:1,post:"",idcard:"",department:"",saltname:"",telephone:"",qq:"",email:"",address:"",zip_code:"",customer_id:"",description:"",id:""},
    provinceList:area.provinceList,custType:parent.type,
    cityList:[],
    tabActive:0,
    parameList:typeList,
    custRating:custRating.list,
    changeCity:function(v){
    	model.cityList=area[v+""];
    },
    showTab:function(i,b){
    	model.tabActive=i;
    },
    setType:function(v){
    	model.data.type=v;
    	model.contacts.type=v;
    },
    setSex:function(v){
    	model.contacts.sex=v;
    }
});
model.data.$watch("$all",function(name,a,b){
	if(a==null||a=="null"){
		model.data[name]="";
	}
});
model.contacts.$watch("$all",function(name,a,b){
	if(a==null||a=="null"){
		model.contacts[name]="";
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
					avalon.mix(model.data,json.data.customer);
					model.changeCity(model.data.province);
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
			Public.ajaxPost(rootPath+"/crm/contacts/mainContacts.json",{customer_id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.contacts,json.data);
				}});
		}
		/**负责人*/
		var r=$("#headCombo");
		var i=Public.comboTree(r,{offset:{top:0,left:94},url:rootPath+'/sso/user/userTree.json',postData:{type:2},callback:{
			beforeClick:function(e, t) {
				if(t.type==10){
					r.val(t.name);model.data.head_id=t.id;i.hide();
				}else{
					r.val("");model.data.head_id="";
				}
			}}
		});
		/**负责人combo END*/
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
	var e = "add" == oper ? "新增客户" : "修改客户";
	if(model.contacts.name==''){
		model.tabActive=1;
		parent.parent.Public.tips({
			type : 1,
			content : "联系人姓名必填！"
		});
		return;
	}
	Public.ajaxPost(url+"/save.json",model.data.$model, function(json) {
		if (200 == json.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=json.data.id;
			model.data.sn=json.data.sn;
			model.contacts.customer_id=json.data.id;
			Public.ajaxPost(rootPath+"/crm/contacts/save.json",model.contacts.$model, function(json_) {
				if(json_.status==200){
					model.contacts.id=json.data.id;
				}else{
					parent.parent.Public.tips({
						type : 1,
						content : "保存联系人失败！" + json_.msg
					});
				}
			});
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + json.msg
			});
	});
}
THISPAGE.init();