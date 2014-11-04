var queryConditions = {
	keyword : "",category:''
}, url=rootPath+"/scm/product",gridQryUrl=url+"/dataGrid.json",
 THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
		this.$_keyword = $("#keyword");
	},
	viewdproduct:function(id){
		$.dialog({
			id : "moreCon",
			width : 850,
			height : 400,
			min : true,
			max : true,
			title : "查看商品",
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
		function fmtAmt(v) {
			return Public.numToCurrency(v);
		}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url : gridQryUrl,
			postData : queryConditions,
			datatype : "json",
			mtype:'POST',
			autowidth : true,
			height : i.h,
			altRows : true,
			gridview : true,
			multiselect : true,rownumbers:true,
			multiboxonly : true,
			colModel : [ {
				name : "operating",
				label : "操作",
				fixed : true,width:100,
				formatter : Public.operFmatter,
				align : "center",
				title : false
			}, {
				name : "category",
				label : "类别",formatter:function(v,e,r){return parent.SYSTEM.custParame[v].name;},
				align : "center",sortable:true,
				title : true
			}, {
				name : "billsn",
				label : "商品编号",
				align : "center",sortable:true,
				title : true
			}, {
				name : "name",
				label : "商品名称",
				align : "center",sortable:true,
				title : true
			}, {
				name : "model",
				label : "商品型号",
				align : "center",sortable:true,
				title : true
			}, {
				name : "unit",
				label : "计量单位",
				align : "center",formatter:function(v,e,r){return parent.SYSTEM.custParame[v].name;},
				width:100,
				title : true
			}, {
				name : "sale_price",
				label : "销售价格",
				align : "right",width:120,formatter:fmtAmt,
				sortable:true,
				title : false
			}, {
				name : "purchase_price",
				label : "采购价格",width:120,formatter:fmtAmt,
				align : "right",
				title : false
			}, {
				name : "stock_warn",
				label : "库存预警",width:120,
				align : "right",
				title : false
			}, {
				name : "status",
				label : "状态",
				align : "center",width:40,sortable:true,
				formatter:function(t,e,i){var status=['<font color="red">禁用</font>','<font color="green">激活</font>']; return "<a class='disable_product_btn' data-id='"+i.id+"'>"+status[i.status]+"</a>";},
				title : true
			} ],
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "name",
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
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			THISPAGE.viewdproduct(e);
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
					queryConditions.keyword =t.$_keyword.val();
					THISPAGE.reloadData(queryConditions)
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
		/**类别combo START*/
		var r = $("#category");
		var i=Public.comboTree(r,{url:rootPath+'/sso/parame/tree.json',postData:{type:0},
				callback : {
					beforeClick : function(e, t) {
						r.val(t.name);
						queryConditions.category=t.id;
						i.hide();
					}
				}
			});
		/**类别combo END*/
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
		operate : function(e, t) {
			if ("add" == e)
				var i = "新增商品", r = {oper : e,callback : this.callback};
			else
				var i = "修改商品", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit",
				data : r,
				width : 850,
				height : 422,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的商品将不能恢复，请确认是否删除？", function() {
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
							content : "删除商品失败！" + t.msg
						})
				})
			})
		},
		disable : function(id) {
			$.dialog.confirm("确定要（禁用或激活）商品？", function() {
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