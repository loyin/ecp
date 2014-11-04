var $_form=$("#base_form");
var model=avalon.define({
	$id:"ctrl",
	data:{city:"",code:"",wx_secret:"",industry:"",wxid:"",province:"",wx_account:"",file_storage_size:0,fax:"",area:"",reg_email:"",wx_appid:"",address:"",wx_type:"",expiry_date:"",telephone:"",reg_date:"",name:"",wx_qrcode:"",short_name:"",
		config:{p_pact_alarm:30,p_sale_audit:"false",p_sysname:"",p_saletui_audit_type:"false",p_sale_audit_type:"false",p_custpool_c:90,p_saletui_audit:"false",
			p_member_card_pay:"false"}
	},
	parameList:SYSTEM.custParame.typeList,
	provinceList:SYSTEM.area.provinceList,
    cityList:[],changeCity:function(v){
    	model.cityList=SYSTEM.area[v+""];
    },
	radio1:function(v){model.data.config.p_sale_audit=v;},
	radio2:function(v){model.data.config.p_saletui_audit=v;},
	radio3:function(v){model.data.config.p_sale_audit_type=v;},
	radio4:function(v){model.data.config.p_saletui_audit_type=v;},
	radio5:function(v){model.data.config.p_member_card_pay=v;}
});
var THISPAGE = {
		init:function(){
			this.initDom();
			this.initEvent();
		},
		initDom:function(){
			Public.ajaxPost(rootPath+"/sso/company/qry.json",{}, function(json){
				if(json.status==200){
					avalon.mix(model.data,json.data);
					model.changeCity(model.data.province);
				}else{
					parent.Public.tips({type: 1, content:json.msg});
				}
			});
			THISPAGE.initEvent();
		},
		initEvent:function(){
			this.initValidator();
		},
		initValidator:function(){
			$_form.validator({
				messages:{required:"请填写{0}"},
				display:function(e){
					return $(e).closest(".row-item").find("label").text()
				},
				valid:function(){
					postData();
				},
				ignore:":hidden",
				theme:"yellow_bottom",
				timely:1,
				stopOnError:true
			});
		}
	};
	function postData(){
		Public.ajaxPost(rootPath+"/sso/company/saveConfig.json",model.data.$model, function(t) {
			if (200 == t.status) {
				parent.parent.Public.tips({content:"保存成功！"});
			} else{
				parent.parent.Public.tips({type:1,content:"保存失败！"+t.msg});
			}
		});
	}
	THISPAGE.init();