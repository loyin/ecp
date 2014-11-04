var api = frameElement.api,oper = api.data.oper,id=api.data.id,
m=["非管理类","管理类"],is_head=["否","是"]
$_form=$("#base_form"),addNew=false;
var model = avalon.define({$id:'view',data:{}});
avalon.filters.isHead=function(str){
	return is_head[str];
}
avalon.filters.m=function(str){
	return m[str];
}
var
THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		$(".type"+api.data.type).show();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/position/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
	},
	initBtn:function(){
		api.button({
			id : "cancel",
			name :"关闭"
		});
	}
};
THISPAGE.init();