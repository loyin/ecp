var url=rootPath+"/scm/productPriceOrder",gridQryUrl=url+"/dataGrid.json",
SYSTEM = parent.SYSTEM;
var model=avalon.define({
	$id:"ctrl",data:{keyword : "",category:'',qryField:'category',beginDate:"",endDate:"",beginDate1:"",endDate1:""}
});
var THISPAGE = {
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
			width : 850,
			height : 400,
			min : true,
			max : true,
			title : "查看商品价目表",
			button : [{name : "关闭"	} ],
			resize : true,lock:true,
			content : "url:"+url+"/view",
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
			postData : model.data.$model,
			datatype : "json",
			mtype:'POST',
			autowidth : true,
			height : i.h,
			altRows : true,rownumbers:true,
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
				name : "subject",
				label : "主题",
				align : "center",sortable:true,
				title : true
			}, {
				name : "start_date",
				label : "开始日期",
				align : "center",sortable:true,
				title : true
			}, {
				name : "end_date",
				label : "结束日期",
				align : "center",sortable:true,
				title : true
			}, {
				name : "create_datetime",
				label : "创建时间",
				align : "center",sortable:true,
				title : true
			}, {
				name : "creater_realname",
				label : "创建人",
				align : "center",
				width:100,
				title : true
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
				total:"",
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
			postData : model.data.$model
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
		$(".grid-wrap").on("click", ".disable_product_btn", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).data("id");
				handle.disable(e);
			}
		});
		$("#search").click(
				function() {
					THISPAGE.reloadData()
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
				var i = "新增商品价目表", r = {oper : e,callback : this.callback};
			else
				var i = "修改商品价目表", r = {oper : e,id : t,callback : this.callback};
			$.dialog({title : i,content : "url:"+url+"/edit",data : r,
				width : 850,height : 422,max :true,resize:true,min :false,cache :false,lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的商品价目表将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del.json", {
					id : id
				}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data || [];
						parent.Public.tips({type : 2,content : t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type : 1,content : "删除商品价目表失败！" + t.msg})
				})
			})
		}
	}
THISPAGE.init();