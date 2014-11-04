var url=rootPath+"/fa/payReceivOrder",gridQryUrl=url+"/dataGrid.json",
tab=parent.tab,status_=["<font color='red'>未结算</font>","<font color='green'>已结清</font>"],type=["付款","收款"],dataList=new Array();
var model = avalon.define({$id:'view',
    data :{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,type:'',status:''},
    status_:status_,
    type_:type
});
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
			dataList[row.id]=row;
			var html_con = '<div class="operating" data-id="' + row.id + '"><span class="fa fa-eye mrb" title="查看"></span>';
			if(row.status==0){
				html_con+='<span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb red" title="删除"></span>';
			}
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
				name:"head_name",sortable:true,
				label:"负责人",
				align:"center",
				title:true
			}, {
				name:"billsn",sortable:true,
				label:"单号",
				align:"center",
				title:true
			}, {
				name:"type",
				label:"类型",sortable:true,
				align:"center",formatter:function(t,e,r){return type[t]},
				title:true
			}, {
				name:"status",sortable:true,
				label:"结算状态",
				align:"center",formatter:function(t,e,r){return status_[t]},
				title:true
			}, {
				name:"customer_name",
				label:"来往单位",formatter:function(v,e,r){return v+(r.csttype!=0?"【客户】":"【供应商】");},
				align:"center",sortable:true,
				title:true
			}, {
				name:"amt",
				label:"金额",sortable:true,
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"pay_datetime",
				label:"付款时间",sortable:true,
				align:"center",
				title:true
			}, {
				name:"creater_name",
				label:"创建人",sortable:true,
				align:"center",
				title:true
			}, {
				name:"create_datetime",
				label:"创建时间",sortable:true,
				align:"center",
				title:true
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
		dataList=new Array();
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
		$(".grid-wrap").on("click", ".fa-trash-o", function(e){//删除
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				handle.del(id);
			}
		});
		$("#add").click(function(t) {
			t.preventDefault();
			if(Business.verifyRight("TF_ADD")){
				handle.operate({oper:'add'});
			}
		});
		$("#search").click(function() {
			THISPAGE.reloadData();
		});
		$(window).resize(function(){
			Public.resizeGrid()
		})
		Public.pageTab();
	}
};
var handle = {
		//opt={id,pid,type,department_id}
		view:function(opt){
			$.dialog({title:"查看应收应付",content:"url:"+url+"/view.html",data:opt,width:800,height:400,max:true,min:false,cache:false,lock:true});
		},
		operate:function(opt){
			if ("add" == opt.oper)
				var i = "新增收款付款单";
			else
				var i = "修改收款付款单";
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
		del:function(e){
			$.dialog.confirm("删除的收款付款单将不能恢复，请确认是否删除？", function(){
				Public.ajaxPost(url+"/del.json", {
					id:e
				}, function(t){
					if (t && 200 == t.status){
						parent.Public.tips({type:2,content:t.msg});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({type:1,content:"删除收款付款单失败！" + t.msg})
				})
			})
		},
		callback:function(e, t, i){
		}
};
THISPAGE.init();