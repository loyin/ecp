var url=rootPath+"/scm/storageBill",gridQryUrl=url+"/rptList.json";
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,depot_id:'',product_id:'',product_name:'',head_id:"",head_name:""},
    list:[],amount_in_total:0,amount_out_total:0,nprint:true,depotList:[],
    productList:[],pdctComboV:false,
	chooseProduct:function(e){
    	model.query.product_id=e.id;
    	model.query.product_name=e.name;
    	model.pdctComboV=false;
    },
    qryProduct:function(v){//自动完成查询商品
    	model.pdctComboV=true;
    	model.query.product_id='';
    	Public.ajaxPost(rootPath+"/scm/product/dataGrid.json",{keyword:v,_sortField:"name",_sort:"asc"},function(json){
    		model.productList=json.data.list;
    	});
    },
    userList:[],userComboV:false,
    chooseUser:function(e){
    	model.query.head_id=e.id;
    	model.query.head_name=e.realname;
    	model.userComboV=false;
    },
    qryUser:function(v){//自动完成查询用户
    	model.userComboV=true;
    	model.query.head_id='';
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,_sortField:"realname",rows:9999,_sort:"asc"},function(json){
    		model.userList=json.data.list;
    	});
    },
	init:function(){
		$(".ui-datepicker-input").datepicker();
		Public.ajaxPost(rootPath+"/scm/depot/list.json",{},function(json){
			model.depotList=json.data;
		});
//		model.loadData();
	},
	loadData:function(){
		Public.ajaxPost(gridQryUrl,model.query.$model, function(json){
			if(json.status==200){
				model.list=json.data;
				model.amount_in_total=0;
				model.amount_out_total=0;
				if(model.list.length>0){
					for(var i=0;i<model.list.length;i++){
						var a=model.list[i];
						model.amount_in_total+=a.amount_in;
						model.amount_out_total+=a.amount_out;
					}
				}
			}
		});
	},
	printRpt:function(){
		model.nprint=false;
		window.print();
		model.nprint=true;
	}
});
model.init();
avalon.filters.type=function(v){
	var type=["采购入库","销售退货入库","调拨入库","其它入库","销售出库","采购退货出库","调拨出库","其它出库"];
	return type[v];
}
