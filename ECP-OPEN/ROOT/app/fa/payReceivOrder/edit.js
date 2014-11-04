var api = frameElement.api,oper = api.data.oper,order=api.data.order,id=api.data.id,$_form=$("#base_form"),addNew=false,type=parent.type,$subject;
var model = avalon.define({$id:'view',type:type,
    data:{payables_id:"",ables_name:"",name:'',billsn:"自动生成",account_id:"",account_name:"",subject_id:"",subject_name:"",name:"",type:0,amt:0,head_id:SYSTEM.user.id,
    	head_name:SYSTEM.user.realname,id:"",description:"",pay_datetime:SYSTEM.date,status:0,
    	customer_id:"",customer_name:""},
    show1:order!=undefined?true:false,
    userList:[],statusList:["未结算","已结算"],accountList:[],
    setStatus:function(v){
    	model.data.status=v;
    },
    setType:function(v){
    	if(order==undefined)
    	model.data.type=v;
    }
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	reset:function(){
	},
	initDom : function() {
		if(order){
			model.data.type=order.type;
			model.data.amt=order.amt-order.pay_amt;
			model.data.payables_id=order.id;
			model.data.ables_name=order.name;
			model.data.name=order.name+"-"+type[order.type];
			model.data.customer_id=order.customer_id;
			model.data.head_id=order.head_id;
			model.data.customer_name=order.customer_name;
		}
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/fa/payReceivOrder/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					THISPAGE.initEvent();
				}else{
					parent.Public.tips({type: 1, content : json.msg});
				}
			});
		}else{
			THISPAGE.initEvent();
		}
		Public.ajaxPost(rootPath+"/sso/user/list.json",{}, function(json){
			if(json.status==200){
				model.userList=json.data;
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		});
		Public.ajaxPost(rootPath+"/fa/account/dataGrid.json",{}, function(json){
			if(json.status==200){
				model.accountList=json.data.list;
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		});
		/**财务科目 START*/
		$subject=$("#subject");
		$subject.combo(
					{
						data : rootPath+"/fa/subject/list.json",
						value : "id",
						text : "name",
						width : 210,
						defaultSelected:model.data.subject_id,//$subject.data("defItem")||"",
						editable :true,
						ajaxOptions : {
							formatData : function(e) {
								if (200 == e.status) {
									e.data.unshift({
										id:"",
										name : "（空）"
									});
									return e.data
								}
								return []
							},callback: {
								onChange: function(data){
									model.data.subject_id=data.id;
								},
								onListClick: function(){
								}
							}
						}
					}).getCombo();
		/**财务科目 END*/
	},
	initBtn:function(){
		var e = "add" ==  api.data.oper ? [ "<i class='fa fa-save mrb'></i>保存", "关闭" ] : [ "<i class='fa fa-save mrb'></i>确定", "取消" ];
		api.button({
			id : "confirm",
			name : e[0],
			focus : !0,
			callback : function() {
				addNew=false;
				$_form.trigger("validate");
				return false
			}
		}, {
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		this.initValidator();
	},
	initValidator:function() {
		$_form.validator({
			messages : {
				required : "请填写{0}"
			},
			display : function(e) {
				return $(e).closest(".row-item").find("label").text()
			},
			valid : function() {
				postData();
			},
			ignore : ":hidden",
			theme : "yellow_bottom",
			timely : 1,
			stopOnError : true
		});
	}
};
function postData(){
	var e = "add" == oper ? "新增" : "修改"+"收款付款单";
	model.data.subject_id=$subject.getCombo().getValue();
	if(model.data.subject_id==''){
		parent.parent.Public.tips({type : 1,content :"财务科目不能为空！"});
		return;
	}
	Public.ajaxPost(rootPath+"/fa/payReceivOrder/save.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=t.data.id;
			model.data.billsn=t.data.billsn;
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();