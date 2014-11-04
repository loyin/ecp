var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({
	$id:'ctrl',
	data:{id:"",name:'',code:"",qz:"",qyrq:0,rqgs:"",dqxh:1,sfmrgx:0,udate:"",ws:4},
	gxzqSel:["不更新","每天","每月","每年"],
	setQyrq:function(v){
		model.data.qyrq=v;
		if(v==0)
			model.data.rqgs="";
		else
			model.data.rqgs="yyyyMMdd";
	},
	init:function(){
		model.data={id:"",name:'',code:"",qz:"",qyrq:0,rqgs:"",dqxh:1,sfmrgx:0,udate:"",ws:4};
	}
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/snCreater/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data
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
			id : "AD",
			name :"保存并新建",
			focus : !0,
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
	var e = "add" == oper ? "新增序号规则" : "修改序号规则";
	Public.ajaxPost(rootPath+"/sso/snCreater/save.json",model.data.$model
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