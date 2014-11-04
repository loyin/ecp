var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({$id:'ctrl',
	data:{id:"",position_id:"",position_name:"",uname:"",password:"",realname:"",sex:0,status:1,email:'',mobile:'',telephone:'',remark:'',address:''},
	setSex:function(v){model.data.sex=v;},
	setStatus:function(v){model.data.status=v;}
});
model.data.$watch("$all",function(name,a,b){
	if(a==null||b==null){
		model.data[name]="";
		console.log(model.data[name]+":"+name);
	}
});
var THISPAGE = {
	init:function() {
		this.reset();
		this.initDom();
		this.initBtn();
	},
	reset:function(){
		model.data = {id:"",position_id:"",position_name:"",uname:"",password:"",realname:"",sex:0,status:1,email:'',mobile:'',telephone:'',remark:'',address:''};
	},
	initDom:function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/user/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					THISPAGE.initEvent();
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
		}else{
			THISPAGE.initEvent();
		}
		/**岗位*/
		var r=$("#position");
		var i=Public.comboTree(r,{url:rootPath+'/sso/position/tree.json',callback:{
			beforeClick:function(e, t) {
				if(t.type==1){
					r.val(t.name);r.data("pid", t.id);model.data.position_id=(t.id);i.hide();
				}else{
					r.val("");r.data("pid","");model.data.position_id=("");
				}
			}}
		});
		/**岗位combo END*/
	},
	initBtn:function(){
		var e = "add" ==  api.data.oper ? [ "<i class='fa fa-save mrb'></i>保存", "关闭" ]:[ "<i class='fa fa-save mrb'></i>确定", "取消" ];
		api.button({
			id:"confirm",
			name:e[0],
			focus:!0,
			callback:function() {
				addNew=false;
				$_form.trigger("validate");
				return false
			}
		}, {
			id:"saveAndNew",
			name:"<i class='fa fa-save mrb'></i>保存并新建",
			focus:!0,
			callback:function() {
				$_form.trigger("validate");
				addNew=true;
				return false
			}
		}, {
			id:"cancel",
			name:e[1]
		})
	},
	initEvent:function(){
		this.initValidator();
	},
	initValidator:function() {
		$_form.validator({
			rules:{
				type:[ /^[a-zA-Z0-9]*$/, "编号只能由数字、字母等字符组成" ],
			},
			messages:{
				required:"请填写{0}"
			},
			fields:{
				uname:{
					rule:"add" === oper ? "required; type"
							: "required; type",
					timely:3
				},
				realname:"required;"
			},
			display:function(e) {
				return $(e).closest(".row-item").find("label").text()
			},
			valid:function() {
				postData();
			},
			ignore:":hidden",
			theme:"yellow_bottom",
			timely:1,
			stopOnError:true
		});
	}
};
function postData(){
	var e = "add" == oper ? "新增用户":"修改用户";
	var val=model.data.position_id;
	if(val=="0"||val==0)
	{
		parent.parent.Public.tips({type:1,content:"岗位未选择！"});
		return;
	}
	$("#position_id").val(val);
	if(model.data.id==""&&model.data.password==""){
		parent.parent.Public.tips({type:1,content:"新增用户时请输入密码！"});
		return;
	}
	Public.ajaxPost(rootPath+"/sso/user/save.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content:e + "成功！"
			});
			$("#id").val(t.data);
			parent.THISPAGE.reloadData(null);
			if(addNew)
				THISPAGE.reset();
		} else
			parent.parent.Public.tips({
				type:1,
				content:e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();