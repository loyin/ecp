var api = frameElement.api,id=api.data.id;
var model = avalon.define({$id:'ctrl',data:{id:""},
	init:function() {
		if(id!='')
			Public.ajaxPost(rootPath+"/crm/leads/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data;
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			})
		}});
model.init();