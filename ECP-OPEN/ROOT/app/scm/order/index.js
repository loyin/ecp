var url=rootPath+"/scm/order",gridQryUrl=url+"/dataGrid.json",custParame=SYSTEM.custParame,typeList=custParame.typeList,
order_type=["采购订单","采购退货单","销售订单","销售退货单","报价"],audit_status=["未提交","待审核","通过","拒绝"],order_name=order_type[type],
audit_hidden=(type<=1||((type==2&&SYSTEM.company.config.p_sale_audit=="false")||(type==3&&SYSTEM.company.config.p_saletui_audit=="false")));
var model = avalon.define({$id:'view',
	query:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",ordertype:type,is_deleted:0,qryType:5,pay_status:""},
	parameList:typeList,
	fastQryText:"快速查询",
	fastQry:[
		       {text:"我创建的",sl:false},
		       {text:"我负责的",sl:false},
		       {text:"下属创建的",sl:false},
		       {text:"下属负责的",sl:false},
		       {text:"回收站",sl:false},
		       {text:"",sl:true},
		       {text:"未支付",sl:false},
		       {text:"已支付",sl:false},
		       {text:"已结算",sl:false}
		       ],
	qry:function(type){
		model.query.qryType=type;
		if(type==4){
			model.query.is_deleted=1;
			model.query.qryType=-1;//查看自己的回收站信息
			model.fastQryText="回收站";
			model.query.pay_status="";
		}else if(type>5){//支付情况
			model.fastQryText=model.fastQry[type].text;
			model.query.pay_status=type-6;
			model.query.qryType=5;
			model.query.is_deleted=0;
		}else{
			model.fastQryText=model.fastQry[type].text;
			model.query.is_deleted=0;
			model.query.pay_status="";
		}
		model.reloadData();
	},
	init:function() {
		$(".ui-datepicker-input").datepicker();
		this.loadGrid();
		this.addEvent()
	},
	resetQry:function(){
		model.query={keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",ordertype:type};
		model.reloadData();
	},
	loadGrid:function() {
				function t(val, opt, row) {
					var html_con = '<div class="operating" data-id="'+ row.id+'"><span class="fa fa-eye mrb" title="查看"></span>';
					if (row.is_deleted == 0) {
						if((row.audit_status==0||row.audit_status==3)&&(SYSTEM.user.id==row.head_id||SYSTEM.user.id==row.creater_id)){//未提交审核或审核不通过
							html_con+='<span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
						}
						if (type<=1||audit_hidden){//销售订单 销售退货单
							if(row.audit_status==0&&(SYSTEM.user.id==row.head_id||SYSTEM.user.id==row.creater_id)){
								html_con+='<span class="submit mrb" title="提交">提交</span>';
							}
						}
						if (type > 1&&audit_hidden==false){//销售订单 销售退货单
							if (row.audit_status ==0&&(row.creater_id==SYSTEM.user.id||row.head_id==SYSTEM.user.id)){//未提交审核
								html_con+='<a href="#" class="fa mrb subAudit" title="提交审核">提交审核</a>';
							}else if (row.audit_status ==1&&(row.creater_id==SYSTEM.user.id||row.head_id==SYSTEM.user.id)){//取消提交审核
								html_con+='<a href="#" class="fa mrb disSubAudit" title="取消审核">取消审核</a>';
							}else if (row.audit_status ==1&&row.auditor_id==SYSTEM.user.id){
								html_con+='<a href="#" class="fa mrb audit" title="审核">审核</a>';
							}
						}
					}
					if (row.is_deleted == 1)
						html_con+='<span class="fa fa-reply mrb" title="恢复"></span><span class="fa fa-trash-o mrb del" title="删除"></span>';
					html_con+='</div>';
					return html_con;
				}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url:gridQryUrl,
			postData:model.query.$model,
			datatype:"json",
			mtype:'POST',
			autowidth:true,
			height:i.h,
			altRows:true,
			gridview:true,rownumbers:true,
			multiselect:true,
			multiboxonly:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,width:150,
				formatter:t,
				align:"center",
				title:false
			}, {
				name:"head_name",
				label:type>1?"销售员":"采购员",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"billsn",
				label:"单号",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"customer_name",
				label:type>1?"客户":"供应商",
				align:"center",sortable:true,
				width:100,
				title:false
			}, {
				name:"ordertype",
				label:"类型",
				align:"center",formatter:function(v){return order_type[v];},
				width:100,sortable:true,
				title:false
			}, {
				name:"pay_status",hidden:type==4,//报价单
				label:"付款状态",sortable:true,
				align:"center",formatter:function(v){var pay_status_=["<font color='red'>未支付</font>","<font color='green'>部分支付</font>","已付清"];return pay_status_[v];},
				width:100,sortable:true,
				title:false
			}, {
				name:"order_amt",
				label:"订单金额",
				align:"center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				width:100,sortable:true,
				title:false
			}, {
				name:"sign_date",
				label:"签订日期",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"audit_status",
				label:"审核状态",hidden:audit_hidden,
				align:"center",sortable:true,formatter:function(v){return audit_status[v];},
				width:100,sortable:true,
				title:false
			}, {
				name:"auditor_name",
				label:"审核人",sortable:true,
				align:"center",hidden:audit_hidden,
				width:100,
				title:false
			}, {
				name:"audit_datetime",
				label:"审核时间",sortable:true,
				align:"center",hidden:audit_hidden,
				width:100,
				title:false
			}, {
				name:"creater_name",
				label:"创建人",sortable:true,
				align:"center",
				width:100,
				title:false
			}, {
				name:"create_datetime",
				label:"创建时间",sortable:true,
				align:"center",
				width:100,
				title:false
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"create_datetime",
			sortorder:"desc",
			pager:"#page",
			rowNum:50,
			rowList:[ 50,100, 200 ],
			viewrecords:true,
			shrinkToFit:false,
			forceFit:false,
			jsonReader:{
				root:"data.list",
				records:"data.totalRow",
				repeatitems:false,
				id:"id"
			},
			loadError:function() {
				parent.Public.tips({
					type:1,
					content :"加载数据异常！"
				})
			},
			ondblClickRow:function(t) {
				model.view(t);
			}
		})
	},
	reloadData:function() {
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",mtype:'POST',
			postData:model.query.$model
		}).trigger("reloadGrid");
	},
	addEvent:function() {
		Public.dateCheck();
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			model.view(e);
		});
		$(".grid-wrap").on("click", ".audit", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			model.audit(e);
		});
		$(".grid-wrap").on("click", ".fa-edit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.operate("edit", t)
			}
		});
		$(".grid-wrap").on("click", ".subAudit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.subAudit(t)
			}
		});
		$(".grid-wrap").on("click", ".disSubAudit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.disSubAudit(t)
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				if(model.query.qryType==4)
					model.del(e);
				else
					model.trash(e);
			}
		});
		$(".grid-wrap").on("click", ".del", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
					model.del(e);
			}
		});
		$(".grid-wrap").on("click", ".fa-reply", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
					model.reply(e);
		});
		$(".grid-wrap").on("click", ".submit", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			model.submit(e);
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				model.operate('add');
			}
		});
		$("#btn-batchDel").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				t.length ? 
						((model.query.qryType==4)?model.del(t.join()):model.trash(t.join()))
						:parent.Public.tips({
					type:2,
					content:"请选择需要删除的项"
				})
			}
		});
		$("#btn-batchReply").click(function(e) {
			e.preventDefault();
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						model.reply(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要恢复的"+order_name});
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	},
	operate:function(e, t) {
			if ("add" == e)
				var i = "新增"+order_type[type], r = {oper:e};
			else
				var i = "修改"+order_type[type], r = {oper:e,id:t};
			$.dialog({title:i,content:"url:"+url+"/edit.html",
				data:r,width:900,height :700,max :true,resize:true,	min :false,	cache :false,lock :true
			})
	},
	view:function(id){
		$.dialog({id:"dialog1",width:900,height :700,min:true,max:true,
			title:"查看"+order_type[type],button:[{name:"关闭"	} ],resize:true,lock:true,
			content:"url:"+url+"/view.html",data:{id:id,type:type}});
	},
	audit:function(id){
		$.dialog({id:"dialog1",width:900,height :700,min:true,max:true,
			title:"审核"+order_type[type],resize:true,lock:true,
			content:"url:"+url+"/audit.html",data:{id:id,type:type}});
	},
	reply:function(e) {
			Public.ajaxPost(url + "/reply.json", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else
					parent.Public.tips({type:1,content:"恢复"+order_name+"失败！" + t.msg})
			});
	},
	trash:function(e) {
			Public.ajaxPost(url + "/trash", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else
					parent.Public.tips({type:1,content:"删除"+order_name+"失败！" + t.msg});
			});
	},
	del:function(id) {
		$.dialog.confirm("删除的"+order_type[type]+"将不能恢复，请确认是否删除？", function() {
			Public.ajaxPost(url+"/del.json", {
				id:id
			}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else{
					parent.Public.tips({type:1,content:"删除"+order_type[type]+"失败！请检查是否被引用！" + t.msg});
				}
			})
		});
	},
	submit:function(id) {
		$.dialog.confirm("提交"+order_type[type]+"后将不能修改，且生成应收应付单，请确认是否提交？", function() {
			Public.ajaxPost(url+"/submit.json", {id:id}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else{
					parent.Public.tips({type:1,content:"提交"+order_type[type]+"失败！" + t.msg});
				}
			})
		});
	},
	subAudit:function(id) {
			Public.ajaxPost(url+"/subAudit.json", {id:id}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else{
					parent.Public.tips({type:1,content:"提交"+order_type[type]+"审核失败！" + t.msg});
				}
		});
	},
	disSubAudit:function(id) {
		$.dialog.confirm("确定取消"+order_name+"的审核？", function() {
		Public.ajaxPost(url+"/subAudit.json", {id:id,status:0}, function(t) {
			if (t && 200 == t.status) {
				parent.Public.tips({type:2,content:t.msg});
				model.reloadData();
			} else{
				parent.Public.tips({type:1,content:"取消"+order_type[type]+"审核失败！" + t.msg});
			}
		});
		});
	}
});
model.init();