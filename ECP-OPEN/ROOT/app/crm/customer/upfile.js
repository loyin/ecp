var url=rootPath+"/crm/customer";
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
			$("#upfile_btn").uploadify({
				height        : 30,
				swf           : rootPath+'/assets/js/plugins/uploadify/uploadify.swf',
				uploader      : url+'/saveFile/'+SYSTEM.user.id+"-"+SYSTEM.user.company_id,
				width         : 220,
				buttonText:'选择文件',//浏览文件',
				fileTypeExts:'*.xls;*.xlsx',
				fileTypeDesc:'Excel文件(*.xls;*.xlsx);',
				onUploadSuccess:function(file, data, response) {
					eval("var json="+data+";");
					if(json.success){
						model.data.id=json.data;
						parent.parent.Public.tips({
							content :json.msg
						});
						parent.THISPAGE.reloadData(null);
					}else{
						parent.parent.Public.tips({
							content :json.msg ,type:1
						});
					}
				}
			});
	},
	initBtn:function(){
		api.button({
			id : "cancel",
			name :"关闭"
		})
	}
};
THISPAGE.init();