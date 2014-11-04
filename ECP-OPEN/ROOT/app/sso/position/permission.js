var api = frameElement.api,oper = api.data.oper,id=api.data.id,
$_form=$("#base_form"),addNew=false,url=rootPath+"/sso/position";
var model=avalon.define({
	$id:'ctrl',
	data:{code:[]},
	checkAll:false
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		$("#id").val(id);
		var name=api.data.name;
		name=name.replace("├","").replace("-","").replace("-","").replace(" ","").replace("&nbsp;","");
		$("#position").val(name);
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(url+"/qryPermission.json",{id:id}, function(json){
				if(json.status==200){
					var s=json.data;
					if(s!=null&&s!=''&&s!='null'){
						var ss=s.split(",");
//						for(var i=0;i<ss.length;i++){
//							$(":checkbox[value='"+ss[i]+"']").click();
//						}
						model.data.code=ss;
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
		var e =["<i class='fa fa-save mrb'></i>保存", "关闭" ];
		api.button({
			id : "confirm",
			name : e[0],
			focus : !0,
			callback : function() {
				postData();
				return false
			}
		}, {
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		$("#chk_all").click(function(){
		     $(":checkbox[name='code']").attr("checked",this.checked);
		});
	}
};
function postData(){
	var e="设置权限";
	Public.ajaxPost(url+"/savePermission.json",$_form.serialize(), function(t){
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();