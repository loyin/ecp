var queryConditions = {
	keyword : "",reg_date$le:'',reg_date$ge:'',position_id:'',
	qryField:'position_id'
},url=rootPath+"/sso/operlog",gridQryUrl=url+"/dataGrid.json",
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

	loadGrid : function() {
		function t(t, e, i) {
			var a = '<div class="operating" data-id="'+ i.id	+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="ui-icon ui-icon-pencil" title="修改"></span><span class="ui-icon fa-trash-o" title="删除"></span></div>';
			return a
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
			gridview : true,
			multiselect : true,
			multiboxonly : true,
			colModel : [ {
				name : "operating",
				label : "操作",
				fixed : true,width:100,
				formatter : Public.operFmatter,
				align : "center",
				title : false
			}, {
				name : "create_datetime",
				label : "操作时间",
				align : "center",
				width:35,
				formatter:function(t,e,i){return (i.head_pic==null||i.head_pic==''||i.head_pic=='null')?"":"<img width='30px' src='"+(i.head_pic)+"'>";},
				title : true
			}, {
				name : "uname",
				label : "操作人员",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "module_name",
				label : "操作模块",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "action_name",
				label : "操作名称",
				align : "center",
				width:30,
				formatter:function(t,e,i){var status=['女','男']; return status[i.sex];},
				title : true
			}, {
				name : "ip",
				label : "操作IP",
				align : "center",
				width:100,sortable:true,
				title : false
			}, {
				name : "content",
				label : "备注",
				align : "center",width:50,sortable:true,
				title : false
			} ],
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "create_datetime",
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
		Public.dateCheck();
		var t = this;

		$("#search").click(
				function() {
					queryConditions.keyword = "请输入用户帐号或姓名" === t.$_keyword.val() ? "" : t.$_keyword.val();
					queryConditions.reg_date$ge = t.$_beginDate.val();
					queryConditions.reg_date$le = t.$_endDate.val();
					THISPAGE.reloadData(queryConditions)
				});

	}
};
var handle = {
		
		disable : function(id) {
			$.dialog.confirm("确定要（禁用或激活）用户？", function() {
				Public.ajaxPost(url+"/disable", {id : id},
				function(t) {
					if (t && 200 == t.status) {
						var i = t.data|| [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "操作失败！" + t.msg
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