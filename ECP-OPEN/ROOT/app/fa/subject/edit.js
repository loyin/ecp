var parameData,api = frameElement.api,oper = api.data.oper,id=api.data.id,parent_id=api.data.parent_id,
parent_name=api.data.parent_name,
category_=api.data.category,type_=api.data.type,

$_form=$("#base_form"),addNew=false,direction=parent.direction,
type=parent.type,category=parent.category,category_json=parent.category_json;
var model = avalon.define({$id:'ctrl',
data:{id:"",name:"",code:"",level:1,direction:0,parent_name:"",parent_id:"",type:0,category:0,description:""},
category_json:[],
type:type,
direction:direction,
setDirection:function(v){
	model.data.direction=v;
},
typeChange:function(v){
	model.category_json=category_json[v];
}
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	reset:function(){
	},
	initDom : function() {
		if(parent_id!=undefined&&parent_id!=null&&parent_id!='null')
			model.data.parent_id=parent_id;
		if(parent_name!=undefined&&parent_name!=null&&parent_name!='null')
			model.data.parent_name=parent_name;
		if(type_!=undefined&&type_!=null&&type_!='null')
			model.data.typ=type_;
		if(category_!=undefined&&category_!=null&&category_!='null')
		model.data.category=category_;
		model.typeChange(model.data.type);
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/subject/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					if(model.data.parent_name==null){
						model.data.parent_name="";
					}
					if(model.data.parent_id==null){
						model.data.parent_id="";
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
	var e = "add" == oper ? "新增" : "修改"+"科目";
	Public.ajaxPost(rootPath+"/fa/subject/save.json",$_form.serialize(), function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			$("#id").val(t.data);
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();