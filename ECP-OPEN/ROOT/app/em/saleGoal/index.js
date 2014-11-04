var url=rootPath+"/em/saleGoal",gridQryUrl=url+"/dataGrid.json",year=new Date(SYSTEM.date).getFullYear();
var model = avalon.define({$id:'view',
	query:{keyword:"",year:""},yearList:new Array()
});
var THISPAGE={
	init : function() {
		for(var i=year-3;i<=year+1;i++){
			model.yearList.push(i);
		}
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
			width : 1000,
			height :300,
			min : true,
			max : true,
			title : "查看销售目标",
			button : [{name : "关闭"	} ],
			resize : true,lock:true,
			content : "url:"+url+"/view.html",
			data : {id:id}
		});
	},
	loadGrid : function() {
		function t(t, e, i) {
			var a = '<div class="operating" data-id="'+ i.id+ '"><span class="fa fa-eye mrb" title="查看"></span>';
			if(f==1)
			a+='<span class="mrb fa fa-edit" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span></div>';
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
			colModel : [ {width:100,
				name : "operating",
				label : "操作",
				fixed : true,
				formatter :t,
				align : "center",
				title : false
			}, {
				width:100,name : "realname",
				label : "姓名",
				align : "center",
				title : false
			}, {
				width:100,name : "department_name",
				label : "部门",
				align : "center",
				title : false
			}, {
				width:100,name : "year",
				label : "年度",
				align : "center",
				title : false
			}, {
				width:100,name : "y",
				label : "年度目标",
				align : "center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				title : false
			}, {
				width:100,name : "yv",
				label : "年度完成",
				align : "center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				title : false
			}, {
				width:100,name : "q1",
				label : "第一季度",
				align : "center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				title : false
			}, {
				width:100,name : "qv1",
				label : "第一季度完成",
				align : "center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				title : false
			}, {
				width:100,name : "q2",
				label : "第二季度",formatter:function(v,e,r){return Public.numToCurrency(v);},
				align : "center",
				title : false
			}, {
				width:100,name : "qv2",formatter:function(v,e,r){return Public.numToCurrency(v);},
				label : "第二季度完成",
				align : "center",
				title : false
			}, {
				width:100,name : "q3",formatter:function(v,e,r){return Public.numToCurrency(v);},
				label : "第三季度",
				align : "center",
				title : false
			}, {
				width:100,name : "qv3",formatter:function(v,e,r){return Public.numToCurrency(v);},
				label : "第三季度完成",
				align : "center",
				title : false
			}, {
				width:100,name : "q4",formatter:function(v,e,r){return Public.numToCurrency(v);},
				label : "第四季度",
				align : "center",
				title : false
			}, {
				width:100,name : "qv4",formatter:function(v,e,r){return Public.numToCurrency(v);},
				label : "第四季度完成",
				align : "center",
				title : false
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
				var i = "新增销售目标", r = {oper : e,callback : this.callback};
			else
				var i = "修改销售目标", r = {oper:e,rowId:t};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit.html",
				data : r,
				width : 1000,
				height :300,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的销售目标将不能恢复，请确认是否删除？", function() {
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
							content : "删除销售目标失败！" + t.msg
						})
				})
			})
		}
	}
THISPAGE.init();