var parameData,api = frameElement.api,oper = api.data.oper,id=api.data.id,
$_form=$("#base_form"),addNew=false,parameType=CONFIG.custParameType;
var model=avalon.define({
	$id:'ctrl',
	data:{type:'',name:"",parent_id:"",parent_name:"",sort_num:1,description:"",id:""},
	parameType:parameType
});
avalon.filters.custParameType=function(v){
	return parameType[v];
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/sso/parame/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
					model.data.is_end=(json.data.is_end==0?"否":"是");
					THISPAGE.initEvent();
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