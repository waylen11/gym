
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("jin4"))
</script>
<html>
<head>
    <title>会员私教详情</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/table/bootstrap-table.min.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/bootstrap-table.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/locale/bootstrap-table-zh-CN.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/sweetalert/sweetalert.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/sweetalert/sweetalert.min.js"></script>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/date/bootstrap-datetimepicker.min.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/date/Moment.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/date/bootstrap-datetimepicker.min.js"></script>

    <script>
        $(function () {
            $('#dg').bootstrapTable({
                url:'${pageContext.request.contextPath}/member/query',
                method:'post',
                contentType:"application/x-www-form-urlencoded",
                columns:[
                    { field:'id',title:'会员编号',sortable: true},
                    { field: 'realName',title:'名称',sortable: true},
                    { field:'phone',title:'电话',sortable: true},
                    { field:'sex',title:'性别',
                        formatter:function (value,row,index) {
                            if(row.sex===0){
                                return "女";
                            }else{
                                return "男";
                            }
                        }
                    }
                ],
                queryParamsType:'',
                queryParams:queryParams,
                height:360,
                pageList:[5,10,15],
                pageNumber:1,
                pageSize:5,
                pagination:true,
                sidePagination:'server',
                detailView:true,
                onExpandRow:function(index, row, $detail){
                    var aa=$detail.html("<table></table>").find('table');
                    var idd=row.id;
                    aa.bootstrapTable({
                        url:'${pageContext.request.contextPath}/privateinfo/byid?memberid='+idd+'',
                        columns:[{
                            field: 'id',
                            title:'编号',
                        },
                            {
                                field: 'member.realName',
                                title: '会员名称'
                            },
                            {
                                field: 'coach.realName',
                                title: '私家教练名称'
                            },{
                                field:'subject.courseName',
                                title:'私教课程'
                            },
                            {
                                field: 'count',
                                title: '数量'
                            },{
                                field: 'status',
                                title: '状态',
                                formatter:function (value,row,index) {
                                    if(row.status==1){
                                        return "购买";
                                    }else{
                                        return "赠送";
                                    }
                                }
                            },{
                                field:'countprice',
                                title:'金额',
                            },
                            {
                                field: 'remark',
                                title: '备注',

                            },{
                                field:'courseDate',
                                title:'开始日期',
                            }
                        ]
                    }) ;
                },
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
                <input id="ktype" type="hidden" value="0" class="form-control">
            </div>
            <button onclick="search()" type="button" class="btn btn-default" style="margin-top: 20px" >查询</button>
        </form>
    </div>

</div>
    <%--//页面数据展示--%>
    <div>
        <table id="dg"></table>
    </div>
</body>
</html>
