var api = frameElement.api,oper = api.data.oper,id=api.data.id,$_form=$("#base_form"),url=rootPath+"/crm/contacts";
var model = avalon.define({$id:'view',
	data:{province:"",city:"",name:"",type:1,sn:"",remark:"",website:"",address:"",fax:"",telephone:"",mobile:"",email:"",head_name:"",id:"",is_delete:0},
    tabActive:0,
    showTab:function(i,b){
    	model.tabActive=i;
    }
});
model.data.$watch("$all",function(name,a,b){
	if(b==null||b=="null"){
		eval("model.data."+name+"='';");
	}
});
avalon.filters.sex=function(v){
	var sex=["女","男"];
	return sex[v];
}
avalon.filters.type=function(v){
	var sex=["供应商","客户","客户"];
	return sex[v];
}
var THISPAGE = {
	init : function() {
		this.initDom();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(url+"/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
	}
};
THISPAGE.init();