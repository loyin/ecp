var url=rootPath+ "/crm/customer", gridQryUrl = url+"/dataGrid.json",type=["供应商","企业","个人"];
var model = avalon.define({$id:'view',
	query:{
		keyword:"",start_date:'',end_date:'',type:-1,qryType:0,status:1,is_deleted:0,uid:""
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
		var r=$("#createrCombo");
		var i=Public.comboTree(r,{width:260,url:rootPath+'/sso/user/userTree.json?type=2',callback:{
			beforeClick:function(e, t) {
				r.val(t.name);model.query.uid=t.id;
				i.hide();
			}}
		});
		/**负责人combo END*/
	},
	view:function(id) {
		$.dialog({id:"moreCon",width:850,height:600,min:true,max:true,title:"查看客户",button:[ {name:"关闭"} ],	resize:true,
			lock:true,content:"url:" + url + "/view",data:{id:id}
		});
	},
	loadGrid:function() {
		function t(val, opt, row) {
			var html_con = '<div class="operating" data-id="'
					+ row.id
					+ '"><span class="fa fa-eye mrb" title="查看"></span></div>';
			return html_con;
		}
		function e(t) {
			var e = t.join('<p class="line" />');
			return e
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
				width:100,hidden:true,
				formatter:t,
				align:"center",
				title:false
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
		})
	},
	reloadData:function() {
		$("#grid").jqGrid("setGridParam", {url:gridQryUrl,datatype:"json",mtype:'POST',postData:model.query.$model}).trigger("reloadGrid");
	},
	addEvent:function() {
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			THISPAGE.view(e);
		});
		$(".grid-wrap").on("click", ".fa-share", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
				handle.receive(e);
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
				}, {name:"取消"} ],
				resize:false,
				content:"url:" + url + "/moreSearch",
				data:model.query.$model
			});
		});
		$("#btn-batchReceive").click(function(e) {
			e.preventDefault();
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						handle.receive(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要领取的客户"});
		});
		$("#btn-batchAllot").click(function(e) {
			e.preventDefault();
			var t = $("#grid").jqGrid("getGridParam", "selarrrow");
			if(t.length){
				handle.allot(t.join());
			}else
				parent.Public.tips({type:2,content:"请选择需要领取的客户"});
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
	receive:function(e) {
		$.dialog.confirm("请确认是否领取？", function() {
			Public.ajaxPost(url + "/allot.json", {
				id:e,type:1
			}, function(t) {
				if (t && 200 == t.status) {
					parent.Public.tips({type:2,content:t.msg});
					THISPAGE.reloadData();
				} else
					parent.Public.tips({type:1,content:"恢复客户失败！" + t.msg})
			})
		})
	},
	allot:function(id) {
		var uid="";
		$.dialog({
			title:"分配人员",
			content:'<b>分配给：</b><input type="text" class="ui-input" readonly="readonly" id="allotToUid">',
			init:function(){
				var r=$("#allotToUid");
				var i=Public.comboTree(r,{width:260,offset:{left:81},url:rootPath+'/sso/user/userTree.json?type=2',callback:{
					beforeClick:function(e, t) {
						if(t.type==10){
							r.val(t.name);uid=t.id;
							i.hide();
						}
					}}
				});
			},
			button:[{name:"确定",callback:function(){
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