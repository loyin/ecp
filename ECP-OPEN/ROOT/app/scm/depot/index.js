var queryConditions = {},url=rootPath+"/scm/depot",gridQryUrl=url+"/dataGrid.json",
 THISPAGE = {
	init:function() {
		this.loadGrid();
		this.addEvent()
	},
	view:function(id){
		$.dialog({id:"moreCon",width:400,height:200,min:true,max:true,title:"查看仓库",button:[{name:"关闭"	} ],
			resize:true,lock:true,content:"url:"+url+"/view.html",data:{id:id}});
	},
	loadGrid:function() {
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url:gridQryUrl,
			postData:queryConditions,
			datatype:"json",
			mtype:'POST',
			autowidth:true,
			height:i.h,
			altRows:true,
			gridview:true,rownumbers:true,
			multiselect:true,
			multiboxonly:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,width:100,
				formatter:Public.operFmatter,
				align:"center",
				title:false
			}, {
				name:"name",
				label:"仓库名称",
				align:"center",
				width:100,
				title:false
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"name",
			sortorder:"asc",
			pager:"#page",
			rowNum:50,
			rowList:[ 50,100, 200 ],
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
					content :"加载数据异常！"
				})
			},
			ondblClickRow:function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		})
	},
	reloadData:function(t) {
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",mtype:'POST',
			postData:t
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
				handle.del(e);
			}
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
				t.length ? handle.del(t.join()):parent.Public.tips({type:2,content:"请选择需要删除的项"	})
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
				var i = "新增仓库", r = {oper:e,callback:this.callback};
			else
				var i = "修改仓库", r = {oper:e,rowId:t,callback:this.callback};
			$.dialog({title:i,content:"url:"+url+"/edit.html",data:r,
				width:400,height:200,max :true,resize:true,min :false,cache :false,	lock :true
			});
		},
		del:function(id) {
			$.dialog.confirm("删除的仓库将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del.json", {id:id}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data || [];
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除仓库失败！" + t.msg});
				})
			})
		}
	}
THISPAGE.init();