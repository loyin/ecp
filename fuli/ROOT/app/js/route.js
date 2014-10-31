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
        .state('home', {url: '/home',views: {'':{templateUrl: tpl+'home.html'},
        	'center@home': {templateUrl: tpl+'main.html',controller:"mainCtrl"}
        	}
        })
        .state('notice', {url: '/notice',views: {'':{templateUrl: tpl+'home.html'},
        	'center@notice': {templateUrl: tpl+'notice.html',controller:"noticeCtrl"}
        	}
        })
        .state('noticeEdit', {url: '/noticeEdit/:id',views: {'':{templateUrl: tpl+'home.html'},
        	'center@noticeEdit': {templateUrl: tpl+'noticeEdit.html',controller:"noticeEditCtrl"}
        }
        })
        .state('noticeShow', {url: '/noticeShow/:id',views: {'':{templateUrl: tpl+'home.html'},
        	'center@noticeShow': {templateUrl: tpl+'noticeShow.html',controller:"noticeShowCtrl"}
        }
        })
        .state('userInfo', {url: '/userInfo',views: {'':{templateUrl: tpl+'home.html'},
        	'center@userInfo': {templateUrl: tpl+'userInfo.html',controller:"userInfoCtrl"}
        }
        })
        .state('modifyPwd', {url: '/modifyPwd',views: {'':{templateUrl: tpl+'home.html'},
        	'center@modifyPwd': {templateUrl: tpl+'modifyPwd.html',controller:"modifyPwdCtrl"}
        }
        })
        .state('account', {url: '/account',views: {'':{templateUrl: tpl+'home.html'},
        	'center@account': {templateUrl: tpl+'account.html',controller:"accountCtrl"}
        }
        })
        .state('securedTran', {url: '/securedTran',views: {'':{templateUrl: tpl+'home.html'},
        	'center@securedTran': {templateUrl: tpl+'securedTran.html',controller:"securedTranCtrl"}
        }
        })
        .state('buy', {url: '/buy',views: {'':{templateUrl: tpl+'home.html'},
        	'center@buy': {templateUrl: tpl+'buy.html',controller:"buyCtrl"}
        }
        })
        .state('buyRecord', {url: '/buyRecord',views: {'':{templateUrl: tpl+'home.html'},
        	'center@buyRecord': {templateUrl: tpl+'buyRecord.html',controller:"buyRecordCtrl"}
        }
        })
        .state('sale', {url: '/sale',views: {'':{templateUrl: tpl+'home.html'},
        	'center@sale': {templateUrl: tpl+'sale.html',controller:"saleCtrl"}
        }
        })
        .state('saleRecord', {url: '/saleRecord',views: {'':{templateUrl: tpl+'home.html'},
        	'center@saleRecord': {templateUrl: tpl+'saleRecord.html',controller:"saleRecordCtrl"}
        }
        })
        .state('transfer', {url: '/transfer',views: {'':{templateUrl: tpl+'home.html'},
        	'center@transfer': {templateUrl: tpl+'transfer.html',controller:"transferCtrl"}
        }
        })
        .state('transferRecord', {url: '/transferRecord',views: {'':{templateUrl: tpl+'home.html'},
        	'center@transferRecord': {templateUrl: tpl+'transferRecord.html',controller:"transferRecordCtrl"}
        }
        })
        .state('bonusRecord', {url: '/bonusRecord',views: {'':{templateUrl: tpl+'home.html'},
        	'center@bonusRecord': {templateUrl: tpl+'bonusRecord.html',controller:"bonusRecordCtrl"}
        }
        })
        .state('goldRecord', {url: '/goldRecord',views: {'':{templateUrl: tpl+'home.html'},
        	'center@goldRecord': {templateUrl: tpl+'goldRecord.html',controller:"goldRecordCtrl"}
        }
        })
        .state('reg', {url: '/reg',views: {'':{templateUrl: tpl+'home.html'},
        	'center@reg': {templateUrl: tpl+'reg.html',controller:"regCtrl"}
        }
        })
        .state('myAccounts', {url: '/myAccounts',views: {'':{templateUrl: tpl+'home.html'},
        	'center@myAccounts': {templateUrl: tpl+'myAccounts.html',controller:"myAccountsCtrl"}
        }
        })
        .state('activeAccount', {url: '/activeAccount',views: {'':{templateUrl: tpl+'home.html'},
        	'center@activeAccount': {templateUrl: tpl+'activeAccount.html',controller:"activeAccountCtrl"}
        }
        })
        .state('myNewAccount', {url: '/myNewAccount',views: {'':{templateUrl: tpl+'home.html'},
        	'center@myNewAccount': {templateUrl: tpl+'myNewAccount.html',controller:"myNewAccountCtrl"}
        }
        })
        .state('accountManage', {url: '/accountManage',views: {'':{templateUrl: tpl+'home.html'},
        	'center@accountManage': {templateUrl: tpl+'accountManage.html',controller:"accountManageCtrl"}
        }
        })
        .state('contactUs', {url: '/contactUs',views: {'':{templateUrl: tpl+'home.html'},
        	'center@contactUs': {templateUrl: tpl+'contactUs.html',controller:"contactUsCtrl"}
        }
        })
        .state('messageBox', {url: '/messageBox',views: {'':{templateUrl: tpl+'home.html'},
        	'center@messageBox': {templateUrl: tpl+'messageBox.html',controller:"messageBoxCtrl"}
        }
        })
        .state('guid', {url: '/guid',views: {'':{templateUrl: tpl+'home.html'},
        	'center@guid': {templateUrl: tpl+'guid.html',controller:"guidCtrl"}
        }
        })
        .state('help', {url: '/help',views: {'':{templateUrl: tpl+'home.html'},
        	'center@help': {templateUrl: tpl+'help.html',controller:"helpCtrl"}
        }
        })
        .state('safe', {url: '/safe',views: {'':{templateUrl: tpl+'home.html'},
        	'center@safe': {templateUrl: tpl+'safe.html',controller:"safeCtrl"}
        }
        })
        ;
});