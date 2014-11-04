var api = frameElement.api,oper = api.data.oper,id=api.data.id,type=parent.type;
var model = avalon.define({$id:'view',data:{}});
/**自定义filter 用法：{{model.attr|isHead}}*/
avalon.filters.type=function(str){
	return type[str];
}
avalon.filters.status=function(str){
	var status=["未结算","已结算"];
	return status[str];
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/payReceivOrder/qryOp.json",{id:id}, function(json){
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