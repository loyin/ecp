var url=rootPath+"/fa/payReceivAbles",gridQryUrl=url+"/dataGrid.json",
tab=parent.tab,status_=["<font color='red'>未结算</font>","部分结算","<font color='green'>已结清</font>"],type=["付款","收款"],dataList=new Array();
var model = avalon.define({$id:'view',
    data :{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,type:'',status:''},
    status_:status_,
    type_:type
});
var  THISPAGE = {
	init:function(){
		this.loadGrid();
		this.addEvent()
		$(".ui-datepicker-input").datepicker();
	},
	loadGrid:function(){
		function t(t, e, row){
			dataList[row.id]=row;
			var html_con = '<div class="operating" data-id="' + row.id + '"><span class="fa fa-eye mrb" title="查看"></span>';
			if(row.status==0){
			//	html_con+='<span class="fa fa-trash-o mrb" title="删除"></span>';
			}
			if(row.status>0){
				html_con+='<span class="fa fa-list mrb" title="查看支付明细"></span>';
			}
			if(row.status!=2){
				html_con+='<a href="javasript:void()" class="pay">'+type[row.type]+'</a>';
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
				name:"head_name",sortable:true,
				label:"负责人",
				align:"center",
				title:true
			}, {
				name:"billsn",sortable:true,
				label:"合同订单号",
				align:"center",
				title:true
			}, {
				name:"customer_name",
				label:"来往单位",formatter:function(v,e,r){return v+(r.csttype!=0?"【客户】":"【供应商】");},
				align:"center",sortable:true,
				title:true
			}, {
				name:"name",
				label:"主题",
				align:"center",
				title:true
			}, {
				name:"amt",
				label:"订单金额",sortable:true,
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"pay_amt",
				label:"已结金额",sortable:true,
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"sfsd",
				label:"未结金额",sortable:true,
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(r.amt-r.pay_amt);},
				title:true
			}, {
				name:"pay_datetime",
				label:"最近付款时间",sortable:true,
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
		$(".grid-wrap").on("click", ".pay", function(e){//支付
			e.preventDefault();
			if (Business.verifyRight("EDIT")){
				var id = $(this).parent().data("id");
				handle.pay(dataList[id]);
			}
		});
		$(".grid-wrap").on("click", ".fa-list", function(t){//查看明细
			t.preventDefault();
			var e = $(this).parent().data("id");
			var type = $(this).parent().data("type");
			handle.detail({id:e,type:type});
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
		detail:function(opt){
			var i = "查看支付明细";
			$.dialog({
				title:i,
				content:"url:"+rootPath+"/fa/payReceivOrder/detail.html",
				data:opt,
				width:800,
				height:800,
				max:true,
				min:false,
				cache:false,
				lock:true
			})
		},
	pay:function(row){
	$.dialog({title:type[row.type],content:"url:"+rootPath+"/fa/payReceivOrder/edit.html",data:{order:row},
		width:800,height:400,max:true,min:false,cache:false,lock:true});
	}
};
THISPAGE.init();