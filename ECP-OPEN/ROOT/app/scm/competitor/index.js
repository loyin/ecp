var url=rootPath+"/scm/competitor",gridQryUrl=url+"/dataGrid.json",//需要查询字段
custParame=SYSTEM.custParame;
var model = avalon.define({$id:'view',
	query:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate}
});
var THISPAGE={
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
		$(".ui-datepicker-input").datepicker();
	},
	view:function(id){
		$.dialog({
			id : "moreCon",
			width : 400,
			height :400,
			min : true,
			max : true,
			title : "查看竞争对手",
			button : [{name : "关闭"	} ],
			resize : true,lock:true,
			content : "url:"+url+"/view.html",
			data : {id:id}
		});
	},
	loadGrid : function() {
		function t(t, e, i) {
			var a = '<div class="operating" data-id="'+ i.id	+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="ui-icon ui-icon-pencil" title="修改"></span><span class="ui-icon fa-trash-o" title="删除"></span></div>';
			return a
		}
		function e(t) {
			var e = t.join('<p class="line" />');
			return e
		}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url : gridQryUrl,
			postData : model.query.$model,
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
				formatter : Public.operFmatter,
				align : "center",
				title : false
			}, {
				name : "name",
				label : "名称",
				align : "center",
				width:100,
				title : false
			}, {
				name : "jzwx",
				label : "竞争威胁",
				align : "center",
				width:100,
				title : false
			}, {
				name : "sczy",
				label : "市场占有",
				align : "center",
				width:100,
				title : false
			}, {
				name : "ppzmd",
				label : "品牌知名度",
				align : "center",
				width:100,
				title : false
			}, {
				name : "qyxz",
				label : "企业类别",formatter:Public.custParamefmter,
				align : "center",
				width:100,
				title : false
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
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "name",
			sortorder : "asc",
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
	reloadData : function() {
		$("#grid").jqGrid("setGridParam", {
			url : gridQryUrl,
			datatype : "json",mtype:'POST',
			postData :model.query.$model
		}).trigger("reloadGrid");
	},
	addEvent : function() {
		Public.dateCheck();
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
				handle.del(e);
			}
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				handle.operate('add');
			}
		});
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
		$("#refresh").click(function() {
			THISPAGE.reloadData()
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
				var i = "新增竞争对手", r = {oper : e,callback : this.callback};
			else
				var i = "修改竞争对手", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit.html",
				data : r,
				width : 400,
				height :400,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的竞争对手将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del.json", {
					id : id
				}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data || [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "删除竞争对手失败！" + t.msg
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