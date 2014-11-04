var queryConditions = {
	keyword : "",reg_date$le:'',reg_date$ge:'',position_id:'',
	qryField:'reg_date$le,reg_date$ge,position_id'
}, url=rootPath+"/sso/user",gridQryUrl=url+"/dataGrid.json",
 THISPAGE = {
	init : function() {
		this.initDom();
		this.loadGrid();
		this.addEvent()
	},
	initDom : function() {
		this.$_keyword = $("#keyword");
		this.$_beginDate = $("#beginDate").val('');//SYSTEM.beginDate);
		this.$_endDate = $("#endDate").val(SYSTEM.endDate);
		this.$_keyword.placeholder();
		$(".ui-datepicker-input").datepicker();
	},
viewdUser:function(id){
	$.dialog({
		id : "moreCon",
		width : 850,
		height : 400,
		min : true,
		max : true,
		title : "查看用户",
		button : [{name : "关闭"	} ],
		resize : true,lock:true,
		content : "url:"+url+"/view",
		data : {id:id}
	});
},
	loadGrid : function() {
		function t(t, e, i) {
			var a = '<div class="operating" data-id="'+ i.id	+ '"><span class="fa fa-eye mrb" title="查看"></span><span class="ui-icon ui-icon-pencil" title="修改"></span><span class="ui-icon fa-trash-o" title="删除"></span></div>';
			return a
		}
		function e(t) {
			var e = t.join('<p class="line" />');
			return e
		}
		var i = Public.setGrid();
		queryConditions.reg_date$ge = this.$_beginDate.val();
		queryConditions.reg_date$le= this.$_endDate.val();
		$("#grid").jqGrid({
			url : gridQryUrl,
			postData : queryConditions,
			datatype : "json",
			mtype:'POST',
			autowidth : true,
			height : i.h,
			altRows : true,
			gridview : true,rownumbers:true,
			multiselect : true,
			multiboxonly : true,
			colModel : [ {
				name : "operating",
				label : "操作",
				fixed : true,width:100,
				formatter : Public.operFmatter,
				align : "center",
				title : false
			}, {
				name : "head_pic",
				label : "头像",
				align : "center",
				width:35,
				formatter:function(t,e,i){return (i.head_pic==null||i.head_pic==''||i.head_pic=='null')?"":"<img width='30px' src='"+(i.head_pic)+"'>";},
				title : true
			}, {
				name : "uname",
				label : "用户帐号",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "realname",
				label : "姓名",
				align : "center",width:100,sortable:true,
				title : true
			}, {
				name : "sex",
				label : "性别",
				align : "center",
				width:30,
				formatter:function(t,e,i){var status=['女','男']; return status[i.sex];},
				title : true
			}, {
				name : "department_name",
				label : "部门",
				align : "center",
				width:100,sortable:true,
				title : false
			}, {
				name : "position_name",
				label : "岗位",
				align : "center",width:50,sortable:true,
				title : false
			}, {
				name : "mobile",
				label : "手机",
				align : "center",
				title : false
			}, {
				name : "email",
				label : "Email",
				align : "center",
				title : false
			}, {
				name : "status",
				label : "状态",
				align : "center",width:40,sortable:true,
				formatter:function(t,e,i){var status=['<font color="red">禁用</font>','<font color="green">激活</font>']; return "<a class='disable_user_btn' data-id='"+i.id+"'>"+status[i.status]+"</a>";},
				title : true
			}, {
				name : "reg_date",
				label : "注册日期",
				align : "center",width:100,sortable:true,
				title : false
			}, {
				name : "last_login_time",
				label : "上次登录时间",
				align : "center",width:140,sortable:true,
				title : false
			}, {
				name : "login_ip",
				label : "上次登录IP",
				align : "center",width:100,sortable:true,
				title : false
			} ],
			cmTemplate : {
				sortable : false,
				title : false
			},
			page : 1,
			sortname : "uname",
			sortorder : "desc",
			pager : "#page",
			rowNum : 50,
			rowList : [ 50,100, 200 ],
			viewrecords : true,
			shrinkToFit : false,
			forceFit : false,
			jsonReader : {
				root : "data.list",
				records : "data.totalRow",
				repeatitems : false,
				id : "id"
			},
			loadError : function() {
				parent.Public.tips({
					type : 1,
					content :"加载数据异常！"
				})
			},
			ondblClickRow : function(t) {
				$("#" + t).find(".fa-eye").trigger("click")
			}
		})
	},
	reloadData : function(t) {
		$("#grid").jqGrid("setGridParam", {
			url : gridQryUrl,
			datatype : "json",mtype:'POST',
			postData : t
		}).trigger("reloadGrid");
	},
	addEvent : function() {
		Public.dateCheck();
		var t = this;
		$(".grid-wrap").on("click", ".fa-eye", function(t) {
			t.preventDefault();
			var e = $(this).parent().data("id");
			THISPAGE.viewdUser(e);
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
		$(".grid-wrap").on("click", ".disable_user_btn", function(t) {
			t.preventDefault();
			if (Business.verifyRight("BU_DELETE")) {
				var e = $(this).data("id");
				handle.disable(e);
			}
		});
		$("#search").click(
				function() {
					queryConditions.keyword =t.$_keyword.val();
					queryConditions.reg_date$ge = t.$_beginDate.val();
					queryConditions.reg_date$le = t.$_endDate.val();
					THISPAGE.reloadData(queryConditions)
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
				t.length ? handle.del(t.join()) : parent.Public.tips({
					type : 2,
					content : "请选择需要删除的项"
				})
			}
		});
		/**岗位combo START*/
		var r = $("#position");
		var i=Public.comboTree(r,{width:260,url:rootPath+'/sso/position/tree.json',postData:{},
				callback : {
					beforeClick : function(e, t) {
						if(t.type==1){
							r.val(t.name);r.data("pid", t.id);
							queryConditions.position_id=t.id;
						}else{
							r.val("");r.data("pid","");
							queryConditions.position_id="";
						}
						i.hide();
					}
				}
			});
		/**岗位combo END*/
		$(window).resize(function() {
			Public.resizeGrid()
		})
	}
};
var handle = {
		operate : function(e, t) {
			if ("add" == e)
				var i = "新增用户", r = {oper : e,callback : this.callback};
			else
				var i = "修改用户", r = {oper : e,rowId : t,callback : this.callback};
			$.dialog({
				title : i,
				content : "url:"+url+"/edit",
				data : r,
				width : 850,
				height : 422,
				max :true,resize:true,
				min :false,
				cache :false,
				lock :true
			})
		},
		del : function(id) {
			$.dialog.confirm("删除的用户将不能恢复，请确认是否删除？", function() {
				Public.ajaxPost(url+"/del.json", {
					id : id
				}, function(t) {
					if (t && 200 == t.status) {
						var i = t.data || [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "删除用户失败！" + t.msg
						})
				})
			})
		},
		disable : function(id) {
			$.dialog.confirm("确定要（禁用或激活）用户？", function() {
				Public.ajaxPost(url+"/disable", {id : id},
				function(t) {
					if (t && 200 == t.status) {
						var i = t.data|| [];
						parent.Public.tips({
							type : 2,
							content : t.msg
						});
						THISPAGE.reloadData();
					} else
						parent.Public.tips({
							type : 1,
							content : "操作失败！" + t.msg
						})
				})
			})
		}
	}
THISPAGE.init();