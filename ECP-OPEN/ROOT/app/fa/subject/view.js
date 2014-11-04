var api = frameElement.api,id=api.data.id,type=parent.type,category=parent.category,direction=parent.direction;
var model = avalon.define({$id:'ctrl',data:{}});
avalon.filters.type=function(str){
	return type[str];
}
avalon.filters.category=function(str){
	return category[str];
}
avalon.filters.direction=function(str){
	return direction[str];
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/subject/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					if(model.data.parent_name==null){
						model.data.parent_name="";
					}
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