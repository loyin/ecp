var userModule = angular.module("UserModule", []);
userModule.controller('loginCtrl',//登录
		function($rootScope,$scope, $http, $state, $stateParams) {
			$scope.data = {username:"admin",password:"123456",vcode:"1234"};
			$scope.vcodeImg="/ValidCode";
			$scope.submit = function() {
				if ($scope.data.username == "" || $scope.data.password == "") {
					$rootScope.alertMsgList=[{text:"用户名、密码、验证码为必填！请填写！",type:"warning"}];
//					jDialog.alert("用户名、密码、验证码为必填！请填写！",{},{autoClose:3000,showShadow:false});
					return;
				}
				$http.post(rootPath + "/login.json", $scope.data).success(
					function(json,status) {
						if(json.success==false){
//							jDialog.alert(json.msg,{},{autoClose:3000,showShadow: false});
							$rootScope.alertMsgList=[{text:json.msg,type:"warning"}];
							$scope.c+=1;
							$scope.data.vcode="";
						}else{
							$rootScope.userInfo.sn=$scope.data.username;
							$rootScope.is_login=true;
							$rootScope.alertMsgList=[{text:"欢迎 "+$scope.data.username,type:"success"}];
							window.location="#/home";
						}
					}).error(function(data,status) {
//						jDialog.alert("网络链接错误！状态码："+status,{},{autoClose:3000,showShadow: false});
						$rootScope.alertMsgList=[{text:"网络链接错误！状态码："+status,type:"danger"}];
						is_login=false;
					});
			}
		});
userModule.controller('logOutCtrl',//退出
		function($scope, $http, $state, $stateParams) {
			$scope.data = {username:"",password:""};
			$scope.logout = function() {
				var dialog = jDialog.confirm('确定退出吗！',{
				    type : 'highlight',
				    text : '退出',
				    handler : function(button,dialog) {
				    	$http.post(rootPath + "/logout.json").success(
								function(data,status) {
									window.location=rootPath+"/";
								});
				        dialog.close();
				    }
				},{
				    type : 'normal',
				    text : '取消',
				    handler : function(button,dialog) {
				    	dialog.close();
				    }
				});
			}
		});