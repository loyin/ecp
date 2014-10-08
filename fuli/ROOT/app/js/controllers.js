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
				$http.post(rootPath + "/login.json", $scope.data).success(
					function(json,status) {
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
		$http.post(rootPath+"/user/savePwd.json",$scope.data).success(function(data,status){
			if(data.success){
				$rootScope.alertMsgList.push({text:data.msg,type:"success"});
				$scope.data={};
			}else{
				$rootScope.alertMsgList.push({text:data.msg,type:"danger"});
			}
		}).error(function(data,status) {
			$rootScope.alertMsgList.push({text:"网络链接错误！状态码："+status,type:"danger"});
		});
	}
});
userModule.controller('regCtrl',//注册帐号
		function($rootScope,$scope, $http, $state, $stateParams) {
	$scope.user={sn:""};
	$scope.getSN=function(){
		$http.get(rootPath+"/user/getSN.json").success(function(json,status){
			$scope.user.sn=json.msg;
		});
	}
	$scope.reg=function(){
		$http.post(rootPath+"/user/reg.json",$scope.user).success(function(data,status){
			if(data.success){
				$rootScope.alertMsgList.push({text:data.msg,type:"success"});
				$scope.user={sn:""};
			}else{
				$rootScope.alertMsgList.push({text:data.msg,type:"danger"});
			}
		}).error(function(data,status) {
			$rootScope.alertMsgList.push({text:"网络链接错误！状态码："+status,type:"danger"});
		});
	}
});
userModule.controller('myMsgCtrl',//私人消息
		function($rootScope,$scope, $http, $state, $stateParams) {
	
});