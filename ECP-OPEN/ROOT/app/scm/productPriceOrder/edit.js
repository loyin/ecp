var categoryData,api = frameElement.api,oper = api.data.oper,id=api.data.id,
$_form=$("#base_form"),addNew=false;
var model=avalon.define({
	$id:"ctrl",
	data:{company_id: "",create_datetime: "",creater_id: "",creater_realname: "",end_date: "",id: "",remark: "",start_date: "",subject: "",submit_status: 0,
		productlistlength:1,
		productlist:[{amount:"",id: "",product_id:"",product_name: "",cost:"",price:""}]},
    //添加商品
    productlist:[],productComboDiv:-1,
    qryProduct:function(v,idx){//自动完成查商品
    	model.productComboDiv=idx;
    	Public.ajaxPost(rootPath+"/scm/product/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc"},function(json){
    		model.productlist=json.data.list;
    	});
    },
    chooseProduct:function(e,idx){
    	model.data.productlist[idx].product_id=e.id;
    	model.data.productlist[idx].product_name=e.name;
    	model.data.productlist[idx].price=e.sale_price;
    	model.data.productlist[idx].cost=e.purchase_price;
    	model.productComboDiv=-1;
    },
    addRow:function(){
    	model.data.productlist.push({product_id:'',cost:"",price:"",amount:"",product_name:""});
    },//增加商品行;
    delRow:function(e){//删除商品行
    	if(model.data.productlist.length==1){
    		parent.parent.Public.tips({type : 1,content : "至少输入一个商品信息"});
    		return;
    	}
    	e.preventDefault();
        var item = this.$vmodel.$remove();
    	model.data.productlist.remove(item);
    },
	init:function(){model.data={company_id: "",create_datetime: "",creater_id: "",creater_realname: "",end_date: "",id: "",remark: "",start_date: "",subject: "",submit_status: 0,productlistlength:1,productlist:[{amount:"",id: "",product_id:"",product_name: "",price:"",cost:"",price:""}]};}
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
		$(".ui-datepicker-input").datepicker();
		if(id!=undefined&&id!=''&&id!='undefined'){
			Public.ajaxPost(rootPath+"/scm/productPriceOrder/qryOp.json",{id:id}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					model.data.productlist=json.data.productlist;
					model.data.productlistlength=json.data.productlistlength;
					if(json.data.productlistlength==0){
						model.data.productlistlength=1;
						model.data.productlist=[{amount:"",id: "",product_id:"",product_name: "",cost:"",price:""}];
					}
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
	var e = ("add" == oper ? "新增" : "修改")+"商品价目表";
	if(model.data.end_date<model.data.start_date){
		parent.parent.Public.tips({
			type : 1,
			content : "截止日期不能早于开始日期"
		});
		return;
	}
	model.data.productlistlength=model.data.productlist.length;
	Public.ajaxPost(rootPath+"/scm/productPriceOrder/save.json",model.data.$model, function(t) {
		if (200 == t.status) {
			parent.parent.Public.tips({content : e + "成功！"});
			if(addNew){
				model.init();
			}else{
				model.data.id=(t.data);
			}
			parent.THISPAGE.reloadData();
		} else
			parent.parent.Public.tips({type : 1,content : e + "失败！" + t.msg});
	});
}
THISPAGE.init();