var api = frameElement.api,oper = api.data.oper,id=api.data.id,$_form=$("#base_form"),
area=SYSTEM.area,custRating=SYSTEM.custRating,url=rootPath+"/crm/customer";
var model = avalon.define({$id:'view',
	data:{province:"",city:"",name:"",type:1,sn:"",remark:"",website:"",address:"",fax:"",telephone:"",mobile:"",email:"",head_name:"",id:"",is_delete:0},
	//首要联系人
	contacts:{name:"",sex:1,mobile:"",is_main:1,post:"",idcard:"",department:"",saltname:"",telephone:"",qq:"",email:"",address:"",zip_code:"",customer_id:"",description:"",id:""},
    tabActive:0,
    showTab:function(i,b){
    	model.tabActive=i;
    }
});
avalon.filters.rating=function(v){
	return custRating[v].name;
}
var THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(url+"/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					model.data=json.data.customer;
					if(model.data.head_id==undefined){
						model.data.head_id="";
						model.data.head_name="";
					}
					Public.ajaxPost(rootPath+"/crm/contacts/mainContacts.json",{customer_id:id}, function(json){
						if(json.status==200){
							model.contacts=json.data;
						}});
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}
		THISPAGE.initEvent();
	},
	loadGrid:function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'+ row.id+ '"><span class="fa fa-eye mrb" title="查看"></span></div>';
			return html_con;
		}
		$("#grid").jqGrid({
			url:rootPath+"/crm/contacts/list.json",
			postData:{customer_id:id},
			datatype:"json",
			mtype:'POST',
			width:720,
			height:500,
			rownumbers:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,
				hidden:true,
				formatter:t,
				align:"center",
				title:false
			}, {
				name:"is_main",
				label:"首要",width:80,
				align:"center",formatter:function(v){var s=["","<font color='red'>首要</font>"];return s[v];},
				title:true
			}, {
				name:"name",
				label:"姓名",
				align:"center",
				width:80,
				sortable:true,
				title:true
			}, {
				name:"sex",
				label:"性别",
				align:"center",formatter:function(v){var sex=["女","男"];return sex[v];},
				title:true
			}, {
				name:"mobile",
				label:"手机",
				align:"center",
				title:true
			}, {
				name:"telephone",
				label:"电话",
				align:"center",
				sortable:true,
				title:true
			}, {
				name:"qq",
				label:"QQ",
				align:"center",
				
				sortable:true,
				title:true
			}, {
				name:"email",
				label:"Email",
				align:"center",
				width:140,
				sortable:true,
				title:false
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			viewrecords:true,
			shrinkToFit:false,
			forceFit:false,
			jsonReader:{
				root:"data",
				records:"data.totalRow",
				repeatitems:false,
				id:"id"
			},
			loadError:function() {
				parent.Public.tips({
					type:1,
					content:"加载数据异常！"
				})
			},
			ondblClickRow:function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		});
	},
	initEvent:function(){
		
	}
};
THISPAGE.init();