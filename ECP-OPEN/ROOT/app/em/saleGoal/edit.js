var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,year=new Date(SYSTEM.date).getFullYear(),$_form=$("#base_form");
var model = avalon.define({$id:'ctrl',yearList:new Array(),
	
	data:{id:"",name:'',year:year,y:0,q1:0,q2:0,q3:0,q4:0,yv:0,qv1:0,qv2:0,qv3:0,qv4:0,user_id:"",
		m1:0,mv1:0,m2:0,mv2:0,m3:0,mv3:0,m4:0,mv4:0,m5:0,mv5:0,m6:0,mv6:0,m7:0,mv7:0,m8:0,mv8:0,m9:0,mv9:0,m10:0,mv10:0,m11:0,mv11:0,m12:0,mv12:0},
	average:function(){//平均年目标到每月中
		if(model.data.y!=0&&model.data.y%12==0){
			var qv=model.data.y/4;
			var mv=model.data.y/12;
			model.data.q1=qv;
			model.data.q2=qv;
			model.data.q3=qv;
			model.data.q4=qv;
			model.data.m1=mv;
			model.data.m2=mv;
			model.data.m3=mv;
			model.data.m4=mv;
			model.data.m5=mv;
			model.data.m6=mv;
			model.data.m7=mv;
			model.data.m8=mv;
			model.data.m9=mv;
			model.data.m10=mv;
			model.data.m11=mv;
			model.data.m12=mv;
		}else{
			parent.Public.tips({type: 1, content :"年度业务目标不能被12整除，不能平均分配到月！"});
		}
	},
	init:function(y){
		model.data={id:"",name:'',year:y,y:0,q1:0,q2:0,q3:0,q4:0,yv:0,qv1:0,qv2:0,qv3:0,qv4:0,user_id:"",
			m1:0,mv1:0,m2:0,mv2:0,m3:0,mv3:0,m4:0,mv4:0,m5:0,mv5:0,m6:0,mv6:0,m7:0,mv7:0,m8:0,mv8:0,m9:0,mv9:0,m10:0,mv10:0,m11:0,mv11:0,m12:0,mv12:0};
		Public.ajaxPost(rootPath+"/em/saleGoal/qry"+(id=="-1"?"":"Op")+".json",{id:id,year:model.data.year}, function(json){
			if(json.status==200){
				avalon.mix(model.data,json.data);
				THISPAGE.initEvent();
			}else{
				parent.Public.tips({type: 1, content : json.msg});
			}
		});
	}
});
var THISPAGE = {
	init : function() {
		for(var i=year-1;i<=year+1;i++){
			model.yearList.push(i);
		}
		model.init(year);
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			model.init();
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
	var e = "add" == oper ? "新增销售目标" : "修改销售目标";
	Public.ajaxPost(rootPath+"/em/saleGoal/save"+(id=="-1"?"My":"")+".json",model.data.$model
			, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			model.data.id=(t.data);
			try{
			parent.THISPAGE.reloadData(null);
			}catch(e){}
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();