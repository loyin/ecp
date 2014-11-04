var SYSTEM = {
	date:"",
	rights :{},//权限对象 如:{A1_01:true,A1_02:true}
	user:{},
	company:{},
	permission:'',//权限字符串
	custParame:[],//格式如{list:[{id:{id值:"",name:"}}],typeList:{0:[]}}
	area:{},
	areaZone:["","华东","华南","华中","华北","西北","西南","东北","港澳台","海外"],
	custRating:{}
},tab;
function setTabHeight() {
	var e = $(window).height(), t = $("#main-bd"), i = e - t.offset().top;
	t.height(i)
}
function initDate() {
	var e = new Date, t = e.getFullYear(), i = ("0" + (e.getMonth() + 1))
			.slice(-2), a = ("0" + e.getDate()).slice(-2);
	SYSTEM.beginDate = t + "-" + i + "-01";
	SYSTEM.endDate = t + "-" + i + "-" + a
}
function setCurrentNav(e) {
	if (e) {
		var t = e.match(/([a-zA-Z]+)[-]?/)[1];
		$("#nav > li").removeClass("current");
		$("#nav > li.item-" + t).addClass("current")
	}
}
var model=avalon.define({
	$id:"ctrl",menuHtml:rootPath+'/menu',
	messageCount:0,
	showMenu:function(code){
		return SYSTEM.rights[code]==true;
	},
	init:function(){
	/** 获取登录信息 及权限 */
	Public.ajaxPost(rootPath+"/sso/user/loginInfo.json",
					{},
					function(json) {
						if (json.success == false) {
							//提示信息
							return;
						}
						SYSTEM = json.data;
						SYSTEM.beginDate = json.data.beginDate;
						SYSTEM.endDate = json.data.endDate;
						SYSTEM.date=json.data.date;
						SYSTEM.rights=json.data.rights;
						SYSTEM.permission=json.data.permission;
						$("#companyName").text(SYSTEM.company.name);
						$("#user_realName").html("欢迎," + SYSTEM.user.realname);
						if (SYSTEM.isexpired) {
							var button = [
									{
										name:"立即续费",
										focus:true,
										callback:function() {
											window.open("");
										}
									}, {
										name:"下次再说"
									} ], tipsContent = [
									'<div class="ui-dialog-tips">',
									"<p>谢谢您使用本商品，您的当前服务已经到期，到期3个月后数据将被自动清除，如需继续使用请购买/续费！</p>",
									'<p style="color:#AAA; font-size:12px;">(续费后请刷新页面或重新登录。)</p>',
									"</div>" ].join("");
							$.dialog({
								width:400,
								min:false,
								max:false,
								title:"系统提示",
								fixed:true,
								lock:true,
								button:button,
								resize:false,
								content:tipsContent
							});
							return;
						}
						/** 获取自定义参数 */
						Public.ajaxPost(rootPath+"/sso/parame/list.json",{},function(json) {SYSTEM.custParame=json;});
						/**地区参数*/
						Public.ajaxPost(rootPath+"/sso/parame/areaList.json",{},function(json) {
							SYSTEM.area=json;
						});
						/**客户等级参数*/
						Public.ajaxPost(rootPath+"/crm/custRating/list.json",{},function(json) {
							SYSTEM.custRating=json.data;
						});
					});
	setTabHeight();
	$(window).bind("resize", function() {
		setTabHeight()
	});
	$("#page-tab").ligerTab({
		height:"100%",
		changeHeightOnResize:true,
		onBeforeAddTabItem:function(e) {
			setCurrentNav(e)
		},
		onAfterAddTabItem:function() {
		},
		onAfterSelectTabItem:function(e) {
			setCurrentNav(e)
		},
		onBeforeRemoveTabItem:function() {
		},
		onAfterLeaveTabItem:function(e) {
		}
	});
	tab = $("#page-tab").ligerGetTabManager();
	tab.addTabItem({
		tabid:"index",
		text:"首页",
		url:rootPath+"/main",
		showClose:false
	});
	$(".service-tab").click(function() {
		var e = $(this).data("tab");
		tab.addTabItem({
			tabid:"myService",
			text:"在线服务",
			url:"/service/service.jsp",
			callback:function() {
			}
		})
	});
	$("#user_realName").click(function() {
		var e = $(this).data("tab");
		tab.addTabItem({
			tabid:"setting-person",
			text:"个人信息",
			url:rootPath+"/sso/user/person",
			callback:function() {
			}
		})
	});
	$("#msg").click(function() {
		var e = $(this).data("tab");
		tab.addTabItem({
			tabid:"msg",
			text:"消息",
			url:"/sso/message",
			callback:function() {
			}
		})
	});
	if ($.cookie("ReloadTips")) {
		Public.tips({
			content:$.cookie("ReloadTips")
		});
		$.cookie("ReloadTips", null)
	}
	$("#logoutBtn").click(function(){
		$.dialog.confirm("确定要退出吗？未保存的数据将会丢失，请先保存再点击退出！", function(){
			window.location=rootPath+"/logout";
		});
	});
	},
	callback:function(){
		!function(e) {
			var t = e("#nav"), i = e("#nav > li"),j=0,length=i.length;
			e.each(i, function() {
				var i = e(this).find(".sub-nav-wrap");
				e(this).on("mouseover", function() {
					t.removeClass("static");
					e(this).addClass("on");
					i.stop(true, true).fadeIn(150)
				}).on("mouseleave", function() {
					t.addClass("static");
					e(this).removeClass("on");
					i.stop(true, true).hide()
				});
				if(j==0)
				i.addClass("group-nav-t0");
				if(j==i.length-1)
				i.addClass("group-nav");
				j++;
			});
			e(".sub-nav-wrap a").bind("click", function() {
				e(this).parents(".sub-nav-wrap").hide()
			})
		}(jQuery);
		/**控制模块显示*/
		/**重新设置二级菜单显示*/
		var subnavList=$(".item:visible>.sub-nav-wrap");
		for(var i=1;i<subnavList.length;i++){
			var $nav=$(subnavList[i]);
			$nav.css("top",0-($nav.height()/2-40));
		}
		$(".group-nav-t0").css("top",0);
		if(subnavList.length>7){
			var $nav=$(subnavList[subnavList.length-1]);
			//$nav.css("top",0-$nav.height()+45);
			$nav.css("top","").css("bottom",0);
		}
		$("#nav").on(
				"click",
				"a[rel=pageTab]",
				function(e) {
					e.preventDefault();
					var t = $(this).data("right");
					if (t && !Business.verifyRight(t))
						return false;
					var i = $(this).attr("tabid"), a = rootPath+$(this).attr("href"), r = $(
							this).attr("showClose"), n = $(this).attr("tabTxt")
							|| $(this).text().replace(">", ""), o = $(this).attr(
							"parentOpen");
					o ? parent.tab.addTabItem({tabid:i,text:n,url:a,showClose:r}):
						tab.addTabItem({tabid:i,text:n,url:a,showClose:r});
					return false
				});
	}
});
model.init();