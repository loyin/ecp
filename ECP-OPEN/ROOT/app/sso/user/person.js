var $_form=$("#base_form"),$_form_pwd=$("#pwd_form");
var model = avalon.define({
	$id:'ctrl',
	data:{},upImg:false,
	pwd:{pwd:"",pwd1:"",pwd2:""},
	setSex:function(v){
		model.data.sex=v;
	},
	submit:function(){
		$_form.trigger("validate");
	},
	savePwd:function(){
		$_form_pwd.trigger("validate");
	},
	setUpImg:function(){
		if(model.upImg)
			model.upImg=false;
		else
			model.upImg=true;
	}
});
function updateavatar(){//完成后调用
	var url=rootPath+"/upload/image/logo/";
	model.data.head_pic=(url+logoImgId+"_1.jpg?id="+(new Date()).getTime());
	model.upImg=false;
}
var THISPAGE = {
	init:function() {
		this.reset();
		this.initDom();
		this.initEvent();
	},
	reset:function(){
		model.data = {id:id,position_id:"",head_pic:"",position_name:"",uname:"",realname:"",sex:0,status:1,email:'',mobile:'',telephone:'',remark:'',address:''};
	},
	initDom:function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/user/qryOp.json",{id:id}, function(json){
				if(json.status==200){
//					avalon.mix(model.data,json.data)
					model.data=json.data;
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
		}
	},
	initEvent:function(){
		this.initValidator();
	},
	initValidator:function() {
		$_form.validator({
			messages:{
				required:"请填写{0}"
			},
//			display:function(e) {
//				return $(e).closest(".row-item").find("label").text()
//			},
			valid:function() {
				postData();
			},
			ignore:":hidden",
			theme:"yellow_bottom",
			timely:1,
			stopOnError:true
		});
		$_form_pwd.validator({
			messages:{
				required:"请填写{0}"
			},
//			display:function(e) {
//				return $(e).closest(".row-item").find("label").text()
//			},
			valid:function() {
				Public.ajaxPost(rootPath+"/sso/user/savePwd.json",model.pwd.$model, function(t) {
					if (200 == t.status) {
						parent.parent.Public.tips({
							content:"保存密码成功！"
						});
					} else
						parent.parent.Public.tips({
							type:1,
							content:"保存密码失败！" + t.msg
						});
				});
			},
			ignore:":hidden",
			theme:"yellow_right",
			timely:1,
			stopOnError:true
		});
	}
};
function postData(){
	Public.ajaxPost(rootPath+"/sso/user/savePersonSet.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content:"保存成功！"
			});
			$("#id").val(t.data);
		} else
			parent.parent.Public.tips({
				type:1,
				content:"保存失败！" + t.msg
			});
	});
}
THISPAGE.init();