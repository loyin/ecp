var queryConditions = {
	keyword : "",reg_date$le:'',reg_date$ge:'',
	qryField:'reg_date$le,reg_date$ge'
}, url=rootPath+"/sso/message",gridQryUrl=url+"/dataGrid",
 THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
		this.$_keyword = $("#keyword");
		this.$_beginDate = $("#beginDate").val('');//SYSTEM.beginDate);
		this.$_endDate = $("#endDate").val(SYSTEM.endDate);
		this.$_keyword.placeholder();
		$(".ui-datepicker-input").datepicker();
	},
viewdUser:function(id){
	$.dialog({
		id : "moreCon",
		width : 850,
		height : 400,
		min : true,
		max : true,
		title : "查看消息",
		button : [{name : "关闭"	} ],
		resize : true,lock:true,
		content : "url:"+url+"/view",
		data : {id:id}
	});
},
	loadGrid : function() {
		function t(t, e, i) {
			var a = '<div class="operating" data-id="'+ i.id	+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="ui-icon fa-trash-o" title="删除"></span></div>';
			return a
		}
		function e(t) {
			var e = t.join('<p class="line" />');
			return e
		}
		var i = Public.setGrid();
		queryConditions.reg_date$ge = this.$_beginDate.val();
		queryConditions.reg_date$le= this.$_endDate.val();
		$("#grid").jqGrid({
			url : gridQryUrl,
			postData : queryConditions,
			datatype : "json",
			mtype:'POST',
			autowidth : true,
			height : i.h,
			altRows : true,
			gridview : true,rownumbers:true,
			multiselect : true,
			multiboxonly : true,
			colModel : [ {
				name : "operating",
				label : "操作",
				fixed : true,width:100,
				formatter : t,
				align : "center",
				title : false
			}, {
				name : "uname",
				label : "消息类型",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "title",
				label : "标题",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "content",
				label : "内容",
				align : "center",width:300,sortable:true,
				title : true
			}, {
				name : "status",
				label : "状态",
				align : "center",width:40,sortable:true,
				formatter:function(t,e,i){var status=['<font color="red">未读</font>','<font color="green">已读</font>']; return status[i.status];},
				title : true
			}, {
				name : "reg_date",
				label : "发送时间",
				align : "center",width:100,sortable:true,
				title : false
			} ],
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "uname",
			sortorder : "desc",
			pager : "#page",
			rowNum : 50,
			rowList : [ 50,100, 200 ],
			viewrecords : true,
			shrinkToFit : false,
			forceFit : false,
			jsonReader : {
				root : "data.list",
				records : "data.totalRow",
				repeatitems : false,
				id : "id"
			},
			loadError : function() {
				parent.Public.tips({
					type : 1,
					content :"加载数据异常！"
				})
			},
			ondblClickRow : function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		})
	},
	reloadData : function(t) {
		$("#grid").jqGrid("setGridParam", {
			url : gridQryUrl,
			datatype : "json",mtype:'POST',
			postData : t
		}).trigger("reloadGrid");
	},
	addEvent : function() {
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			THISPAGE.viewdUser(e);
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
				$.dialog.confirm("您确定要删除该消息吗？", function() {
					Public.ajaxPost(url+"/del", {trash:true,//true:放置回收站
						id : e
					}, function(t) {
						if (200 === t.status) {
							$("#grid").jqGrid("delRowData", e);
							parent.Public.tips({
								content : "删除成功！"
							})
						} else
							parent.Public.tips({
								type : 1,
								content : t.msg
							})
					})
				})
			}
		});
		$("#search").click(
				function() {
					queryConditions.keyword = "请输入消息帐号或姓名" === t.$_keyword.val() ? "" : t.$_keyword.val();
					queryConditions.reg_date$ge = t.$_beginDate.val();
					queryConditions.reg_date$le = t.$_endDate.val();
					THISPAGE.reloadData(queryConditions)
				});
		$("#moreCon").click(
						function() {
							queryConditions.keyword = t.$_keyword.val();
							queryConditions.reg_date$ge = t.$_beginDate.val();
							queryConditions.reg_date$le = t.$_endDate.val();
							$.dialog({
										id : "moreCon",
										width : 480,
										height : 300,
										min : false,
										max : false,
										title : "高级搜索",
										button : [
												{
													name : "<i class='fa fa-search'></i>查询",
													focus : true,
													callback : function() {
														queryConditions = this.content.handle();
														THISPAGE.reloadData(queryConditions);
														"" !== queryConditions.keyword&& t.$_keyword.val(queryConditions.keyword);
														t.$_beginDate	.val(queryConditions.reg_date$ge);
														t.$_endDate.val(queryConditions.reg_date$le)
													}
												}, {
													name : "取消"
												} ],
										resize : false,
										content : "url:/sso/user/moreSearch",
										data : queryConditions
									})
						});
		$("#add").click(function(t) {
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				handle.operate('add');
			}
		});
		$("#btn-batchDel").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				t.length ? handle.del(t.join()) : parent.Public.tips({
					type : 2,
					content : "请选择需要删除的项"
				})
			}
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
		operate : function(e, t) {
			if ("add" == e)
				var i = "新增消息", r = {oper : e,callback : this.callback};
			else
				var i = "修改消息", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:/sso/user/edit",
				data : r,
				width : 850,
				height : 422,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(e) {
			$.dialog.confirm("删除的消息将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del", {
					id : e
				}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data.id || [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "删除消息失败！" + t.msg
						})
				})
			})
		},
		callback : function(e, t, i) {
			var r = $("#grid").data("gridData");
			if (!r) {
				r = {};
				$("#grid").data("gridData", r)
			}
			e.difMoney = e.amount - e.periodMoney;
			r[e.id] = e;
			if ("edit" == t) {
				$("#grid").jqGrid("setRowData", e.id, e);
				i && i.api.close()
			} else {
				$("#grid").jqGrid("addRowData", e.id, e, "last");
				i && i.resetForm(e)
			}
		}
	}
THISPAGE.init();