var rootPath="",tpl=rootPath+"/app/tpl/";//模板路径
var url=window.location.href;
var routerApp = angular.module('routerApp', ['ui.router','UserModule','filterModule','ui.bootstrap']);
/**
 * 由于整个应用都会和路由打交道，所以这里把$state和$stateParams这两个对象放到$rootScope上，方便其它地方引用和注入。
 * 这里的run方法只会在angular启动的时候运行一次。
 * @param  {[type]} $rootScope
 * @param  {[type]} $state
 * @param  {[type]} $stateParams
 */
routerApp.run(function($rootScope,$state,$http,$stateParams){
    $rootScope.$state = $state;
    $rootScope.system={name:"金叶子投资理财复利系统"};
    $rootScope.userInfo={};//存储登录用户信息
    $rootScope.alertMsgList=[];//消息列表
    $rootScope.rootPath=rootPath;
    $rootScope.tpl=tpl;
    $rootScope.$stateParams = $stateParams;
    console.log("$state:"+$state+"\t$stateParams:"+$stateParams);
    $http.post("qryLoginInfo.json",{}).success(function(json,status){
    	if(json.status==200){
    		$rootScope.userInfo=json.data;
    	}else{
    		window.location='#/';
    	}
    });
    setInterval(function(){
    	console.log("调用定时器");
    	  $rootScope.$apply(function(){
    	      $rootScope.alertMsgList.splice(0,1);
    	    });
    	 },5000);
    /*$rootScope.$watchCollection("alertMsgList",function(news,olds){
    	if(news.length>olds.length){
    		setTimeout(function (){
    			$rootScope.alertMsgList.splice(0,1);
    		},500);
    	}
    });*/
});
