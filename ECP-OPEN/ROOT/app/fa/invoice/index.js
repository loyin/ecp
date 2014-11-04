var url=rootPath+"/fa/invoice",gridQryUrl=url+"/dataGrid.json",
tab=parent.tab,pjlx=["普通发票","增值税发票","收据","支票","其它"],type=["出票","进票"];
var model = avalon.define({$id:'view',data:{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,type:'',pjlx:''},pjlx:pjlx,type:type});
var  THISPAGE = {
	init:function(){
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom:function(){
		$(".ui-datepicker-input").datepicker();
	},
	loadGrid:function(){
		function t(t, e, row){
			var html_con = '<div class="operating" data-id="' + row.id + '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span>';
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
				width:250,
				align:"left",
				title:false
			}, {
				name:"type",
				label:"类型",
				align:"center",formatter:function(t,e,r){return type[t]},
				title:true
			}, {
				name:"pjlx",
				label:"票据类型",
				align:"center",formatter:function(t,e,r){return pjlx[t]},
				title:true
			}, {
				name:"fpsn",
				label:"票据号码",
				align:"center",
				title:true
			}, {
				name:"order_billsn",
				label:"订单编号",
				align:"center",
				title:true
			}, {
				name:"amt",
				label:"金额",
				align:"right",formatter:function(v,e,r){return Public.numToCurrency(v);},
				title:true
			}, {
				name:"kprq",
				label:"开票日期",
				align:"center",
				title:true
			}, {
				name:"head_name",
				label:"负责人",
				align:"center",
				title:true
			}, {
				name:"creater_name",
				label:"创建人",
				align:"center",
				title:true
			}, {
				name:"create_datetime",
				label:"创建时间",
				align:"center",
				title:true
			} ],
			cmTemplate:{
				sortable:false,
				title:false
			},
			page:1,
			sortname:"kprq",
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
			var id = $(this).parent().data("id");
			handle.view({id:id});
		});
		$(".grid-wrap").on("click", ".fa-edit", function(e){//编辑
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				handle.operate({id:id,oper:'edit'});
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
			THISPAGE.reloadData(model.data.$model)
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
				var i = "新增票据";
			else
				var i = "修改票据";
			$.dialog({
				title:i,
				content:"url:"+url+"/edit.html",
				data:opt,
				width:800,
				height:400,
				max:false,
				min:false,
				cache:false,
				lock:true
			})
		},
		//opt={id,pid,type,department_id}
		view:function(opt){
				var i = "查看票据";
			$.dialog({
				title:i,
				content:"url:"+url+"/view.html",
				data:opt,
				width:800,
				height:400,
				max:true,
				min:false,
				cache:false,
				lock:true
			})
		},
		del:function(e,type){
			$.dialog.confirm("删除的票据将不能恢复，请确认是否删除？", function(){
				Public.ajaxPost(url+"/del.json", {
					id:e
				}, function(t){
					if (t && 200 == t.status){
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除票据失败！" + t.msg})
				})
			})
		},
		callback:function(e, t, i){
		}
	}
THISPAGE.init();