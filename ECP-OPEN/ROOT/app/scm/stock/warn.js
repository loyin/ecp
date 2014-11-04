var url=rootPath+"/scm/stock",gridQryUrl=url+"/warnDataGrid.json";
var model = avalon.define({$id:'view',data:{keyword : "",category_id:'',depot_id:''}});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
	},
	loadGrid : function() {
		function fred(v,e,r){
			return (r.amount==null||r.stock_warn>r.amount)?"<font color='red'>"+v+"</font>":v;
		}
		function custParame(v,e,r){
			return (r.amount==null||r.stock_warn>r.amount)?"<font color='red'>"+SYSTEM.custParame[v].name+"</font>":SYSTEM.custParame[v].name;
		}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url : gridQryUrl,
			postData : model.data.$model,
			datatype : "json",
			mtype:'POST',
			autowidth : true,
			height : i.h,
			altRows : true,
			gridview : true,rownumbers:true,
			colModel : [ {
				name : "category",
				label : "类别",sortable:true,
				align : "center",sortable:true,formatter:custParame,
				title : true
			}, {
				name : "billsn",
				label : "商品编号",sortable:true,
				align : "center",sortable:true,formatter:fred,
				title : true
			}, {
				name : "product_name",
				label : "商品名称",sortable:true,
				align : "center",sortable:true,formatter:fred,
				title : true
			}, {
				name : "model",
				label : "商品型号",sortable:true,
				align : "center",sortable:true,formatter:fred,
				title : true
			}, {
				name : "unit",
				label : "计量单位",
				align : "center",
				width:100,formatter:custParame,
				title : true
			}, {
				name : "stock_warn",
				label : "预警库存",
				align : "center",sortable:true,
				width:100,formatter:fred,
				title : false
			}, {
				name : "amount",
				label : "当前库存",sortable:true,
				align : "center",formatter:function(v,e,r){return "<font color='"+(r.stock_warn>v?"red":"green")+"'>"+v+"<font>";},
				width:100,
				title : false
			} ],
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "product_name",
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
	reloadData : function(t) {
		$("#grid").jqGrid("setGridParam", {
			url : gridQryUrl,
			datatype : "json",mtype:'POST',
			postData : t
		}).trigger("reloadGrid");
	},
	addEvent : function() {
		Public.dateCheck();
		$("#search").click(
			function() {
				THISPAGE.reloadData(model.data.$model)
		});
		/**商品类别combo START*/
		var r = $("#category");
		var i=Public.comboTree(r,{url:rootPath+'/sso/parame/tree.json',postData:{type:0},
				callback : {
					beforeClick : function(e, t) {
						r.val(t.name);r.data("pid", t.id);
						model.data.category_id=t.id;
						i.hide();
					}
				}
			});
		/**商品类别combo END*/
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
THISPAGE.init();