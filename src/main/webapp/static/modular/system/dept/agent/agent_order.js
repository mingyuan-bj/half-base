/**
 * 渠道分会订单初始化
 */
var AgentOrder = {
    id: "AgentOrderTable",	//表格id
    seItem: null,		//选中的条目
    table: null,
    layerIndex: -1
};

/**
 * 初始化表格的列
 */
AgentOrder.initColumn = function () {
    return [
        {field: 'selectItem', radio: true},
       /* {title: 'id', field: 'id', visible: false, align: 'center', valign: 'middle',width:'50px'},*/
        {title: '分会简称', field: 'simplename', align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: '分会全称', field: 'fullname', align: 'center', valign: 'middle', sortable: true,width:'150px'},
        {title: '分会级别', field: 'levelName', align: 'center', valign: 'middle', sortable: true },
        /*{title: '创建时间', field: 'createTime', align: 'center', valign: 'middle', sortable: true},*/
        {title: '销售总计', field: 'sum', align: 'center', valign: 'middle', sortable: true},
        {title: '推广大使', field: 'tuiguang', align: 'center', valign: 'middle', sortable: true},
        {title: '销售大使', field: 'xiaoshou', align: 'center', valign: 'middle', sortable: true},
        {title: '知识送礼', field: 'give', align: 'center', valign: 'middle', sortable: true},
        {title: '公众号', field: 'h51', align: 'center', valign: 'middle', sortable: true},
        {title: 'ios', field: 'ios', align: 'center', valign: 'middle', sortable: true},
        {title: 'android', field: 'android', align: 'center', valign: 'middle', sortable: true},
        {title: '实体卡', field: 'card', align: 'center', valign: 'middle', sortable: true}
    ];
};







/**
 * 查询分会列表
 */
AgentOrder.search = function () {
    var queryData = {};
    queryData['condition'] = $("#condition").val();
    queryData['beginTime'] = $("#beginTime").val();
    queryData['endTime'] = $("#endTime").val();
    AgentOrder.table.refresh({query: queryData});
};
/**
 * 导出表格
 */
AgentOrder.exportList = function () {
    var condition= $("#condition").val();
    if(condition==null || condition==''){
        condition='null';
    }
    var beginTime=$("#beginTime").val();
    if(beginTime==null || beginTime==''){
        beginTime='null';
    }
    var endTime=$("#endTime").val();
    if(endTime==null || endTime==''){
        endTime='null';
    }
    window.location.href=Feng.ctxPath + "/agent/branchsaler/agent_orderExecl/"+beginTime+"/"+endTime+"/"+condition;
    Feng.info("数据正在导出,请耐心等待。");
};

AgentOrder.resetSearch = function () {
    $("#condition").val("");
    $("#beginTime").val("");
    $("#endTime").val("");
    AgentOrder.search();
}
AgentOrder.widthAuto = function () {
    $(".fixed-table-container").css("width",'1500px');
}
$(function () {
    var defaultColunms = AgentOrder.initColumn();
    var table = new BSTreeTable(AgentOrder.id, "/agent/branchsaler/agent_order", defaultColunms);
    table.setExpandColumn(0);
    table.setCodeField("id");
    table.setParentCodeField("pid");
    table.setExpandAll(true);
    table.init();
    AgentOrder.table = table;
    AgentOrder.widthAuto();
});
