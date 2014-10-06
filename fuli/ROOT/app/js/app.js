var rootPath="",tpl="app/tpl/";//模板路径
var routerApp = angular.module('routerApp', ['ui.router','UserModule']);
/**
 * 由于整个应用都会和路由打交道，所以这里把$state和$stateParams这两个对象放到$rootScope上，方便其它地方引用和注入。
 * 这里的run方法只会在angular启动的时候运行一次。
 * @param  {[type]} $rootScope
 * @param  {[type]} $state
 * @param  {[type]} $stateParams
 */
routerApp.run(function($rootScope, $state, $stateParams){
    $rootScope.$state = $state;
    $rootScope.username="";
    $rootScope.alertMsgList=[];//消息列表
    $rootScope.userId=null;
    $rootScope.is_login=false;
    $rootScope.$stateParams = $stateParams;
    console.log("$state:"+$state+"\t$stateParams:"+$stateParams);
    setInterval(function(){
    	console.log("调用定时器");
    	  $rootScope.$apply(function(){
    	      $rootScope.alertMsgList=[];
    	    });
    	 },10000);
});