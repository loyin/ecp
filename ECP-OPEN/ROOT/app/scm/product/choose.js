function callback(e) {
	var t = frameElement.api, i = t.data.id, r = t.data.callback, 
	n = $("#grid").jqGrid("getGridParam", "selarrrow"), o = n.length, s = oldRow = parent.curRow, l = parent.curCol;

	parent.$("#grid").jqGrid("restoreCell", s, l);
	var d = $("#grid").jqGrid("getRowData",
			$("#grid").jqGrid("getGridParam", "selrow")), c = d.name;
	if (s > 8 && s > oldRow)
		;
	else
		;
	var u = parent.$("#grid").jqGrid("getRowData", Number(i));
	u = $.extend({}, u, {
		goods : c,
		invNumber : d.number,
		invName : d.name,
		unitName : d.unitName,
		qty : 1,
		price : d.salePrice,
		spec : d.spec
	});
	parent.$("#grid").jqGrid("setRowData", Number(i), u);
	r(i)
}
var api=frameElement.api,multiselect=api.data.multiselect, SYSTEM = parent.parent.SYSTEM;
var model = avalon.define({
	$id : "ctrl",
	data : {
		keyword : "",
		category : "",
		qryField : "category"
	}
});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
		this.$_matchCon = $("#matchCon");
		this.$_matchCon.placeholder()
	},
	loadGrid : function() {
		function e(e, t, i) {
			var a = '<div class="operating" data-id="'
					+ i.id
					+ '"><a class="ui-icon ui-icon-search" title="查询库存"></a></div>';
			return a
		}
		function fmtAmt(v) {
			return Public.numToCurrency(v);
		}
		$(window).height() - $(".grid-wrap").offset().top - 84;
		$("#grid").jqGrid(
				{
					url : "/scm/product/dataGrid.json",
					postData : model.data.$model,
					datatype : "json",
					mtype : 'POST',
					width : 578,
					rownumbers : true,
					height : 354,
					altRows : true,
					gridview : true,
					colModel : [
							{
								name : "id",
								label : "ID",
								width : 0,
								hidden : true
							},
							{
								name : "operating",
								label : "操作",
								width : 60,
								fixed : true,
								formatter : e,
								align : "center"
							},
							{
								name : "billsn",
								label : "商品编号",
								align : "center",
								sortable : true,
								title : true
							},
							{
								name : "name",
								label : "商品名称",
								align : "center",
								sortable : true,
								title : true
							},
							{
								name : "model",
								label : "商品型号",
								align : "center",
								sortable : true,
								title : true
							},
							{
								name : "category_name",
								label : "类别",
								align : "center",
								sortable : true,
								title : true
							},
							{
								name : "unit_name",
								label : "计量单位",
								align : "center",
								width : 100,
								title : true
							},
							{
								name : "sale_price",
								hidden : false,
								label : "销售价格",
								align : "right",formatter:fmtAmt,
								sortable : true,
								title : false
							},
							{
								name : "purchase_price",
								label : "采购价格",
								width : 120,formatter:fmtAmt,
								hidden : true,
								align : "right",
								title : false
							} ],
					cmTemplate : {
						sortable : false
					},
					multiselect:multiselect,
					page : 1,
					sortname : "billsn",
					sortorder : "asc",
					pager : "#page",
					page : 1,
					rowNum : 50,
					viewrecords : true,
					shrinkToFit : true,
					forceFit : false,
					jsonReader : {
						root : "data.list",
						records : "data.totalRow",
						repeatitems : false,
						id : "id"
					},
					loadError : function() {
					},
					ondblClickRow : function() {
						if (frameElement==false) {
							callback();
							api.close()
						}
					}
				})
	},
	reloadData : function() {
		$("#grid").jqGrid("setGridParam", {
			url : "/scm/product/dataGrid.json",
			datatype : "json",
			mtype : 'POST',
			postData : model.data.$model
		}).trigger("reloadGrid")
	},
	addEvent : function() {
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
	}
};
THISPAGE.init();