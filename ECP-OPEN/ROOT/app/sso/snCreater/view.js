var api = frameElement.api,id=api.data.id;
var model = avalon.define({$id:'ctrl',data:{id:"",name:''}});
avalon.filters.yn=function(str){
	var ysn=["否","是"];
	return ysn[str];
}
avalon.filters.gxtyp=function(str){
	var ysn=["不更新","每天","每月","每年"];
	return ysn[str];
}
var THISPAGE = {
	init : function() {
		this.initDom();
	},
	initDom : function() {
		if(id!='')
		Public.ajaxPost(rootPath+"/sso/snCreater/qryOp.json",{id:id}, function(json){
			if(json.status==200){
				model.data=json.data;
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		})
	}
};
THISPAGE.init();