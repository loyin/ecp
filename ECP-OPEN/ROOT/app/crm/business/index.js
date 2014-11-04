var url=rootPath+"/crm/business",gridQryUrl=url+"/dataGrid.json",custParame=SYSTEM.custParame,typeList=custParame.typeList;
var model = avalon.define({$id:'view',
	query:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",origin:"",type:"",uid:"",qryType:-1},
	fastQryText:"快速查询",
	fastQry:[
		       {text:"我创建的",sl:false},
		       {text:"我负责的",sl:false},
		       {text:"下属创建的",sl:false},
		       {text:"下属负责的",sl:false}],
	qry:function(type){
		model.query.qryType=type;
		if(type==100){
			model.query.is_deleted=1;
			model.fastQryText="回收站";
		}else{
			model.fastQryText=model.fastQry[type].text;
			model.query.is_deleted=0;
		}
		model.reloadData();
	},
	parameList:typeList,
	reset:function(){
		model.fastQryText="快速查询";
		model.query={keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",origin:"",type:"",uid:"",qryType:-1};
		model.reloadData();
	},
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
		model.loadGrid();
		model.addEvent()
	},
	loadGrid : function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'
					+ row.id
					+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>'+(row.is_deleted==1?'<span class="fa fa-reply mrb" title="恢复"></span>':'')+'</div>';
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
				name : "head_name",
				label : "负责人",
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
				name : "due_date",
				label : "预计成交日期",
				align : "center",
				width:100,
				title : false
			}, {
				name:"estimate_price",
				label:"预计金额",
				align:"center",formatter:function(v,e,r){return Public.numToCurrency(v);},
				width:100,
				title:false
			}, {
				name : "type",
				label : "类型",
				align : "center",formatter:Public.custParamefmter,
				width:100,
				title : false
			}, {
				name : "status",
				label : "状态",
				align : "center",formatter:Public.custParamefmter,
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
				name : "nextstep_date",
				label : "下次联系",
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
				model.view(t);
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
			model.view(e);
		});
		$(".grid-wrap").on("click", ".fa-edit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.operate("edit", t)
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				model.del(e);
			}
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				model.operate('add');
			}
		});
		$("#btn-batchDel").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				t.length ? model.del(t.join()) : parent.Public.tips({
					type : 2,
					content : "请选择需要删除的项"
				})
			}
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	},
	operate : function(e, t) {
			if ("add" == e)
				var i = "新增商机", r = {oper : e,callback : this.callback};
			else
				var i = "修改商机", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit.html",
				data : r,
				width : 900,
				height :600,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		view:function(id){
			$.dialog({
				id : "moreCon",
				width : 900,
				height :600,
				min : true,
				max : true,
				title : "查看商机",
				button : [{name : "关闭"	} ],
				resize : true,lock:true,
				content : "url:"+url+"/view.html",
				data : {id:id}
			});
		},
		del : function(id) {
			$.dialog.confirm("删除的商机将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del.json", {
					id : id
				}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data || [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						model.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "删除商机失败！" + t.msg
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
);
model.init();