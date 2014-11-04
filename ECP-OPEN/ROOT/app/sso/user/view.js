var api = frameElement.api,id=api.data.id,parame={sex:["女","男"],status:["禁用","激活"]};
var model = avalon.define({$id:'ctrl',data:{}});
/**自定义filter 用法：{{model.attr|isHead}}*/
avalon.filters.sex=function(str){
	return parame.sex[str];
}
avalon.filters.status=function(str){
	return parame.status[str];
}
var THISPAGE = {
	init : function() {
		this.initDom();
	},
	initDom : function() {
		if(id!='')
		Public.ajaxPost(rootPath+"/sso/user/qryOp.json",{id:id}, function(json){
			if(json.status==200){
				model.data=json.data;
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		})
	}
};
THISPAGE.init();