var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({
	$id:'ctrl',
	data:{id:"",end_date:SYSTEM.date,hearer_size:0,exp_rate_return:"",update_datetime:"",fact_sale_count:"",
		fact_rate_return:"",remark:"",type:"",product_id:"",expect_reaction:"",expect_income:"",exp_rec_count:"",send_count:0,cost_budget:0,
		hearer:"",initiator_id:"",name:"",fact_rec_count:"",fact_cost:0,head_id:SYSTEM.user.id,status:0,exp_sale_count:""},
	userList:[],
	qryHead:function(v){
	  Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,status:1,_sortField:"realname",rows:9999,_sort:"asc"},function(json){
	  	model.userList=json.data.list;
	  });
	},
	expect_reaction:["无","好","非常好","一般","差"],
	status:["计划中","激活","禁止","完成","取消"],type:["会议","网上技术交流会","展览会","公共关系","合作伙伴","推荐程序","广告","网络广告","直邮","Email","电话营销","其它"]
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
		model.qryHead();
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/crm/campaigns/qryOp.json",{id:id}, function(json){
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
	var e = "add" == oper ? "新增线索" : "修改线索";
	Public.ajaxPost(rootPath+"/crm/campaigns/save.json",model.data.$model
			, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=(t.data);
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();