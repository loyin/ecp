var api = frameElement.api,id=api.data.id,oper = api.data.oper,url=rootPath+"/wf/process",$_form=$("#base_form"),addNew=false;
var model = avalon.define({$id:'ctrl',data:{id:"",name:''}});
var THISPAGE = {
	init : function() {
		this.initDom();
		this.initBtn();
	},
	initDom : function() {
			$("#upfile_btn").uploadify({
				height        : 30,
				swf           : rootPath+'/assets/js/plugins/uploadify/uploadify.swf',
				uploader      : url+'/deploy/'+(id?id:""),
				width         : 220,
				buttonText:'选择文件',//浏览文件',
				fileTypeExts:'*.snaker;',
				fileTypeDesc:'工作流程文件',//'Excel文件(*.xls;*.xlsx);Word文件(*.doc;*.docx);幻灯片(*.ppt;*.pptx;);PDF(*.pdf);图片(*.jpg;*.jepg;*.bmp;*.png;*.gif);视频(*.avi;*.mp4;*.flv);',
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