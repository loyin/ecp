var api = frameElement.api,id=api.data.id;
var model = avalon.define({$id:'ctrl',data:{id:"",name:'',remark:""}});
var THISPAGE = {
	init : function() {
		this.initDom();
	},
	initDom : function() {
		if(id!='')
		Public.ajaxPost(rootPath+"/em/saleGoal/qryOp.json",{id:id}, function(json){
			if(json.status==200){
				model.data=json.data;
			}
		})
	}
};
THISPAGE.init();