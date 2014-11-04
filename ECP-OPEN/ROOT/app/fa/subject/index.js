var url=rootPath+"/fa/subject",gridQryUrl=url+"/dataGrid.json",
tab=parent.tab,type=["资产","负责","权益","成本","损益"],direction=["借","贷"],
category=[
//资产0
"流动资产","非流动资产",
//负债1
"流动负债","非流动负债",
/*权益2*/
"所有者权益",/*成本3*/"成本",
/*损益4*/
"营业收入","其他收益",
"其他费用","期间费用",
"其他损失","营业成本及税金",
"以前年度损益调整","所得税"
],
category_json=[
//资产：
[{text:"流动资产",val:0},{text:"非流动资产",val:1}],
//负债：
[{text:"流动负债",val:2},{text:"非流动负债",val:3}],
//权益：
[{text:"所有者权益",val:4}],
//成本：
[{text:"成本",val:5}],
//损益：
[{text:"营业收入",val:6},{text:"其他收益",val:7},{text:"其他费用",val:8},{text:"期间费用",val:9},{text:"其他损失",val:10},{text:"营业成本及税金",val:11},
 {text:"以前年度损益调整",val:12},{text:"所得税",val:13}]
],
SYSTEM = parent.SYSTEM;
var model=avalon.define({
	$id:"ctrl",
	data:{type:0},
	type:type,
	search:function(){
		THISPAGE.reloadData();
	}
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
			var html_con = '<div class="operating" data-id="' + row.id + '" data-name="'+row.textname+'" data-type="'+row.type+'" data-category="'+row.category+'"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
			html_con+='<span class="fa fa-plus mrb subParame" title="添加子科目"></span>';
			html_con+='</div>';
			return html_con;
		}
		var i = Public.setGrid();
		$("#grid").jqGrid({
			url:gridQryUrl,
			postData:model.data.$model,
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
				width:100,
				align:"left",
				title:false
			}, {
				name:"code",
				label:"编码",
				align:"left",
				title:true
			}, {
				name:"name",
				label:"名称",
				align:"left",
				title:true
			}, {
				name:"type",
				label:"类别",
				align:"left",formatter:function(t,e,r){return type[r.type]},
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
			postData:model.data.$model
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
		$(".grid-wrap").on("click", ".subParame", function(e){//添加子级科目
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				var type = $(this).parent().data("type");
				var name = $(this).parent().data("name");
				var category = $(this).parent().data("category");
				handle.operate({id:null,type:type,pid:null,parent_id:id,parent_name:name,oper:'add',category:category});
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t){//删除
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")){
				var e = $(this).parent().data("id");
				handle.del(e,$(this).parent().data("type"));
			}
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
				var i = "新增科目";
			else
				var i = "修改科目";
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
				var i = "查看科目";
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
			$.dialog.confirm("删除的科目将不能恢复，请确认是否删除？", function(){
				Public.ajaxPost(url+"/del.json", {
					id:e
				}, function(t){
					if (t && 200 == t.status){
						var i = t.data.id || [];
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除科目失败！" + t.msg})
				})
			})
		},
		callback:function(e, t, i){
		}
	}
THISPAGE.init();