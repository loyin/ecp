var queryConditions = {},url=rootPath+"/fa/account",api = frameElement.api,gridQryUrl=url+"/detailDg.json?account_id="+api.data.account_id;

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
				name:"create_datetime",
				label:"交易时间",
				align:"center",
				title:true
			}, {
				name:"amt_in",
				label:"转入金额",
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"amt_out",
				label:"转出金额",
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"balance",
				label:"余额",
				align:"right",formatter:function(v,e,r){return parent.Public.numToCurrency(v);},
				title:true
			}, {
				name:"creater_name",
				label:"交易人",
				align:"center",
				title:true
			}, {
				name:"remark",
				label:"交易备注",
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
		$(window).resize(function(){
			Public.resizeGrid()
		})
	}
};
THISPAGE.init();