var api = frameElement.api,oper = api.data.oper,id=api.data.rowId,
$_form=$("#base_form"),addNew=false;
var model = avalon.define({
	$id:'view',
	data:{id:"",category:"",category_name:"",name:"",billsn:"",stock_warn:0,model:"",status:1,unit:"",unit_name:"",sale_price:0,purchase_price:0,remark:""},
	setStatus:function(v){
		model.data.status=v;
	},
	init:function(){
		model.data={id:"",category:"",category_name:"",name:"",billsn:"",stock_warn:0,model:"",status:1,unit:"",unit_name:"",sale_price:0,purchase_price:0,remark:""};
	}
});
model.data.$watch("$all",function(name,a,b){
	if(a==null){
		model.data[name]="";
	}
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/product/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					model.data.unit_name=parent.parent.SYSTEM.custParame[json.data.unit].name;
					model.data.category_name=parent.parent.SYSTEM.custParame[json.data.category].name;
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
			focus :true,
			callback : function() {
				addNew=false;
				$_form.trigger("validate");
				return false
			}
		}, {
			id : "saveAndNew",
			name : "<i class='fa fa-save mrb'></i>保存并新建",
			focus :false,
			callback : function() {
				$_form.trigger("validate");
				addNew=true;
				return false
			}
		}, {
			id : "cancel",
			name : e[1]
		})
	},
	initEvent:function(){
		/**类别combo START*/
		var r = $("#category_sel");
		var i=Public.comboTree(r,{offset:{top:0,left:94},url:rootPath+'/sso/parame/tree.json',postData:{type:0},
				callback:{
					beforeClick : function(e, t) {
						r.val(t.name);model.data.category=t.id;i.hide();
					}
				}
			});
		/**类别combo END*/
		/**计量单位combo START*/
		var r1 = $("#unit_sel");
		var i1=Public.comboTree(r1,{offset:{top:0,left:94},url:rootPath+'/sso/parame/tree.json',postData:{type:1},
					callback : {
						beforeClick : function(e, t) {
							r1.val(t.name);model.data.unit=t.id;i1.hide();
						}
					}
				});
		/**计量单位combo END*/
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
	var e = "add" == oper ? "新增商品" : "修改商品";
	if(model.data.category=="")
	{
		parent.parent.Public.tips({type : 1,content : "类别未选择！"});
		return;
	}
	if(model.data.unit=="")
	{
		parent.parent.Public.tips({type : 1,content : "计量单位未选择！"});
		return;
	}
	Public.ajaxPost(rootPath+"/scm/product/save.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({
				content : e + "成功！"
			});
			if(addNew){
				model.init();
			}else{
				model.data.id=t.data.id;
				model.data.billsn=t.data.sn;
			}
			parent.THISPAGE.reloadData(null);
		} else
			parent.parent.Public.tips({
				type : 1,
				content : e + "失败！" + t.msg
			});
	});
}
THISPAGE.init();