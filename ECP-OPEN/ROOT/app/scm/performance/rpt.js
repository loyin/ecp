var url=rootPath+"/scm/performance",gridQryUrl=url+"/rptList.json";
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{start_date:SYSTEM.beginDate,type:type,end_date:SYSTEM.endDate,uid:"",head_name:"",department_id:""},
    list:[],nprint:true,
    cust_ct:0,newcust_ct:0,contact_ct:0,leads_ct:0,biz_ct:0,biz_amt:0,quoted_ct:0,quoted_amt:0,order_ct:0,order_amt:0,nopay_amt:0,pay_amt:0,tui_amt:0,
    userList:[],userComboV:false,
    chooseUser:function(e){
    	model.query.uid=e.id;
    	model.query.head_name=e.realname;
    	model.userComboV=false;
    },
    qryUser:function(v){//自动完成查询用户
    	model.userComboV=true;
    	model.query.uid='';
    	Public.ajaxPost(rootPath+"/sso/user/dataGrid.json",{keyword:v,_sortField:"realname",rows:9999,_sort:"asc"},function(json){
    		model.userList=json.data.list;
    	});
    },
    mCheck:function(){
    	if(model.query.m==1){
    		model.query.m=0;
    	}else{
    		model.query.m=1;
    	}
    },
    tCheck:function(){
    	if(model.query.t==1){
    		model.query.t=0;
    	}else{
    		model.query.t=1;
    	}
    },
    taxCheck:function(){
    	if(model.query.tax==1){
    		model.query.tax=0;
    	}else{
    		model.query.tax=1;
    	}
    },
	init:function(){
		$(".ui-datepicker-input").datepicker();
		/**部门 combo START*/
		var r1= $("#department");
		var i1=Public.comboTree(r1,{width:260,offset:{left:284,top:0},url:rootPath+'/sso/position/departMentTree.json',postData:{},
					callback : {
						beforeClick : function(e, t) {
							r1.val(t.name);model.query.department_id=(t.id);i1.hide();
						}
					}
				});
		/**部门 combo END*/
		model.loadData();
	},
	loadData:function(){
		Public.ajaxPost(gridQryUrl,model.query.$model, function(json){
			if(json.status==200){
				model.list=json.data;
				model.cust_ct=0;
				model.newcust_ct=0;
				model.contact_ct=0;
				model.leads_ct=0;
				model.biz_ct=0;
				model.biz_amt=0;
				model.quoted_ct=0;
				model.quoted_amt=0;
				model.order_ct=0;
				model.order_amt=0;
				model.nopay_amt=0;
				model.pay_amt=0;
				model.tui_amt=0;
				if(model.list.length>0){
					for(var i=0;i<model.list.length;i++){
						var a=model.list[i];
						model.cust_ct+=new Number(a.cust_ct);
						model.newcust_ct+=new Number(a.newcust_ct);
						model.contact_ct+=new Number(a.contact_ct);
						model.leads_ct+=new Number(a.leads_ct);
						model.biz_ct+=new Number(a.biz_ct);
						model.biz_amt+=new Number(a.biz_amt);
						model.quoted_ct+=new Number(a.quoted_ct);
						model.quoted_amt+=new Number(a.quoted_amt);
						model.order_ct+=new Number(a.order_ct);
						model.order_amt+=new Number(a.order_amt);
						model.nopay_amt+=new Number(a.nopay_amt);
						model.pay_amt+=new Number(a.pay_amt);
						model.tui_amt+=new Number(a.tui_amt);
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