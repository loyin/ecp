var queryConditions = {},url=rootPath+"/fa/account",gridQryUrl=url+"/dataGrid.json",
accountType=["现金","银行账号","在线支付"],
 THISPAGE = {
	init:function(){
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom:function(){
	},
	loadGrid:function(){
		function t(t, e, row){
			var html_con = '<div class="operating" data-id="' + row.id + '" data-typ="'+row.yh+'" data-name="'+row.textname+'"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-list mrb" title="查看明细"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
			html_con+='</div>';
			return html_con;
		}
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
			multiselect:false,
			multiboxonly:false,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,
				formatter:t,
				width:250,
				align:"left",
				title:false
			}, {
				name:"yh",
				label:"类别",
				align:"left",formatter:function(t,e,r){return accountType[t]},
				title:true
			}, {
				name:"name",
				label:"名称",
				align:"center",
				title:true
			}, {
				name:"account",
				label:"账号",
				align:"center",
				title:true
			}, {
				name:"bankname",
				label:"开户银行",
				align:"center",
				title:true
			}, {
				name:"khmc",
				label:"开户名称",
				align:"center",
				title:true
			}, {
				name:"amt",
				label:"金额",
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
//			page:1,
//			sortname:"",
//			sortorder:"",
//			pager:"#page",
//			rowNum:50,
//			rowList:[ 50,100, 200 ],
			viewrecords:true,
			shrinkToFit:false,
			forceFit:false,
			jsonReader:{
				root:"data.list",
				records:"data.totalRow",
				repeatitems:false,
				id:"id"
			},
			loadError:function(){
				parent.Public.tips({
					type:1,
					content:"加载数据异常！"
				})
			},
			ondblClickRow:function(t){
				$("#" + t).find(".fa-eye").trigger("click")
			}
		})
	},
	reloadData:function(t){
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",mtype:'POST',
			postData:t
		}).trigger("reloadGrid");
	},
	addEvent:function(){
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t){//查看
			t.preventDefault();
			var e = $(this).parent().data("id");
			var type = $(this).parent().data("type");
			handle.view({id:e,type:type});
		});
		$(".grid-wrap").on("click", ".fa-list", function(t){//查看明细
			t.preventDefault();
			var e = $(this).parent().data("id");
			var type = $(this).parent().data("type");
			handle.detail({account_id:e,type:type});
		});
		$(".grid-wrap").on("click", ".fa-edit", function(e){//编辑
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				var type = $(this).parent().data("type");
				handle.operate({id:id,type:type,pid:'',oper:'edit'});
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t){//删除
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")){
				var e = $(this).parent().data("id");
				handle.del(e,$(this).parent().data("type"));
			}
		});
		$("#search").click(function(t) {
			t.preventDefault();
			queryConditions.type =$("#type").val();
			THISPAGE.reloadData(queryConditions)
		});
		$("#add").click(function(t){
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				handle.operate({id:null,type:0,pid:null,oper:'add'});
			}
		});
		$("#addparame").click(function(t){
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				handle.operate({id:null,type:1,pid:null,oper:'add'});
			}
		});
		$("#btn-batchDel").click(function(e){
			e.preventDefault();
			if (Business.verifyRight("BU_DELETE")){
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				t.length ? handle.del(t.join()):parent.Public.tips({
					type:2,
					content:"请选择需要删除的项"
				})
			}
		});
		$(window).resize(function(){
			Public.resizeGrid()
		})
	}
};
var handle = {
		//opt={e,id,pid,type,parent_name}
		operate:function(opt){
			if ("add" == opt.oper)
				var i = "新增账户";
			else
				var i = "修改账户";
			$.dialog({
				title:i,
				content:"url:"+url+"/edit.html",
				data:opt,
				width:400,
				height:400,
				max:false,
				min:false,
				cache:false,
				lock:true
			})
		},
		//opt={id,pid,type,department_id}
		view:function(opt){
				var i = "查看账户";
			$.dialog({
				title:i,
				content:"url:"+url+"/view.html",
				data:opt,
				width:400,
				height:400,
				max:true,
				min:false,
				cache:false,
				lock:true
			})
		},
		detail:function(opt){
			var i = "查看账户明细";
			$.dialog({
				title:i,
				content:"url:"+url+"/detail.html",
				data:opt,
				width:800,
				height:800,
				max:true,
				min:false,
				cache:false,
				lock:true
			})
		},
		del:function(e,type){
			$.dialog.confirm("删除的账户将不能恢复，请确认是否删除？", function(){
				Public.ajaxPost(url+"/del.json", {
					id:e
				}, function(t){
					if (t && 200 == t.status){
						var i = t.data.id || [];
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除账户失败！" + t.msg})
				})
			})
		},
		callback:function(e, t, i){
		}
	}
THISPAGE.init();