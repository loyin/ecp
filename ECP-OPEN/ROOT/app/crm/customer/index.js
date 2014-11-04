var url=rootPath+ "/crm/customer", gridQryUrl = url+"/dataGrid.json",type=["供应商","企业","个人","经销商"];
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:'',end_date:'',type:-1,qryType:-1,status:0,is_deleted:0,uid:""
	},expoutUrl:"",
	fastQryText:"快速查询",
	fastQry:[
	       //{text:"全部",sl:false},
	       {text:"我创建的",sl:false},
	       {text:"我负责的",sl:false},
	       {text:"下属创建的",sl:false},
	       {text:"下属负责的",sl:false},
	       {text:"",sl:true},
//	       {text:"今日新增",sl:false},
//	       {text:"本周新增",sl:false},
//	       {text:"本月新增",sl:false},
//	       {text:"最近更新",sl:false},
//	       {text:"",sl:true},
	       {text:"一周未跟进",sl:false},
	       {text:"半月未跟进",sl:false},
	       {text:"一直未跟进",sl:false},
	       {text:"",sl:true},
	       {text:"已购买",sl:false},
	       {text:"未购买",sl:false}],
	showMenu:function(code){
		return SYSTEM.rights[code]==true;
	},
	qry:function(type){
		model.query.qryType=type;
		if(type==100){
			model.query.is_deleted=1;
			model.fastQryText="回收站";
		}else{
			model.fastQryText=model.fastQry[type].text;
			model.query.is_deleted=0;
		}
		THISPAGE.reloadData();
	},
	init:function(){
		model.query={
				keyword:"",start_date:'',end_date:'',type:-1,qryType:-1,status:0,is_deleted:0,uid:""
		};
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
			title:"查看客户",
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
					+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>'+(row.is_deleted==1?'<span class="fa fa-reply mrb" title="恢复"></span>':'')+'</div>';
			return html_con;
		}
		function fmtAmt(v) {
			return Public.numToCurrency(v);
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
				name:"sn",
				label:"编号",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"name",
				label:"客户名称",
				align:"center",
				width:100,
				sortable:true,
				title:true
			}, {
				name:"type",
				label:"类型",formatter:function(v,e,r){return type[v];},
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"origin",
				label:"来源",formatter:Public.custParamefmter,
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"member_card",
				label:"会员卡号",
				align:"center",
				width:100,sortable:true,
				title:true
			}, {
				name:"amt",
				label:"金额",
				align:"center",
				width:100,sortable:true,formatter:fmtAmt,
				title:true
			}, {
				name:"integral",
				label:"积分",
				align:"center",
				width:100,sortable:true,
				title:true
			}, {
				name:"rating",
				label:"等级",formatter:Public.custRatingfmter,
				align:"center",
				width:100,
				title:false
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
		});
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
		$(".grid-wrap").on("click", ".fa-trash-o", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				if(model.query.qryType==100)
					handle.del(e);
				else
					handle.trash(e);
			}
		});
		$(".grid-wrap").on("click", ".fa-reply", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
					handle.reply(e);
		});
		$("#search").click(function() {
			THISPAGE.reloadData()
		});
		$("#import").click(function() {//导入
			$.dialog({
				title:"导入客户资料",
				content:"url:" + url + "/upfile",
				width:450,
				height:300,
				max:true,
				min:false,
				cache:false,
				resize:true,
				lock:true
			})
		});
		$("#expout").click(function() {//导出
			model.expoutUrl=url+"/expout";
		});
		$("#refresh").click(function() {
			model.init();
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
					if(model.query.qryType==100)
						handle.del(t.join());
					else
						handle.trash(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要删除的客户"});
			}
		});
		$("#btn-batchAllot").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						handle.allot(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要分配的客户"});
			}
		});
		$("#btn-batchReply").click(function(e) {
			e.preventDefault();
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						handle.reply(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要恢复的客户"});
		});
		$("#toPool").click(function(e) {
			e.preventDefault();
			if (Business.verifyRight("KH_TOPOOL")) {
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				t.length ? handle.toPool(t.join()):parent.Public.tips({
					type:2,
					content:"请选择需要放入客户池的客户"
				})
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
			var i = "新增客户", r = {
				oper:e,
				callback:this.callback
			};
		else
			var i = "修改客户", r = {
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
		$.dialog.confirm("删除的客户将不能恢复，请确认是否删除？", function() {
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
						content:"删除客户失败！" + t.msg
					})
			})
		})
	},
	toPool:function(e) {
		$.dialog.confirm("请确认是否放入客户池？", function() {
			Public.ajaxPost(url + "/toPool.json", {
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
						content:"批量放入客户池失败！" + t.msg
					})
			})
		})
	},
	reply:function(e) {
		Public.ajaxPost(url + "/reply.json", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({type:1,content:"恢复客户失败！" + t.msg
					});
			});
	},
	trash:function(e) {
			Public.ajaxPost(url + "/trash", {id:e}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({type:1,content:"删除客户失败！" + t.msg});
			});
	},
	allot:function(id) {
		var uid="";
		$.dialog({
			title:"分配人员",
			content:'<b>分配给：</b><input type="text" class="ui-input" readonly="readonly" id="allotToUid">',
			init:function(){
				var r=$("#allotToUid");
				var i=Public.comboTree(r,{width:260,offset:{left:80},url:rootPath+'/sso/user/userTree.json?type=2',callback:{
					beforeClick:function(e, t) {
						if(t.type==10){
							r.val(t.name);uid=t.id;
							i.hide();
						}
					}}
				});
			},
			button:[{name:"确定",focus:true,callback:function(){
				if(uid==""){
					parent.Public.tips({type:1,content:"未选择分配人员！"});
					return false;
				}
				Public.ajaxPost(url + "/allot", {id:id,uid:uid,type:2}, function(t) {
					if (t && 200 == t.status) {
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"分配客户失败！" + t.msg});
				});
			}},{name:"关闭"}],
			width:200,height:10,max:false,	min:false,resize:false});
	}
}
THISPAGE.init();