var userModule = angular.module("UserModule", []);
userModule.controller('loginCtrl',//登录
		function($rootScope,$scope, $http, $state, $stateParams) {
			$scope.data = {username:"admin",password:"123456",vcode:"1234"};
			$scope.vcodeImg="/ValidCode";
			$scope.submit = function() {
				if ($scope.data.username == "" || $scope.data.password == "") {
					$rootScope.alertMsgList.push({text:"用户名、密码、验证码为必填！请填写！",type:"warning"});
					return;
				}
				$rootScope.loading=true;
				$http.post(rootPath + "/login.json", $scope.data).success(
					function(json,status) {
						$rootScope.loading=false;
						if(json.success==false){
							$rootScope.alertMsgList.push({text:json.msg,type:"warning"});
							$scope.c+=1;
							$scope.data.vcode="";
						}else{
							$http.post("qryLoginInfo.json",{}).success(function(json,status){
						    	if(json.status==200){
						    		$rootScope.userInfo=json.data;
						    	}else{
						    		window.location='#/';
						    	}
						    });
							$rootScope.alertMsgList.push({text:"欢迎 "+$scope.data.username,type:"success"});
							window.location="#/home";
						}
					}).error(function(data,status) {
						$rootScope.loading=false;
						$rootScope.alertMsgList.push({text:"网络链接错误！状态码："+status,type:"danger"});
					});
			}
		});
userModule.controller('mainCtrl',//首页
		function($rootScope,$scope, $http, $state, $stateParams) {
			
		});
userModule.controller('modifyPwdCtrl',//修改密码
		function($rootScope,$scope, $http, $state, $stateParams) {
	$scope.savePwd=function(){
		$rootScope.loading=true;
		$http.post(rootPath+"/user/savePwd.json",$scope.data).success(function(data,status){
			if(data.success){
				$rootScope.alertMsgList.push({text:data.msg,type:"success"});
				$scope.data={};
			}else{
				$rootScope.alertMsgList.push({text:data.msg,type:"danger"});
			}
			$rootScope.loading=false;
		}).error(function(data,status) {
			$rootScope.loading=false;
			$rootScope.alertMsgList.push({text:"网络链接错误！状态码："+status,type:"danger"});
		});
	}
});
userModule.controller('regCtrl',//注册帐号
		function($rootScope,$scope, $http, $state, $stateParams) {
	$scope.user={id:"",referee_sn:$rootScope.userInfo.sn};
	var $_form=$("#regForm");
	$_form.validator({
		messages : {
			required : "请填写{0}"
		},
		display : function(e) {
			return $(e).closest(".form-group").find("label").text().replace("：","");
		},
		valid : function() {
			$http.post(rootPath+"/user/reg.json",$scope.user).success(function(data,status){
				if(data.success){
					$rootScope.alertMsgList.push({text:data.msg,type:"success"});
					$scope.user={id:""};
				}else{
					$rootScope.alertMsgList.push({text:data.msg,type:"danger"});
				}
			}).error(function(data,status) {
				$rootScope.alertMsgList.push({text:"网络链接错误！状态码："+status,type:"danger"});
			});
		},
		ignore : ":hidden",
		theme : "yellow_right",
		timely : 1,
		stopOnError : true
	});
	$scope.getSN=function(){
		$http.get(rootPath+"/user/getSN.json").success(function(json,status){
			$scope.user.id=json.msg;
		});
	}
	$scope.getSN();
	$scope.reg=function(){
		$_form.trigger("validate");
	}
});
userModule.controller('bonusRecordCtrl',//游戏奖金记录
	function($rootScope,$scope, $http, $state, $stateParams){
	//月份列表
	var dateList=getMonths();
	$scope.query={date:""};
	$scope.dateList=dateList;
	$scope.qryPage=function(){
		$rootScope.loading=true;
		$http.post(rootPath+"/bonusRecord/dataGrid.json",$scope.query).success(function(json,status){
			$rootScope.loading=false;
			$scope.page=json.data;
		});
	};
	$scope.qryPage();
});
userModule.controller('goldRecordCtrl',//金币记录
		function($rootScope,$scope, $http, $state, $stateParams){
	//月份列表
	var dateList=getMonths();
	$scope.query={date:""};
	$scope.dateList=dateList;
	$scope.qryPage=function(){
		$rootScope.loading=true;
		$http.post(rootPath+"/goldTransDetail/dataGrid.json",$scope.query).success(function(json,status){
			$scope.page=json.data;
			$rootScope.loading=false;
		});
	};
	$scope.qryPage();
});
userModule.controller('noticeCtrl',//玩家公告
		function($rootScope,$scope, $http, $state, $stateParams){
	//月份列表
	var dateList=getMonths();
	$scope.query={date:""};
	$scope.dateList=dateList;
	$scope.qryPage=function(){
		$rootScope.loading=true;
		$http.post(rootPath+"/notice/dataGrid.json",$scope.query).success(function(json,status){
			$scope.page=json.data;
			$rootScope.loading=false;
		});
	};
	$scope.show=function(id){//显示
		$rootScope.loading=true;
		$http.post(rootPath+"/notice/qryOp.json",{id:id}).success(function(json,status){
			$rootScope.loading=false;
			
		});
	}
	$scope.qryPage();
});
userModule.controller('transCtrl',//交易明细
		function($rootScope,$scope, $http, $state, $stateParams){
	//月份列表
	var dateList=getMonths();
	$scope.query={date:""};
	$scope.dateList=dateList;
	$scope.qryPage=function(){
		$rootScope.loading=false;
		$http.post(rootPath+"/trans/dataGrid.json",$scope.query).success(function(json,status){
			$scope.page=json.data;
			$rootScope.loading=false;
		});
	};
	$scope.qryPage();
});
userModule.controller('safeCtrl',//帐号安全
		function($rootScope,$scope, $http, $state, $stateParams) {
	$rootScope.loading=true;
	$http.post("qryLoginInfo.json",{}).success(function(json,status){
    	if(json.status==200){
    		$rootScope.loading=false;
    		$rootScope.userInfo=json.data;
    	}
    });
});
userModule.controller('myMsgCtrl',//私人消息
		function($rootScope,$scope, $http, $state, $stateParams) {
	
});