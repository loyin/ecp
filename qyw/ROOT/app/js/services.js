/**
 * 返回顶部
 */
var Back2top = (function($,undefined){
    var _timeInterval = 300;
    var browserInfo = (function(){
        var userAgent = navigator.userAgent.toLowerCase();
        return {
            version: (userAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [])[1],
            msie: /msie/.test( userAgent ) && !/opera/.test( userAgent )
        };
    })();
    var setup = function(){
        var elBack2Top = $('<div id="doitbegin_Back2top">回到顶部</div>')
            .appendTo('body')
            .click(function() {
                $("html, body").animate({ scrollTop: 0 }, parseInt(_timeInterval,10) || 300);
            });
        var fnBack2Top = function() {
            var scrollTop = $(document).scrollTop(), winHeight = $(window).height();

            (scrollTop > 0)? elBack2Top.show(): elBack2Top.hide();
            if (browserInfo.msie && parseInt(browserInfo.version,10) == 6) {
                elBack2Top.css("top", scrollTop + winHeight - 150);
            }
        };
        $(window).bind("scroll", fnBack2Top);
        fnBack2Top();
    };
    var init = function(timeInterval){
        _timeInterval = timeInterval;
        $(function(){
            setup();
        });
    };

    return {
        init : init,
        version : '1.0'
    };

})(jQuery);
Back2top.init();
function getMonths(){
	var dateList=new Array();
	var date=new Date();
	var year=date.getYear()+1900;
	var month=date.getMonth()+1;
	for(var i=0;i<3;i++){
		var m=month-i;
		var y=year;
		if(m<=0){
			y=y-1;
			m+=12;
		}
		dateList.push({val:y+"-"+(m<10?("0"+m):m)+"/2",text:y+"年"+(m<10?("0"+m):m)+"月下半月"});
		dateList.push({val:y+"-"+(m<10?("0"+m):m)+"/1",text:y+"年"+(m<10?("0"+m):m)+"月上半月"});
	}
	return dateList;
}