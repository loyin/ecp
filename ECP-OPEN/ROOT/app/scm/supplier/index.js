var url=rootPath+ "/crm/customer", gridQryUrl = url+"/dataGrid.json",type=["供应商","企业","个人"];
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:"",end_date:"",type:0,qryType:-3,status:0,is_deleted:0,uid:""
	},
	qry:function(type){
		model.query.qryType=type;
		if(type==100){
			model.query.is_deleted=1;
			model.fastQryText="回收站";
		}else{
			model.query.is_deleted=0;
		}
		THISPAGE.reloadData();
	}
});
var THISPAGE = {
	init:function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom:function() {
		$(".ui-datepicker-input").datepicker();
	},
	view:function(id) {
		$.dialog({
			id:"moreCon",
			width:850,
			height:600,
			min:true,
			max:true,
			title:"查看供应商",
			button:[ {
				name:"关闭"
			} ],
			resize:true,
			lock:true,
			content:"url:" + rootPath + "/?go=scm/supplier/view",
			data:{
				id:id
			}
		});
	},
	loadGrid:function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'
					+ row.id
					+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>'+(row.is_deleted==1?'<span class="fa fa-reply mrb" title="恢复"></span>':'')+'</div>';
			return html_con;
		}
		function fmtAmt(v) {
			return Public.numToCurrency(v);
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
			gridview:true,
			rownumbers:true,
			multiselect:true,
			multiboxonly:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,
				width:100,
				formatter:t,
				align:"center",
				title:false
			}, {
				name:"sn",
				label:"编号",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"name",
				label:"供应商名称",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"updater_name",
				label:"修改人",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"update_datetime",
				label:"修改日期",
				align:"center",
				width:140,
				sortable:true,
				title:false
			}, {
				name:"creator_name",
				label:"创建人",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"create_datetime",
				label:"创建日期",
				align:"center",
				width:140,
				sortable:true,
				title:false
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"sn",
			sortorder:"desc",
			pager:"#page",
			rowNum:50,
			rowList:[ 50, 100, 200 ],
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
					content:"加载数据异常！"
				})
			},
			ondblClickRow:function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		});
	},
	reloadData:function() {
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",
			mtype:'POST',
			postData:model.query.$model
		}).trigger("reloadGrid");
	},
	addEvent:function() {
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			THISPAGE.view(e);
		});
		$(".grid-wrap").on("click", ".fa-edit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				handle.operate("edit", t)
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				if(model.query.qryType==100)
					handle.del(e);
				else
					handle.trash(e);
			}
		});
		$(".grid-wrap").on("click", ".fa-reply", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
					handle.reply(e);
		});
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
		$("#refresh").click(function() {
			THISPAGE.reloadData()
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if (Business.verifyRight("TF_ADD")) {
				handle.operate('add');
			}
		});
		$("#btn-batchDel").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
					if(model.query.qryType==100)
						handle.del(t.join());
					else
						handle.trash(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要删除的供应商"});
			}
		});
		$("#btn-batchReply").click(function(e) {
			e.preventDefault();
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						handle.reply(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要恢复的供应商"});
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
	operate:function(e, t) {
		if ("add" == e)
			var i = "新增供应商", r = {
				oper:e,
				callback:this.callback
			};
		else
			var i = "修改供应商", r = {
				oper:e,
				id:t,
				callback:this.callback
			};
		$.dialog({
			title:i,
			content:"url:" + rootPath + "/?go=scm/supplier/edit",
			data:r,
			width:850,
			height:600,
			max:true,
			min:false,
			cache:false,
			resize:true,
			lock:true
		})
	},
	del:function(e) {
		$.dialog.confirm("删除的供应商将不能恢复，请确认是否删除？", function() {
			Public.ajaxPost(url + "/del.json", {
				id:e
			}, function(t) {
				if (t && 200 == t.status) {
					
					parent.Public.tips({
						type:2,
						content:t.msg
					});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({
						type:1,
						content:"删除供应商失败！" + t.msg
					})
			})
		})
	},
	reply:function(e) {
		$.dialog.confirm("请确认是否恢复？", function() {
			Public.ajaxPost(url + "/reply.json", {
				id:e
			}, function(t) {
				if (t && 200 == t.status) {
					
					parent.Public.tips({
						type:2,
						content:t.msg
					});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({
						type:1,
						content:"恢复供应商失败！" + t.msg
					})
			})
		})
	},
	trash:function(e) {
		$.dialog.confirm("删除的供应商将不可使用，但可以在回收站还原，是否继续？", function() {
			Public.ajaxPost(url + "/trash", {
				id:e
			}, function(t) {
				if (t && 200 == t.status) {
					
					parent.Public.tips({
						type:2,
						content:t.msg
					});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({
						type:1,
						content:"删除供应商失败！" + t.msg
					})
			})
		})
	}
}
THISPAGE.init();