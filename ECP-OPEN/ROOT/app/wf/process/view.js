var api = frameElement.api,id=api.data.id;
var model = avalon.define({$id:'ctrl',data:{id:"",name:''}});
var THISPAGE = {
	init : function() {
		this.initDom();
	},
	initDom : function() {
		if(id!='')
		Public.ajaxPost(rootPath+"/wf/process/qryOp.json",{id:id}, function(json){
			if(json.status==200){
				$('#snakerflow').snakerflow({
					basePath : rootPath+"/assets/snaker/",
					restore :json.data.process,
					editable :false
				});
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		});
	}
};
THISPAGE.init();