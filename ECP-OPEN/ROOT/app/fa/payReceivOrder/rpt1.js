var url=rootPath+"/fa/payReceivOrder",gridQryUrl=url+"/rpt1List.json",subjectList=[],$subject;
var model = avalon.define({$id:'view',user:SYSTEM.user,
    query :{keyword:"",start_date:SYSTEM.beginDate,end_date:SYSTEM.endDate,keyword:'',subject_id:"",account_id:""},
    list:[],nprint:true,accountList:[],amt_in_total:0,amt_out_total:0,
	init:function(){
		$(".ui-datepicker-input").datepicker();
		Public.ajaxPost(url+"/dataGrid.json",{}, function(json){
			if(json.status==200){
				model.accountList=json.data.list;
			}
		});
		Public.ajaxPost(rootPath+"/fa/subject/list1.json",{}, function(json){
			if(json.status==200){
				subjectList=json.data;
			}
		});
//		model.loadData();
		/**财务科目 START*/
		$subject=$("#subject");
		$subject.combo(
					{
						data : rootPath+"/fa/subject/list.json",
						value : "id",
						text : "name",
						width : 210,
						defaultSelected:model.query.subject_id,//$subject.data("defItem")||"",
						editable :true,
						ajaxOptions : {
							formatData : function(e) {
								if (200 == e.status) {
									e.data.unshift({
										id:"",
										name : "（空）"
									});
									return e.data
								}
								return []
							},callback: {
								onChange: function(data){
									model.query.subject_id=data.id;
								},
								onListClick: function(){
								}
							}
						}
					}).getCombo();
		/**财务科目 END*/
	},
	loadData:function(){
		model.query.subject_id=$subject.getCombo().getValue();
		Public.ajaxPost(gridQryUrl,model.query.$model, function(json){
			model.amt_in_total=0;
			model.amt_out_total=0;
			if(json.status==200){
				model.list=json.data;
				for(var i=0;i<json.data.length;i++){
					var e=json.data[i];
					model.amt_in_total+=new Number(e.amt_in);
					model.amt_out_total+=new Number(e.amt_out);
				}
			}
		});
	},
	printRpt:function(){
		model.nprint=false;
		window.print();
		model.nprint=true;
	}
});
model.init();
avalon.filters.subject=function(v){
	return subjectList[v];
}