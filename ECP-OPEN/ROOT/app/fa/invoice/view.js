var api = frameElement.api,oper = api.data.oper,id=api.data.id,type=parent.type,pjlx=parent.pjlx;
var model = avalon.define({$id:'view',data:{}});
/**自定义filter 用法：{{model.attr|isHead}}*/
avalon.filters.type=function(str){
	return type[str];
}
avalon.filters.pjlx=function(str){
	return pjlx[str];
}
avalon.filters.money=function(str){
	return parent.Public.numToCurrency(str);
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/invoice/qryOp.json",{id:id}, function(json){
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
			name : "关闭"
		})
	},
	initEvent:function(){
	}
};
THISPAGE.init();