var api = frameElement.api,oper = api.data.oper,id=api.data.id,
type=parent.type,pjlx=parent.pjlx;
var model = avalon.define({$id:'view',data:{},
	init:function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/payReceivAbles/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
		api.button({
			id : "cancel",
			name : "关闭"
		})
	}
});
avalon.filters.type=function(v){return type[v];};
avalon.filters.status=function(v){return parent.status_[v];};
model.init();