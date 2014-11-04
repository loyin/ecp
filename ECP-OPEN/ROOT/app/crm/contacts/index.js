var url=rootPath+ "/crm/contacts", gridQryUrl = url+"/dataGrid.json";
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,type:-1,qryType:0,status:0,is_deleted:0,uid:""
	},
	qry:function(type){
		model.query.qryType=type;
		THISPAGE.reloadData();
	}
});
var THISPAGE = {
	init:function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom:function() {
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
	},
	view:function(id) {
		$.dialog({
			id:"moreCon",
			width:850,
			height:600,
			min:true,
			max:true,
			title:"查看联系人",
			button:[ {
				name:"关闭"
			} ],
			resize:true,
			lock:true,
			content:"url:" + url + "/view",
			data:{
				id:id
			}
		});
	},
	loadGrid:function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'
					+ row.id
					+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span></div>';
			return html_con;
		}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url:gridQryUrl,
			postData:model.query.$model,
			datatype:"json",
			mtype:'POST',
			autowidth:true,
			height:i.h,
			altRows:true,
			gridview:true,
			rownumbers:true,
			multiselect:true,
			multiboxonly:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,
				width:100,
				formatter:t,
				align:"center",
				title:false
			}, {
				name:"head_name",
				label:"负责人",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"name",
				label:"姓名",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"sex",
				label:"性别",
				align:"center",formatter:function(v){var sex=["女","男"];return sex[v];},
				width:100,
				sortable:true,
				title:true
			}, {
				name:"customer_name",
				label:"所属客户",
				align:"center",
				width:100,formatter:function(v,e,r){return v;if(v)return "<a href='#' data-customter_id='"+r.customer_id+"'>"+v+"</a>";else return "-";},
				sortable:true,
				title:true
			}, {
				name:"mobile",
				label:"手机",
				align:"center",
				width:100
			}, {
				name:"telephone",
				label:"电话",
				align:"center",
				width:100
			}, {
				name:"email",
				label:"Email",
				align:"center",
				width:100
			}, {
				name:"qq",
				label:"QQ",
				align:"center",
				width:100
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
				name:"creater_name",
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
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"sn",
			sortorder:"desc",
			pager:"#page",
			rowNum:50,
			rowList:[ 50, 100, 200 ],
			viewrecords:true,
			shrinkToFit:false,
			forceFit:false,
			jsonReader:{
				root:"data.list",
				records:"data.totalRow",
				repeatitems:false,
				id:"id"
			},
			loadError:function() {
				parent.Public.tips({
					type:1,
					content:"加载数据异常！"
				})
			},
			ondblClickRow:function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		})
	},
	reloadData:function() {
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",
			mtype:'POST',
			postData:model.query.$model
		}).trigger("reloadGrid");
	},
	addEvent:function() {
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
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
		$("#refresh").click(function() {
			THISPAGE.reloadData()
		});
		$("#moreCon").click(function() {
			$.dialog({
				id:"moreCon",
				width:480,
				height:300,
				min:false,
				max:false,
				title:"高级搜索",
				button:[ {
					name:"<i class='fa fa-search'></i>查询",
					focus:true,
					callback:function() {
						THISPAGE.reloadData();
					}
				}, {
					name:"取消"
				} ],
				resize:false,
				content:"url:" + url + "/moreSearch",
				data:model.query.$model
			})
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if (Business.verifyRight("TF_ADD")) {
				handle.operate('add');
			}
		});
		$("#btn-batchDel").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						handle.del(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要删除的联系人"});
			}
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
	operate:function(e, t) {
		if ("add" == e)
			var i = "新增联系人", r = {
				oper:e,
				callback:this.callback
			};
		else
			var i = "修改联系人", r = {
				oper:e,
				id:t,
				callback:this.callback
			};
		$.dialog({
			title:i,
			content:"url:" + url + "/edit",
			data:r,
			width:850,
			height:600,
			max:true,
			min:false,
			cache:false,
			resize:true,
			lock:true
		})
	},
	del:function(e) {
		$.dialog.confirm("删除的联系人将不能恢复，请确认是否删除？", function() {
			Public.ajaxPost(url + "/del.json", {
				id:e
			}, function(t) {
				if (t && 200 == t.status) {
					
					parent.Public.tips({
						type:2,
						content:t.msg
					});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({
						type:1,
						content:"删除联系人失败！" + t.msg
					})
			})
		})
	}
}
THISPAGE.init();