var url=rootPath+"/scm/storageBill",gridQryUrl=url+"/flowDataGrid.json",custParame=SYSTEM.custParame,typeList=custParame.typeList,
billType=['采购入库','销售退货入库','调拨入库','其它入库','销售出库','采购退货出库','调拨出库','其它出库'];
var model = avalon.define({$id:'view',
	query:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,type:"",is_deleted:0,status:""},
	parameList:typeList,billType:billType,
	init:function() {
		$(".ui-datepicker-input").datepicker();
		this.loadGrid();
		this.addEvent()
	},
	resetQry:function(){
		model.query={keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,status:"",ordertype:type};
		model.reloadData();
	},
	loadGrid:function() {
				function t(val, opt, row) {
					var html_con = '<div class="operating" data-id="'+ row.id+'"><span class="fa fa-eye mrb" title="查看"></span>';
					html_con += '</div>';
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
			gridview:true,rownumbers:true,
			colModel:[ {
				name:"operating",
				label:"操作",
				fixed:true,width:50,
				formatter:t,
				align:"center",
				title:false
			}, {
				name:"head_name",
				label:"负责人",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"type",
				label:"类别",
				align:"center",formatter:function(v){return billType[v];},
				width:100,sortable:true,
				title:false
			}, {
				name:"billsn",
				label:"单号",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"depot_name",
				label:"仓库",
				align:"center",sortable:true,
				width:100,
				title:false
			}, {
				name:"bill_date",
				label:"单据日期",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"category",
				label:"商品类别",
				align : "center",formatter:function(v,e,r){return parent.SYSTEM.custParame[v].name;},
				width:100,sortable:true,
				title:false
			}, {
				name:"product_sn",
				label:"商品编号",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"product_name",
				label:"商品名称",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"unit",
				label:"计量单位",
				align : "center",formatter:function(v,e,r){return parent.SYSTEM.custParame[v].name;},
				width:100,sortable:true,
				title:false
			}, {
				name:"amount",
				label:"数量",
				align:"center",
				width:100,sortable:true,
				title:false
			}, {
				name:"creater_name",
				label:"创建人",sortable:true,
				align:"center",
				width:100,
				title:false
			}, {
				name:"create_datetime",
				label:"创建时间",sortable:true,
				align:"center",
				width:100,
				title:false
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"create_datetime",
			sortorder:"desc",
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
				model.view(t);
			}
		})
	},
	reloadData:function() {
		$("#grid").jqGrid("setGridParam", {
			url:gridQryUrl,
			datatype:"json",mtype:'POST',
			postData:model.query.$model
		}).trigger("reloadGrid");
	},
	addEvent:function() {
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
		$(".grid-wrap").on("click", ".submit", function(e) {
			e.preventDefault();
			if (Business.verifyRight("TD_UPDATE")) {
				var t = $(this).parent().data("id");
				model.submit(t)
			}
		});
		$(".grid-wrap").on("click", ".fa-trash-o", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).parent().data("id");
				if(model.query.qryType==4)
					model.del(e);
				else
					model.trash(e);
			}
		});
		$(".grid-wrap").on("click", ".fa-reply", function(t) {
			t.preventDefault();
				var e = $(this).parent().data("id");
					model.reply(e);
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
				t.length ? 
						((model.query.qryType==4)?model.del(t.join()):model.trash(t.join()))
						:parent.Public.tips({
					type:2,
					content:"请选择需要删除的项"
				})
			}
		});
		$("#btn-batchReply").click(function(e) {
			e.preventDefault();
				var t = $("#grid").jqGrid("getGridParam", "selarrrow");
				if(t.length){
						model.reply(t.join());
				}else
					parent.Public.tips({type:2,content:"请选择需要恢复的出库入库单"});
		});
		$(window).resize(function() {
			Public.resizeGrid()
		})
	},
	view:function(id){
		$.dialog({id:"dialog1",width:900,height :700,min:true,max:true,
			title:"查看出库入库单",button:[{name:"关闭"	} ],resize:true,lock:true,
			content:"url:"+url+"/view.html",data:{id:id,type:type}});
	}
});
model.init();