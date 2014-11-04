var queryConditions = {},url=rootPath+"/fa/payReceivOrder",api = frameElement.api,gridQryUrl=url+"/list.json?id="+api.data.id
,status_=["<font color='red'>未结算</font>","<font color='green'>已结清</font>"],type=["付款","收款"];
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
			var html_con = '<div class="operating" data-id="' + row.id + '" data-typ="'+row.yh+'" data-name="'+row.textname+'"><a class="fa fa-eye mrb" title="查看">查看</a><a class="fa fa-list mrb" title="查看明细">明细</a><a class="fa fa-edit mrb" title="修改">修改</a><a class="fa fa-trash-o mrb" title="删除">删除</a>';
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
				name:"billsn",sortable:true,
				label:"支付单号",
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
			sortname:"",
			sortorder:"",
			pager:"#page",
			rowNum:50,
			rowList:[ 50,100, 200 ],
			viewrecords:true,
			shrinkToFit:false,
			forceFit:false,
			jsonReader:{
				root:"data",
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
		$(window).resize(function(){
			Public.resizeGrid()
		})
	}
};
var handle = {
		view:function(opt){
				var i = "查看支付详情";
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
		}
	}
THISPAGE.init();