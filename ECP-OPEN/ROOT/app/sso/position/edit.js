var api = frameElement.api,oper = api.data.oper,id=api.data.id,
$_form=$("#base_form"),addNew=false;
var model=avalon.define({
	$id:"ctrl",
	data:{parent_name:"",is_head:null,company_id:"",department_id:"",department_name:"",parentids:"",description:"",departids:"",permission:"",type:0,m:0,parent_id:"",quota:1,sort_num:2,name:"",id:""},
	setIs_head:function(v){model.data.is_head=v;},
	setM:function(v){model.data.m=v;},
	init:function(){
		model.data={parent_name:"",is_head:null,company_id:"",department_id:"",department_name:"",parentids:"",description:"",departids:"",permission:"",type:0,m:0,parent_id:"",quota:1,sort_num:2,name:"",id:""};
	}
});
model.data.$watch("$all",function(name,a,b){
	if(a==null||b==null)
		model.data[name]="";
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		model.data.type=(api.data.type);
		if(api.data.department_id){
			model.data.department=(api.data.department_name);
			model.data.department_id=(api.data.department_id);
		}
		if(api.data.pid){
			model.data.position=(api.data.parent_name);
			model.data.parent_depart=(api.data.parent_name);
			model.data.parent_id=(api.data.parent_id);
		}
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/position/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
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
			name : "<i class='fa fa-save mrb'></i>保存并新建",
			focus :false,
			callback : function() {
				$_form.trigger("validate");
				addNew=true;
				return false
			}
		}, {
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		if(api.data.type==1){
		/**上级岗位combo START*/
		var r = $("#position");
		var i=Public.comboTree(r,{width:260,offset:{left:122,top:0},url:rootPath+'/sso/position/tree.json',
				callback : {
					beforeClick : function(e, t) {
						if(t.type==1&&t.id!=model.data.id){
							r.val(t.name);model.data.parent_id=t.id;i.hide();
						}else{
							r.val("");model.data.parent_id="";
						}
					}
				}
			});
		/**上级岗位combo END*/
		/**部门 combo START*/
		var r1= $("#department");
		var i1=Public.comboTree(r1,{width:260,offset:{left:122,top:0},url:rootPath+'/sso/position/departMentTree.json',postData:{},
					callback : {
						beforeClick : function(e, t) {
							r1.val(t.name);model.data.department_id=(t.id);i1.hide();
						}
					}
				});
		}
		/**部门 combo END*/
		if(api.data.type==0){
			/**上级部门 combo START*/
			var r = $("#parent_depart");
			var i=Public.comboTree(r,{width:260,offset:{left:122,top:0},url:rootPath+'/sso/position/departMentTree.json',postData:{},
						callback : {
							beforeClick : function(e, t) {
								if(t.id!=model.data.id){
									r.val(t.name);model.data.parent_id=(t.id);i.hide();
								}else{
									r.val("");model.data.parent_id=("");
								}
							}
						}
					});
			/**上级部门 combo END*/
		}
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
	var tit=(model.data.type==0?"部门":"岗位");
	var e = "add" == oper ? "新增" : "修改"+tit;
	Public.ajaxPost(rootPath+"/sso/position/save.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=(t.data);
			parent.THISPAGE.reloadData(null);
			if(addNew){
				model.init();
			}else{
				api.close();
			}
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();