/**
 * 配置路由。
 * 注意这里采用的是ui-router这个路由，而不是ng原生的路由。
 * ng原生的路由不能支持嵌套视图，所以这里必须使用ui-router。
 * @param  {[type]} $stateProvider
 * @param  {[type]} $urlRouterProvider
 * @return {[type]}
 */
routerApp.config(function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.otherwise('/');
    $stateProvider
        .state('index', {url: '/',views: {'': {templateUrl: tpl+'login.html',controller:"loginCtrl"}}})
        .state('main', {url: '/home',views: {'':{templateUrl: tpl+'home.html'},
        	'main@home': {templateUrl: tpl+'main.html',controller:"mainCtrl"}
        	}
        });
});