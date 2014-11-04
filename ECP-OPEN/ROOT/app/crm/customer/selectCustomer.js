function callback(){
	var t=$("#grid").jqGrid("getGridParam","selrow");
	if(t.length>0){var e=$("#grid").jqGrid("getRowData",t);
	e.id=t;
var i=e.number+" "+e.name,a=parent.THISPAGE.$_customer;
a.find("input").val(i);
a.data("contactInfo",e)}}
var queryConditions={skey:""},urlParam=Public.urlParam(),
zTree,SYSTEM=parent.parent.SYSTEM,taxRequiredCheck=SYSTEM.taxRequiredCheck;
taxRequiredInput=SYSTEM.taxRequiredInput;
var THISPAGE={
		init:function(){this.initDom();
this.loadGrid();
this.addEvent()},
initDom:function(){
	this.$_matchCon=$("#matchCon");
this.$_matchCon.placeholder()},
loadGrid:function(){
	var t="/basedata/contact.do?action=list&isDelete=2";
"10"===urlParam.type&&(t+="&type=10");
var e=($(window).height()-$(".grid-wrap").offset().top-84,[{name:"customerType",label:"类别",index:"customerType",width:100,title:!1},{name:"number",label:"编号",index:"number",width:100,title:!1},{name:"name",label:"名称",index:"name",width:220,classes:"ui-ellipsis"},{name:"contacter",label:"联系人",index:"contacter",width:100,align:"center",classes:"ui-ellipsis"},{name:"mobile",label:"手机",index:"mobile",width:100,align:"center",title:!1}]);
$("#grid").jqGrid({url:t,postData:queryConditions,datatype:"json",autowidth:!0,height:354,altRows:!0,gridview:!0,onselectrow:!1,multiselect:!0,multiboxonly:!0,colModel:e,pager:"#page",viewrecords:!0,cmTemplate:{sortable:!1},rowNum:100,rowList:[100,200,500],shrinkToFit:!0,jsonReader:{root:"data.rows",records:"data.records",total:"data.total",repeatitems:!1,id:"id"},loadComplete:function(){$("#jqgh_grid_cb").hide()},loadError:function(){}})},reloadData:function(t){$("#grid").jqGrid("setGridParam",{page:1,postData:{skey:t}}).trigger("reloadGrid")},addEvent:function(){var t=this;
$(".grid-wrap").on("click",".ui-icon-search",function(t){t.preventDefault();
var e=$(this).parent().data("id");
Business.forSearch(e,"")});
$("#search").click(function(){var e="输入编号 / 名称 / 联系人 / 电话查询"===t.$_matchCon.val()?"":t.$_matchCon.val();
t.reloadData(e)});
$("#refresh").click(function(){t.reloadData(queryConditions)})}};
THISPAGE.init();
