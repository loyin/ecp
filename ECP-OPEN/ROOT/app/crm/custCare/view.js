var api = frameElement.api,oper = api.data.oper,id=api.data.id,url=rootPath+"/crm/custCare";
var model = avalon.define({$id:'view',
	data:{}
});
avalon.filters.rating=function(v){
	return custRating[v].name;
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(url+"/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					if(model.data.head_id==undefined){
						model.data.head_id="";
						model.data.head_name="";
					}
					model.init();
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
		THISPAGE.initEvent();
	},
	initBtn:function(){
		api.button({
			id : "cancel",
			name :"关闭"
		})
	}
};
THISPAGE.init();