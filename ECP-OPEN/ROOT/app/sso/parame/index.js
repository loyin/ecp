var url=rootPath+"/sso/parame",gridQryUrl=url+"/dataGrid.json",parameType=CONFIG.custParameType;
var model=avalon.define({
	$id:'ctrl',
	query:{type:''},
	parameType:parameType
});
var THISPAGE = {
	init:function(){
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom:function(){
	},
	loadGrid:function(){
		function t(t, e, row){
			var html_con = '<div class="operating" data-id="' + row.id + '" data-typ="'+row.type+'" data-name="'+row.textname+'"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
			html_con+='<span class="fa fa-plus mrb subParame" title="添加子参数"></span>';
			html_con+='</div>';
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
			altRows:true,rownumbers:true,
			gridview:true,
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
				name:"name",
				label:"名称",
				align:"left",
				title:true
			}, {
				name:"type",
				label:"类别",
				align:"left",formatter:function(t,e,r){return parameType[r.type];},
				title:true
			}, {
				name:"sort_num",
				label:"排序号",
				align:"center",
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
			rowNum:1e10,
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
	reloadData:function(){
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",mtype:'POST',
			postData:model.query.$model
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
		$(".grid-wrap").on("click", ".fa-edit", function(e){//编辑
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				var type = $(this).parent().data("type");
				handle.operate({id:id,type:type,pid:'',oper:'edit'});
			}
		});
		$(".grid-wrap").on("click", ".subParame", function(e){//添加子级参数
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				var type = $(this).parent().data("typ");
				var name = $(this).parent().data("name");
				handle.operate({id:null,type:type,pid:null,parent_id:id,parent_name:name,oper:'add'});
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
			THISPAGE.reloadData()
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
		Public.pageTab();
	}
};
var handle = {
		//opt={e,id,pid,type,parent_name}
		operate:function(opt){
			if ("add" == opt.oper)
				var i = "新增参数";
			else
				var i = "修改参数";
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
				var i = "查看参数";
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
		del:function(e,type){
			$.dialog.confirm("删除的参数将不能恢复，请确认是否删除？", function(){
				Public.ajaxPost(url+"/del.json", {
					id:e
				}, function(t){
					if (t && 200 == t.status){
						var i = t.data.id || [];
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除参数失败！" + t.msg})
				})
			})
		},
		callback:function(e, t, i){
		}
	}
THISPAGE.init();