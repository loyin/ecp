var Public = Public || {};
var Business = Business || {};
Public.isIE6 = !window.XMLHttpRequest;	//ie6
/*$(function(){
	//菜单按钮
	$('.ui-btn-menu .menu-btn').on('mouseenter.menuEvent',function(e){
		if($(this).hasClass("ui-btn-dis")) {
			return false;
		}
		$(this).parent().addClass('ui-btn-menu-cur');
		$(this).blur();
		e.preventDefault();
	});
	$(document).on('click.menu',function(e){
		var target  = e.target || e.srcElement;
		$('.ui-btn-menu').each(function(){
			var menu = $(this);
			if($(target).closest(menu).length == 0 && $('.con',menu).is(':visible')){
				 menu.removeClass('ui-btn-menu-cur');
			}
		})
	});
});*/
//设置表格宽高
Public.setGrid = function(adjustH, adjustW){
	var adjustH = adjustH || 65;
	var adjustW = adjustW || 20;
	var gridW = $(window).width() - adjustW, gridH = $(window).height() - $(".grid-wrap").offset().top - adjustH;
	return {
		w : gridW,
		h : gridH
	}
};
//重设表格宽高
Public.resizeGrid = function(adjustH, adjustW){
	var grid = $("#grid");
	var gridWH = Public.setGrid(adjustH, adjustW);
	grid.jqGrid('setGridHeight', gridWH.h);
	grid.jqGrid('setGridWidth', gridWH.w);
};
//自定义报表宽高初始化以及自适应
Public.initCustomGrid = function(tableObj){
	//去除报表原始定义的宽度
	$(tableObj).css("width") && $(tableObj).attr("width","auto");
	//获取报表宽度当做最小宽度
	var _minWidth = $(tableObj).outerWidth();
	$(tableObj).css("min-width",_minWidth+"px");
	//获取当前window对象的宽度作为报表原始的宽度
	$(tableObj).width($(window).width() - 74);
	$(tableObj).closest('.mod-report').height($(window).height() - 66);
	//设置resize事件
	var _throttle = function(method,context){
		clearTimeout(method.tid);
		method.tid = setTimeout(function(){
			method.call(context);
		},100)
	};
	var _resize = function(){
		$(tableObj).width($(window).width() - 74);
		$(tableObj).closest('.mod-report').height($(window).height() - 66);
	};
	$(window).resize(function() {
		_throttle(_resize);
	});
}
/**
 * 节点赋100%高度
 *
 * @param {object} obj 赋高的对象
*/
Public.setAutoHeight = function(obj){
if(!obj || obj.length < 1){
	return ;
}

Public._setAutoHeight(obj);
$(window).bind('resize', function(){
	Public._setAutoHeight(obj);
});

}

Public._setAutoHeight = function(obj){
obj = $(obj);
//parent = parent || window;
var winH = $(window).height();
var h = winH - obj.offset().top - (obj.outerHeight() - obj.height());
obj.height(h);
}
//操作项格式化，适用于有“修改、删除”操作的表格
Public.operFmatter = function (val, opt, row) {
	var html_con = '<div class="operating" data-id="' + row.id + '"><span class="fa fa-eye mrb" title="查看"></span><span class="fa fa-edit mrb" title="修改"></span><span class="fa fa-trash-o mrb" title="删除"></span></div>';
	return html_con;
};

Public.dateCheck = function(){
	$('.ui-datepicker-input').bind('focus', function(e){
		$(this).data('original', $(this).val());
	}).bind('blur', function(e){
		var reg = /((^((1[8-9]\d{2})|([2-9]\d{3}))(-)(10|12|0?[13578])(-)(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(11|0?[469])(-)(30|[12][0-9]|0?[1-9])$)|(^((1[8-9]\d{2})|([2-9]\d{3}))(-)(0?2)(-)(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)(-)(0?2)(-)(29)$)|(^([3579][26]00)(-)(0?2)(-)(29)$)|(^([1][89][0][48])(-)(0?2)(-)(29)$)|(^([2-9][0-9][0][48])(-)(0?2)(-)(29)$)|(^([1][89][2468][048])(-)(0?2)(-)(29)$)|(^([2-9][0-9][2468][048])(-)(0?2)(-)(29)$)|(^([1][89][13579][26])(-)(0?2)(-)(29)$)|(^([2-9][0-9][13579][26])(-)(0?2)(-)(29)$))/;
		var _self = $(this);
		setTimeout(function(){
			if(!reg.test(_self.val())) {
				parent.Public.tips({type:1, content : '日期格式有误！如：2013-08-08。'});
				_self.val(_self.data('original'));
			};
		}, 10)

	});
}
/*获取URL参数值*/
Public.getRequest = Public.urlParam = function() {
   var param, url = location.search, theRequest = {};
   if (url.indexOf("?") != -1) {
      var str = url.substr(1);
      strs = str.split("&");
      for(var i = 0, len = strs.length; i < len; i ++) {
		 param = strs[i].split("=");
         theRequest[param[0]]=decodeURIComponent(param[1]);
      }
   }
   return theRequest;
};
/*
  通用post请求，返回json
  url:请求地址， params：传递的参数{...}， callback：请求成功回调
*/ 
Public.ajaxPost = function(url, params, callback, errCallback){
	$.ajax({  
	   type: "POST",
	   url: url,
	   data: params, 
	   dataType: "json",  
	   success: function(data, status){  
		   callback(data);  
	   },  
	   error: function(err){  
			parent.Public.tips({type: 1, content : '操作失败了哦，请检查您的网络链接！'});
			errCallback && errCallback(err);
	   } 
	});  
};  
Public.ajaxGet = function(url, params, callback, errCallback){    
	$.ajax({  
	   type: "GET",
	   url: url,
	   dataType: "json",  
	   data: params,    
	   success: function(data, status){  
		   callback(data);  
	   },   
	   error: function(err){  
			parent.Public.tips({type: 1, content : '操作失败了哦，请检查您的网络链接！'});
			errCallback && errCallback(err);
	   }  
	});  
};
/*操作提示*/
Public.tips = function(options){ return new Public.Tips(options); }
Public.Tips = function(options){
	var defaults = {
		renderTo: 'body',
		type : 0,
		autoClose : true,
		removeOthers : true,
		time : undefined,
		top : 10,
		onClose : null,
		onShow : null
	}
	this.options = $.extend({},defaults,options);
	this._init();
	!Public.Tips._collection ?  Public.Tips._collection = [this] : Public.Tips._collection.push(this);
}

Public.Tips.removeAll = function(){
	try {
		for(var i=Public.Tips._collection.length-1; i>=0; i--){
			Public.Tips._collection[i].remove();
		}
	}catch(e){}
}

Public.Tips.prototype = {
	_init : function(){
		var self = this,opts = this.options,time;
		if(opts.removeOthers){
			Public.Tips.removeAll();
		}

		this._create();

		if(opts.autoClose){
			time = opts.time || opts.type == 1 ? 5000 : 3000;
			window.setTimeout(function(){
				self.remove();
			},time);
		}

	},
	
	_create : function(){
		var opts = this.options, self = this;
		if(opts.autoClose) {
			this.obj = $('<div class="ui-tips"><i></i></div>').append(opts.content);
		} else {
			this.obj = $('<div class="ui-tips"><i></i><span class="close"></span></div>').append(opts.content);
			this.closeBtn = this.obj.find('.close');
			this.closeBtn.bind('click',function(){
				self.remove();
			});
		};
		
		switch(opts.type){
			case 0 : 
				this.obj.addClass('ui-tips-success');
				break ;
			case 1 : 
				this.obj.addClass('ui-tips-error');
				break ;
			case 2 : 
				this.obj.addClass('ui-tips-warning');
				break ;
			default :
				this.obj.addClass('ui-tips-success');
				break ;
		}
		
		this.obj.appendTo('body').hide();
		this._setPos();
		if(opts.onShow){
				opts.onShow();
		}

	},
	_setPos : function(){
		var self = this, opts = this.options;
		if(opts.width){
			this.obj.css('width',opts.width);
		}
		var h =  this.obj.outerHeight(),winH = $(window).height(),scrollTop = $(window).scrollTop();
		//var top = parseInt(opts.top) ? (parseInt(opts.top) + scrollTop) : (winH > h ? scrollTop+(winH - h)/2 : scrollTop);
		var top = parseInt(opts.top) + scrollTop;
		this.obj.css({position : Public.isIE6 ? 'absolute' : 'fixed',left : '50%',top : top,zIndex : '9999',marginLeft : -self.obj.outerWidth()/2});
		window.setTimeout(function(){
			self.obj.show().css({marginLeft : -self.obj.outerWidth()/2});
		},150);

		if(Public.isIE6){
			$(window).bind('resize scroll',function(){
				var top = $(window).scrollTop() + parseInt(opts.top);
				self.obj.css('top',top);
			})
		}
	},

	remove : function(){
		var opts = this.options;
		this.obj.fadeOut(200,function(){
			$(this).remove();
			if(opts.onClose){
				opts.onClose();
			}
		});
	}
};
//数值显示格式转化
Public.numToCurrency = function(val, dec) {
	val = parseFloat(val);	
	dec = dec || 2;	//小数位
	if(isNaN(val)){
		return '0.00';
	}
	val = val.toFixed(dec).split('.');
	var reg = /(\d{1,3})(?=(\d{3})+(?:$|\D))/g;
	return val[0].replace(reg, "$1,") + '.' + val[1];
};
//数值显示
Public.currencyToNum = function(val){
	var val = String(val);
	if ($.trim(val) == '') {
		return 0;
	}
	val = val.replace(/,/g, '');
	val = parseFloat(val);
	return isNaN(val) ? 0 : val;
};
/*批量绑定页签打开*/
Public.pageTab = function() {
	$(document).on('click', '[rel=pageTab]', function(e){
		e.preventDefault();
		var right = $(this).data('right');
		if (right && !Business.verifyRight(right)) {
			return false;
		};
		var tabid = $(this).attr('tabid'), url = $(this).attr('href'), showClose = $(this).attr('showClose'), text = $(this).attr('tabTxt') || $(this).text(),parentOpen = $(this).attr('parentOpen');
		if(parentOpen){
			parent.tab.addTabItem({tabid: tabid, text: text, url: url, showClose: showClose});
		} else {
			parent.tab.addTabItem({tabid: tabid, text: text, url: url, showClose: showClose});
		}
	});
};

$.fn.artTab = function(options) {
  var defaults = {};
  var opts = $.extend({}, defaults, options);
  var callback = opts.callback || function () {};
  this.each(function(){
	  var $tab_a =$("dt>a",this);
	  var $this = $(this);
	  $tab_a.bind("click", function(){
		  var target = $(this);
		  target.siblings().removeClass("cur").end().addClass("cur");
		  var index = $tab_a.index(this);
		  var showContent = $("dd>div", $this).eq(index);
		  showContent.siblings().hide().end().show();
		  callback(target, showContent, opts);
	  });
	  if(opts.tab)
		  $tab_a.eq(opts.tab).trigger("click");
	  if(location.hash) {
		  var tabs = location.hash.substr(1);
		  $tab_a.eq(tabs).trigger("click");
	  }
  });	  
};
//input占位符
$.fn.placeholder = function(){
	this.each(function() {
		$(this).focus(function(){
			if($.trim(this.value) == this.defaultValue){
				this.value = '';
			}
			$(this).removeClass('ui-input-ph');
		}).blur(function(){
			var val = $.trim(this.value);
			if(val == '' || val == this.defaultValue){
				$(this).addClass('ui-input-ph');
			}
			val == '' && $(this).val(this.defaultValue);
		});
	});
};

Public.getDefaultPage = function(){
	var win = window.self;
	do{
		if (win.CONFIG) {
			return win;
		}
		win = win.parent;
	} while(true);
};

//权限验证
Business.verifyRight = function(right){
	var system = Public.getDefaultPage().SYSTEM;
	var isAdmin = system.user.isAdmin;
	var siExperied = system.isexpired;
	var rights = system.rights;
	if (isAdmin && !siExperied) {
		return true;
	};

	if(siExperied) {
			var html = [
				'<div class="ui-dialog-tips">',
				'<p>谢谢您使用本产品，您的当前服务已经到期，到期3个月后数据将被自动清除，如需继续使用请购买/续费！</p>',
				'<p style="color:#AAA; font-size:12px;">(续费后请刷新页面或重新登录。)</p>',
				'</div>'
			].join('');
			$.dialog({width: 280,title: '系统提示',icon: 'alert.gif',fixed: true,lock: true,	resize: false,ok: true,content: html});
			return false;
	} else {
		return true;//权限判断
		if (rights.indexOf(right)>0){
			return true;
		} else {
			var html = [
				'<div class="ui-dialog-tips">',
				'<h4 class="tit">您没有该功能的使用权限哦！</h4>',
				'<p>请联系管理员为您授权！</p>',
				'</div>'
			].join('');
			$.dialog({width: 240,title: '系统提示',icon: 'alert.gif',fixed: true,lock: true,resize: false,ok: true,ontent: html});
			return false;
		}
	};
};
/**用户下拉框*/
Public.userCombo = function($_obj, opts,type){
	if ($_obj.length == 0) { return };
	var opts = $.extend(true, {
		data: function(){
			return rootPath+'/sso/user/list.json'+(type?"?type="+type:"");
		},
		ajaxOptions: {
			formatData: function(data){
				return data.data.list;
			}	
		},
		formatText: function(data){
			return data.userno + ' ' + data.real_name;
		},
		value: 'id',
		defaultSelected: -1,
		editable: true,
//		extraListHtml: '<a href="javascript:void(0);" id="quickAddGoods" class="quick-add-link"><i class="ui-icon-add"></i>新增商品</a>',
		maxListWidth: 500,cache: false,forceSelection: true,maxFilter: 10,trigger: false,listHeight: 182,
		listWrapCls: 'ui-droplist-wrap',
		callback: {
			onChange: function(data){
				alert(data);
			},
			onListClick: function(){
			}
		},
		queryDelay: 0,inputCls: 'edit_subject',wrapCls: 'edit_subject_wrap',focusCls: '',disabledCls: '',activeCls: ''
	}, opts);
	return $_obj.combo(opts).getCombo();
};
/** 
 1. 设置cookie的值，把name变量的值设为value   
example $.cookie(’name’, ‘value’);
 2.新建一个cookie 包括有效期 路径 域名等
example $.cookie(’name’, ‘value’, {expires: 7, path: ‘/’, domain: ‘jquery.com’, secure: true});
3.新建cookie
example $.cookie(’name’, ‘value’);
4.删除一个cookie
example $.cookie(’name’, null);
5.取一个cookie(name)值给myvar
var account= $.cookie('name');
**/
$.cookie = function(name, value, options) {
    if (typeof value != 'undefined') { // name and value given, set cookie
        options = options || {};
        if (value === null) {
            value = '';
            options.expires = -1;
        }
        var expires = '';
        if (options.expires && (typeof options.expires == 'number' || options.expires.toUTCString)) {
            var date;
            if (typeof options.expires == 'number') {
                date = new Date();
                date.setTime(date.getTime() + (options.expires * 24 * 60 * 60 * 1000));
            } else {
                date = options.expires;
            }
            expires = '; expires=' + date.toUTCString(); // use expires attribute, max-age is not supported by IE
        }
        var path = options.path ? '; path=' + options.path : '';
        var domain = options.domain ? '; domain=' + options.domain : '';
        var secure = options.secure ? '; secure' : '';
        document.cookie = [name, '=', encodeURIComponent(value), expires, path, domain, secure].join('');
    } else { // only name given, get cookie
        var cookieValue = null;
        if (document.cookie && document.cookie != '') {
            var cookies = document.cookie.split(';');
            for (var i = 0; i < cookies.length; i++) {
                var cookie = jQuery.trim(cookies[i]);
                // Does this cookie string begin with the name we want?
                if (cookie.substring(0, name.length + 1) == (name + '=')) {
                    cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                    break;
                }
            }
        }
        return cookieValue;
    }
};
//生成树
Public.zTree = {
    zTree: {},
    opts:{showRoot:true,defaultClass:'',disExpandAll:false,//showRoot为true时无效
    	callback:'',rootTxt:'全部'},
    setting: {
        view: {dblClickExpand: false,showLine: true,selectedMulti: false},
        data: {
            simpleData: {enable: true,idKey: "id",pIdKey: "pid",rootPId: ""}
        },
        callback: {
            //beforeClick: function(treeId, treeNode) {}
        }
    },
    _getTemplate: function(opts) {
    	this.id = 'tree'+parseInt(Math.random()*10000);
        var _defaultClass = "ztree";
        if (opts) {
            if(opts.defaultClass){
                _defaultClass += ' ' + opts.defaultClass;
            }
        }
        return '<ul id="'+this.id+'" class="' + _defaultClass + '"></ul>';
    },
    init: function($target, opts, setting ,callback) {
//    	init: function($target, opts, setting ,callback) {
        if ($target.length === 0) {
            return;
        }
        var self = this;
        self.opts = $.extend(true, self.opts, opts);
        self.container = $($target);
        self.obj = $(self._getTemplate(opts)); 
        self.container.append(self.obj);
        setting = $.extend(true, self.setting, setting);
        Public.ajaxPost(opts.url, opts.postData, function(data) {
            if (data.status === 200 && data.data) {
            	self._callback(data.data);
            } else {
            	Public.tips({type: 2,content: "加载分类信息失败！"});
            }
        });
        return self;
    },
    _callback: function(data){
    	var self = this;
    	var callback = self.opts.callback;
    	if(!data.length) return;
    	self.zTree = $.fn.zTree.init(self.obj, self.setting, data);
    	self.zTree.expandAll(!self.opts.disExpandAll);
    	if(callback && typeof callback === 'function'){
    		callback(self, data);
    	}
    }
};
/**
 * 下拉树
 * opts={url:"",postData:{},toVal:赋值对象,toValObj:赋值DOM,callback:{beforeClick:function(e,t)}}
 */
Public.comboTree=function($obj,opts){
	var i = $("<div class='comboDiv'/>");
	i.appendTo($obj.parent());
	i.hide();
	i.offtop=$obj.offtop+50;
	i.offleft=$obj.offleft;
	var offset = $obj.offset();
	var topDiff = $obj.outerHeight();
	var w =(opts.width?opts.width:$obj.width())+12;
	if(opts.offset){
		i.css({width:w,left:opts.offset.left});
	}else{
		i.css({width:w, top:offset.top + topDiff,left:offset.left});
	}
	$obj.click(function(){
	if (!i.show().data("hasInit")){
		i.show().data("hasInit",true);
		Public.zTree.init(i, {url:opts.url,postData:opts.postData}, {
			callback :opts.callback?opts.callback:{
				beforeClick : function(e, t) {
				$obj.val(t.name);$obj.data("pid", t.pid);$obj.data("val",t.id);
				if(opts.toVal){
					opts.toVal=t.id;
				}
				if(opts.toValObj){
					opts.toValObj.val(t.id);
				}
				i.hide();
				}
			}
		});
	}
	});
	return i;
}
/***
 * 下拉框
 */
Public.comboBox=function($obj,opts){
	var i = $("<div class='comboDiv'/>");
	
}
/*
 * 兼容IE8 数组对象不支持indexOf()
 * create by guoliang_zou ,20140812
 */
if(!Array.prototype.indexOf)
{
  Array.prototype.indexOf = function(elt /*, from*/)
  {
    var len = this.length >>> 0;
    var from = Number(arguments[1]) || 0;
    from = (from < 0)
         ? Math.ceil(from)
         : Math.floor(from);
    if (from < 0)
      from += len;
    for (; from < len; from++)
    {
      if (from in this &&
          this[from] === elt)
        return from;
    }
    return -1;
  };
}
Public.custParamefmter=function(v){
	var p=SYSTEM.custParame[v];
	if(p!=undefined){
		return p.name;
	}else{
		return "-";
	}
}
Public.custRatingfmter=function(v){
	var p=SYSTEM.custRating[v];
	if(p!=undefined){
		return p.name;
	}else{
		return "-";
	}
}
function chinaYuan(num) {
	var uper = "零壹贰叁肆伍陆柒捌玖".split("");
	var ext = "拾佰仟萬亿".split("");
//	num +=num+ "";
	var num1 =num.split(".")[1],newNum = "",newNum1="";
	if(new Number(num)<0){
		newNum="负";
		num=num.replace("-","");
	}
	if(num1!="00"){
		var num1List=num1.split("");
		newNum1+=uper[num1List[0]]+"角";
		newNum1+=uper[num1List[1]]+"分";
		newNum1 = newNum1.replace(/零角/ig, "");
		newNum1 = newNum1.replace(/零分/ig, "");
	}else{
		newNum1="整";
	}
	num =num.split(".")[0];
	for (var i = num.length; i > 0; i--) {
		if(num[num.length - i]=='-'){
			continue;
		}
		newNum += uper[num[num.length - i]];
		if (i == 2)
			newNum += "拾";
		if (i == 3)
			newNum += "佰";
		if (i == 4)
			newNum += "仟";
		if (i == 5)
			newNum += "萬";
		if (i == 6)
			newNum += "拾";
		if (i == 7)
			newNum += "佰";
		if (i == 8)
			newNum += "仟";
		if (i == 9)
			newNum += "亿";
		if (i == 10)
			newNum += "拾";
		if (i == 11)
			newNum += "佰";
		if (i == 12)
			newNum += "仟";
		if (i == 13)
			newNum += "兆";
	}
	newNum += "圆";
	newNum = newNum.replace(/零拾/ig, "零");
	newNum = newNum.replace(/零佰/ig, "零");
	newNum = newNum.replace(/零仟/ig, "零");
	newNum = newNum.replace(/零零零/ig, "零");
	newNum = newNum.replace(/零零/ig, "零");
	newNum = newNum.replace(/亿萬/ig, "萬");
	newNum = newNum.replace(/零萬/ig, "萬");
	newNum = newNum.replace(/零亿/ig, "亿");
	newNum = newNum.replace(/零兆/ig, "兆");
	newNum = newNum.replace(/亿萬/ig, "亿");
	newNum = newNum.replace(/兆亿/ig, "兆");
	return newNum+newNum1;
}
function fixedNum(val,dc){
	dc=dc||2;
	if(val==undefined)
		return"";
	if(isNaN(val))
		return val;
	else
	return val.toFixed(dc);
}
/** avalon filter*/
/**自定义参数*/
avalon.filters.custParame=function(v){
	var p=SYSTEM.custParame[v];
	if(p!=undefined){
		return p.name;
	}else{
		return "-";
	}
}
//自定义参数类型
avalon.filters.custParameType=function(v){
	return parent.parameType[v];
}
//地区省市
avalon.filters.area=function(v){
	var p=SYSTEM.area.list[v];
	if(p!=undefined){
		return p.name;
	}else{
		return "-";
	}
}
//金额格式化
avalon.filters.money=function(v){
	return Public.numToCurrency(v);
}
//金额转大写
avalon.filters.atoc=function(v){
	return (v!=undefined&&isNaN(v)==false)?chinaYuan(v):"";
}
/**权限码判断显示*/
function showByRights(code){
	return SYSTEM.rights[code]==true;
}