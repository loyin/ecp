var url=rootPath+"/scm/stockAllot",gridQryUrl=url+"/dataGrid.json",custParame=SYSTEM.custParame,typeList=custParame.typeList;
var model = avalon.define({$id:'view',
	query:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",ordertype:type,is_deleted:0,qryType:5,submit_status:""},
	parameList:typeList,
	fastQryText:"快速查询",
	fastQry:[
		       {text:"我创建的",sl:false},
		       {text:"我负责的",sl:false},
		       {text:"下属创建的",sl:false},
		       {text:"下属负责的",sl:false},
		       {text:"回收站",sl:false},
		       {text:"",sl:true},
		       {text:"未出库",sl:false},
		       {text:"已出库",sl:false}
		       ],
	qry:function(type){
		model.query.qryType=type;
		if(type==4){
			model.query.is_deleted=1;
			model.query.qryType=-1;//查看自己的回收站信息
			model.fastQryText="回收站";
			model.query.submit_status="";
		}else if(type>5){//提交情况
			model.fastQryText=model.fastQry[type].text;
			model.query.submit_status=type-6;
			model.query.qryType=5;
			model.query.is_deleted=0;
		}else{
			model.fastQryText=model.fastQry[type].text;
			model.query.is_deleted=0;
			model.query.submit_status="";
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
						if(row.submit_status==0){
							html_con += '<span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
							html_con += '<a href="#" class="fa mrb submit" title="提交">提交</a>';
						}
					}
					if (row.is_deleted == 1)
						html_con += '<span class="fa fa-reply mrb" title="恢复"></span>';
					html_con += '</div>';
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
				label:"负责人",
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
				name:"out_depot_name",
				label:"调出仓库",
				align:"center",sortable:true,
				width:100,
				title:false
			}, {
				name:"to_depot_name",
				label:"调入仓库",
				align:"center",sortable:true,
				width:100,
				title:false
			}, {
				name:"bill_date",
				label:"单据日期",
				align:"center",
				width:100,sortable:true,
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
		$(".grid-wrap").on("click", ".fa-edit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.operate("edit", t)
			}
		});
		$(".grid-wrap").on("click", ".submit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.submit(t)
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
		$(".grid-wrap").on("click", ".fa-reply", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
					model.reply(e);
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
					parent.Public.tips({type:2,content:"请选择需要恢复的调拨单"});
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	},
	operate:function(e, t) {
			if ("add" == e)
				var i = "新增调拨单", r = {oper:e};
			else
				var i = "修改调拨单", r = {oper:e,id:t};
			$.dialog({title:i,content:"url:"+url+"/edit.html",
				data:r,width:900,height :700,max :true,resize:true,	min :false,	cache :false,lock :true
			})
	},
	view:function(id){
		$.dialog({id:"dialog1",width:900,height :700,min:true,max:true,
			title:"查看调拨单",button:[{name:"关闭"	} ],resize:true,lock:true,
			content:"url:"+url+"/view.html",data:{id:id,type:type}});
	},
	reply:function(e) {
			Public.ajaxPost(url + "/reply.json", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else
					parent.Public.tips({type:1,content:"恢复调拨单失败！" + t.msg})
			});
	},
	trash:function(e) {
			Public.ajaxPost(url + "/trash", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else
					parent.Public.tips({type:1,content:"删除调拨单失败！" + t.msg});
			});
	},
	del:function(id) {
		$.dialog.confirm("删除的调拨单将不能恢复，请确认是否删除？", function() {
			Public.ajaxPost(url+"/del.json", {id:id}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else{
					parent.Public.tips({type:1,content:"删除调拨单失败！请检查是否被引用！" + t.msg});
				}
			})
		});
	},
	submit:function(id) {
		$.dialog.confirm("提交后数据将不可修改，确定要提交吗？", function() {	
			Public.ajaxPost(url+"/submit.json", {id:id}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					model.reloadData();
				} else{
					parent.Public.tips({type:1,content:t.msg});
				}
		});
		});
	}
});
model.init();