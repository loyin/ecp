var url=rootPath+"/crm/campaigns",gridQryUrl=url+"/dataGrid.json",status=["计划中","激活","禁止","完成","取消"];
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,is_transformed:"",qryType:-1,uid:""
	},
	fastQryText:"快速查询",
	fastQry:[
	       {text:"我创建的",sl:false},
	       {text:"下属创建的",sl:false},
	       {text:"我负责的",sl:false},
	       {text:"下属负责的",sl:false}
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
			height :500,
			min : true,
			max : true,
			title : "查看营销活动",
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
				label : "营销活动名称",
				align : "center",
				width:100,
				title : false
			}, {
				name : "head_name",
				label : "负责人",
				align : "center",
				width:100,
				title : false
			}, {
				name : "type",
				label : "营销类别",
				align : "center",
				width:100,
				title : false
			}, {
				name : "status",
				label : "状态",formatter:function(v){return status[v];},
				align : "center",
				width:100,
				title : false
			}, {
				name : "end_date",
				label : "预期结束日期",
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
				var i = "新增营销活动", r = {oper : e,callback : this.callback};
			else
				var i = "修改营销活动", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit.html",
				data : r,
				width : 800,
				height :500,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的营销活动将不能恢复，请确认是否删除？", function() {
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
							content : "删除营销活动失败！" + t.msg
						})
				})
			})
		}
	}
THISPAGE.init();