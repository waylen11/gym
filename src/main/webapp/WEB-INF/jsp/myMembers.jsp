<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("myMembers"))
</script>
<html>
<head>
    <title>我的会员</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.css" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/table/bootstrap-table.min.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/bootstrap-table.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/locale/bootstrap-table-zh-CN.min.js"></script>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/static/sweetalert/sweetalert.css" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/sweetalert/sweetalert.min.js"></script>

    <script>
        $(function () {
            $('#dg').bootstrapTable({
                url:'${pageContext.request.contextPath}/coach/myMembersQuery',
                method:'post',
                contentType:"application/x-www-form-urlencoded",
                columns:[
                    { field:'id',title:'记录编号',sortable: true},
                    { field:'member.realName',title:'会员姓名',sortable: true},
                    { field:'member.phone',title:'会员电话',sortable: true},
                    { field:'subject.courseName',title:'私教课程'},
                    { field:'count',title:'剩余次数',
                        formatter:function (value,row,index) {
                            if(row.count > 0){
                                return "<span style='color:green'>" + row.count + " 节</span>";
                            }else{
                                return "<span style='color:red'>0 节(已用完)</span>";
                            }
                        }
                    },
                    { field:'countprice',title:'总金额(元)'},
                    { field:'courseDate',title:'开始日期'},
                    { field:'status',title:'状态',
                        formatter:function (value,row,index) {
                            return row.status==1 ? "购买" : "赠送";
                        }
                    },
                    { field:'remark',title:'备注'}
                ],
                queryParamsType:'',
                queryParams:queryParams,
                height:420,
                pageList:[5,10,15],
                pageNumber:1,
                pageSize:10,
                pagination:true,
                sidePagination:'server',
                detailView:true,
                onExpandRow:function(index, row, $detail){
                    var aa=$detail.html("<table></table>").find('table');
                    var idd=row.id;
                    aa.bootstrapTable({
                        url:'${pageContext.request.contextPath}/privateinfo/query2?pid='+idd,
                        columns:[
                            { field:'member.id',title:'会员编号'},
                            { field:'member.realName',title:'会员姓名'},
                            { field:'member.sex',title:'性别',
                                formatter:function(v,r){return r.member.sex==0?'女':'男';}
                            },
                            { field:'member.age',title:'年龄'},
                            { field:'member.memberType.typeName',title:'卡类型'},
                            { field:'member.remainingDays',title:'卡剩余天数'},
                            { field:'member.memberXufei',title:'卡到期日期'}
                        ]
                    });
                }
            })
        });

        function queryParams(afds){
            var i={
                "pageSize":afds.pageSize,
                "pageNumber":afds.pageNumber
            };
            return i;
        }

        function search(){
            var opt=$('#dg').bootstrapTable('getOptions');
            $.post("${pageContext.request.contextPath}/coach/myMembersQuery",{
                "pageSize":opt.pageSize,
                "pageNumber":opt.pageNumber
            },function (releset) {
                $("#dg").bootstrapTable('load',releset);
            })
        }
    </script>
</head>
<body background="${pageContext.request.contextPath}/static/HTmoban/images/tongji.jpeg">
    <div class="panel panel-default">
        <div class="panel-body">
            <form class="form-inline">
                <label class="control-label">我的私教会员列表(按当前登录教练自动筛选)</label>
                <button onclick="search()" type="button" class="btn btn-default" style="float:right;">刷新</button>
            </form>
        </div>
    </div>
    <div>
        <table id="dg"></table>
    </div>
</body>
</html>
