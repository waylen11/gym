<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("jin"))
</script>
<html>
<head>
    <title>会员卡续卡</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/table/bootstrap-table.min.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/bootstrap-table.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/locale/bootstrap-table-zh-CN.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/sweetalert/sweetalert.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/sweetalert/sweetalert.min.js"></script>
    <script src="${pageContext.request.contextPath}/static//bootstrap/js/tableExport.js"></script>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/date/bootstrap-datetimepicker.min.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/date/Moment.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/date/bootstrap-datetimepicker.min.js"></script>

    <%--<link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/">--%>
    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/bootstrap-select-1.12.4/js/bootstrap-select.js"></script>--%>

    <%--<script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/bootstrap-select-1.12.4/js/i18n/defaults-*.min.js"></script>--%>

    <script>
        $(function () {
            $.post("${pageContext.request.contextPath}/ktype/query",{},function (releset) {
                var e=releset;
                $(e).each(function () {
                    $('#ktype').append('<option value='+this.id+' >'+this.typeName+'</option>');
                })
            });
            $('#srdata').datetimepicker({
                //viewMode: 'day',
                format: 'MM-DD'
            });
            $('#dg').bootstrapTable({
                url:'${pageContext.request.contextPath}/member/query',
                method:'post',
                contentType:"application/x-www-form-urlencoded",
                columns:[
                    { field:'id',title:'会员编号',sortable: true},
                    { field: 'realName',title:'名称',sortable: true},
                    { field:'phone',title:'电话',sortable: true},
                    { field:'memberType.typeName',title:'卡类型'},
                    { field:'memberDate',title:'录入日期'},
                    { field: 'status',title:'会员状态',
                        formatter:function (value,row,index) {
                            // 按剩余天数判断,与 status 字段保持联动
                            if(row.remainingDays!=null && row.remainingDays>0){
                                return "正常"
                            }else{
                                return "<p style='color:red'>请续卡</p>"
                            }
                        }
                    },
                    { field:'memberXufei',title:'到期日期'},
                    { field:'xxx',title:'操作',
                        formatter:function (value,row,index) {
                            return "<a title='续卡' href='javascript:xufei("
                                + row.id + ")'><span class='glyphicon glyphicon-arrow-right'>续卡</span></a>";
                        }
                    },
                ],
                queryParamsType:'',
                queryParams:queryParams,
                height:360,
                pageList:[5,10,15],
                pageNumber:1,
                pageSize:5,
                pagination:true,
                sidePagination:'server',
                onDblClickRow:function(row,$element,field) {

                }
            })
        });

        //获取当前的条件个页面页数即使更新值
        function queryParams(afds){
            var i={
                "pageSize":afds.pageSize,
                "pageNumber":afds.pageNumber,
                "id":$('#hyid').val(),
                "ktype":$('#ktype').val()

            };
            return i;
        }
        //查询
        function search(){
            var opt=$('#dg').bootstrapTable('getOptions');
            var hyid=$('#hyid').val();
            var ktype=$('#ktype').val();
            $.post("${pageContext.request.contextPath}/member/query",{"pageSize":opt.pageSize,"pageNumber":opt.pageNumber,"hyname":hyid,"ktype":ktype},function (releset) {
                $("#dg").bootstrapTable('load',releset) ;
            })
        }
        function xufei(id) {
            //$('#xztype').empty();
            $.post("${pageContext.request.contextPath}/ktype/query",{},function (releset) {
                var e=releset;
                var tt ="";
                var tttt="";
                var ttt="<option value='-1'>"+"--请选择--"+"</option>";
                $(e).each(function () {
                    tt += "<option value='"+this.id+"'>"+""+this.typeName+"</option>";
                    tttt=ttt+tt;
                    $('#xztype').html(tttt);
                })

            });
            $('#ycy').val(id);
            $('#updateeModal').modal('show');
        }
        function cz() {
            if(!validateAdd()){
                return;
            }
            var id=$('#ycy').val();
            var xztype=$('#xztype').val();
            var jine=$('#xzmoney').val();
            var ssmoney=$('#ssmoney').val();
            var zhaoqian=$('#zhaoqian').val();
            $.post("${pageContext.request.contextPath}/member/cha",{"id":id},function (releset) {
                var memberxufei = releset.memberXufei;
                $.post("${pageContext.request.contextPath}/ktype/query2",{"xztype":xztype},function (releset) {
                    var typetian=releset.validDays;
                    var date1 = new Date();
                    var date2 = new Date(date1);
                    var date3 = new Date(memberxufei);
                    var date4 = new Date(memberxufei);
                    if(date1.getTime()>= date3.getTime()){
                        var qb=date2.setDate(date1.getDate() + typetian);
                        var rq=date2.getFullYear() + "-" + (date2.getMonth() + 1) + "-" + date2.getDate();
                    }else {
                        var qb=date4.setDate(date3.getDate() + typetian);
                        var rq=date4.getFullYear() + "-" + (date4.getMonth() + 1) + "-" + date4.getDate();

                    }

                    var bz=$('#bz').val();
                    $.post("${pageContext.request.contextPath}/member/ins",{
                        "remark":bz,
                        "member.id":id,"membertype.id":xztype,"money":jine,"ssmoney":ssmoney,"zlmoney":zhaoqian,"daoqi":rq},function (releset) {
                        if(releset != null){
                            $("#dg").bootstrapTable('load',releset) ;
                            $('#updateeModal').modal('hide');
                            swal(
                                {
                                    title:"续卡成功",
                                    type:"success",
                                    timer: 1500,
                                    showConfirmButton: false
                                }
                            )
                        }else{
                            swal(
                                {
                                    title:"续卡失败",
                                    type:"warning",
                                    timer: 1500,
                                    showConfirmButton: false
                                }
                            )
                        }
                    })
                })
            })
        }

        function validateAdd() {

            $("#xztype").parent().find("span").remove();
            $("#ssmoney").parent().find("span").remove();

            var xztype = $("#xztype").val().trim();
            if(xztype==-1){
                $("#xztype").parent().append("<span style='color:red'>请选择会员卡型</span>");
                return false;
            }

            var ssmoney = $("#ssmoney").val().trim();
            if(ssmoney == null || ssmoney == ""){
                $("#ssmoney").parent().append("<span style='color:red'>请填写实收金额</span>");
                return false;
            }

            if(!(/^[1-9]\d*$/.test(ssmoney))){
                $("#ssmoney").parent().append("<span style='color:red'>实收金额只能为正整数</span>");
                return false;
            }



            return true;
        }

        function sss() {
            var xztype=$('#xztype').val();
            if(xztype!=0){
                $.post("${pageContext.request.contextPath}/ktype/query2",{xztype:xztype},function (releset) {
                       $('#xzmoney').val(releset.price);
                });
            }
        }
        function zql() {
            var jine=$('#xzmoney').val();
            var ssjine=$('#ssmoney').val();
            var zhao=ssjine-jine;
            $('#zhaoqian').val(zhao);
        }
    </script>
</head>
<body background="${pageContext.request.contextPath}/static/HTmoban/images/tongji.jpeg">
<%--    //查询--%>
<div class="panel panel-default">
    <div class="panel-body">
        <form class="form-inline">
            <div  class="input-group input-daterange">
                <label for="hyid" class="control-label">姓名:</label>
                <input id="hyid" type="text" class="form-control">
            </div>
            <div  class="input-group input-daterange">
                <label for="ktype" class="control-label">卡型:</label>
                <select class="form-control" id="ktype">
                    <option value="0">请选择</option>
                </select>
            </div>
            <button onclick="search()" type="button" class="btn btn-default" style="margin-top: 20px" >查询</button>
        </form>
    </div>

</div>
<%--//页面数据展示--%>
<div>
    <table id="dg"></table>
</div>
<%--修改--%>
<div class="modal fade" id="updateeModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="updateModalLabel">选择续卡类型</h4>
            </div>
            <div class="modal-body">
                <form>
                    <div>
                        <label for="xztype" class="control-label">卡型:</label>
                        <select class="form-control" id="xztype" oninput="sss()">

                        </select>
                    </div>
                    <div>
                        <label for="xzmoney" class="control-label">金额:</label>
                        <input class="form-control" type="text" id="xzmoney" readonly="readonly" >
                    </div>
                    <div>
                        <label for="ssmoney" class="control-label">实收金额:</label>
                        <input class="form-control" type="text" id="ssmoney" oninput="zql()">
                    </div>
                    <div>
                        <label for="zhaoqian" class="control-label">找钱:</label>
                        <input class="form-control" type="text" id="zhaoqian" readonly="readonly">
                    </div>
                    <div>
                        <label for="bz" class="control-label">备注:</label>
                        <input class="form-control" type="text" id="bz">
                    </div>
                    <input type="hidden" id="ycy">
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                <button type="button" class="btn btn-primary" onclick="cz()">充值</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
