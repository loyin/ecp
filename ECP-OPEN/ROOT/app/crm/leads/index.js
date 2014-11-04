var url=rootPath+"/crm/leads",gridQryUrl=url+"/dataGrid.json";
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,is_transformed:"",qryType:-1,uid:""
	},
	fastQryText:"快速查询",
	fastQry:[
	       {text:"我创建的",sl:false},
	       {text:"下属创建的",sl:false},
	       {text:"我负责的",sl:false}
	       //,
//	       {text:"下属负责的",sl:false},
//	       {text:"未转换",sl:false},
//	       {text:"已转换",sl:false}
	       ],
	qry:function(type){
		model.query.qryType=type;
		model.fastQryText=model.fastQry[type].text;
		THISPAGE.reloadData();
	}
});
var THISPAGE = {
	init : function() {
		$(".ui-datepicker-input").datepicker();
		/**负责人*/
		var r=$("#headCombo");
		var i=Public.comboTree(r,{width:260,url:rootPath+'/sso/user/userTree.json?type=2',callback:{
			beforeClick:function(e, t) {
				if(t.type==10){
					r.val(t.name);model.query.uid=t.id;
				}else{
					r.val("");model.query.uid="";
				}
				i.hide();
			}}
		});
		/**负责人combo END*/
		this.loadGrid();
		this.addEvent()
	},
	view:function(id){
		$.dialog({
			id : "moreCon",
			width : 800,
			height :300,
			min : true,
			max : true,
			title : "查看线索",
			button : [{name : "关闭"	} ],
			resize : true,lock:true,
			content : "url:"+url+"/view.html",
			data : {id:id}
		});
	},
	loadGrid : function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'+ row.id+'"><span class="fa fa-eye mrb" title="查看"></span>';
			if(row.creater_id==SYSTEM.user.id||row.head_id==SYSTEM.user.id)
			html_con+='<span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
			html_con+='</div>';
			return html_con;
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
				formatter:t,
				align : "center",
				title : false
			}, {
				name : "name",
				label : "主题",
				align : "center",
				width:100,
				title : false
			}, {
				name : "customer_name",
				label : "客户名称",
				align : "center",
				width:100,
				title : false
			}, {
				name : "contacts_name",
				label : "联系人",
				align : "center",
				width:100,
				title : false
			}, {
				name : "telephone",
				label : "电话",
				align : "center",
				width:100,
				title : false
			}, {
				name : "mobile",
				label : "手机",
				align : "center",
				width:100,
				title : false
			}, {
				name : "is_transformed",
				label : "是否转换",formatter:function(v){var vv=["未转换","已转换"];return vv[v];},
				align : "center",
				width:100,
				title : false
			}, {
				name : "nextstep_date",
				label : "下次联系时间",
				align : "center",
				width:100,
				title : false
			}, {
				name : "creater_name",
				label : "创建人",
				align : "center",
				width:100,
				title : false
			}, {
				name : "create_datetime",
				label : "创建时间",
				align : "center",
				width:100,
				title : false
			}, {
				name : "updater_name",
				label : "修改人",
				align : "center",
				width:100,
				title : false
			}, {
				name : "update_datetime",
				label : "修改时间",
				align : "center",
				width:100,
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
				THISPAGE.view(t);
			}
		})
	},
	reloadData : function() {
		$("#grid").jqGrid("setGridParam", {
			url : gridQryUrl,
			datatype : "json",mtype:'POST',
			postData : model.query.$model
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
		$(".grid-wrap").on("click", ".fa-exchange", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				handle.exchange(e);
			}
		});
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
		$("#refresh").click(function() {
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
				var i = "新增线索", r = {oper : e,callback : this.callback};
			else
				var i = "修改线索", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit.html",
				data : r,
				width : 800,
				height :300,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的线索将不能恢复，请确认是否删除？", function() {
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
							content : "删除线索失败！" + t.msg
						})
				})
			})
		},
		exchange: function(id) {
			$.dialog.confirm("确定要将此线索转换成商机吗？", function() {
				Public.ajaxPost(url+"/exchange.json", {
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
							content : "转换商机失败！" + t.msg
						})
				})
			})
		}
	}
THISPAGE.init();