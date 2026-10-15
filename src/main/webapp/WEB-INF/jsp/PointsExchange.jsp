<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js"></script>
<html>
<head>
    <title>积分兑换记录</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/bootstrap/css/bootstrap.css"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/table/bootstrap-table.min.css"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/bootstrap-table.js"></script>
    <script type="text/javascript" src="${pageContext.request.contextPath}/static/table/locale/bootstrap-table-zh-CN.min.js"></script>

    <script>
        $(function () {
            $('#table').bootstrapTable({
                url: '${pageContext.request.contextPath}/pointsExchange/list',
                method: 'post',
                columns: [{
                    field: 'exchangeId',
                    title: '记录编号'
                }, {
                    field: 'memberName',
                    title: '会员'
                }, {
                    field: 'goodsName',
                    title: '兑换商品'
                }, {
                    field: 'pointsCost',
                    title: '消耗积分'
                }, {
                    field: 'createTime',
                    title: '兑换时间'
                }],
                height: 400,
                pageNumber: 1,
                pageSize: 10,
                pageList: [10, 20, 50],
                pagination: true,
                sidePagination: 'client'
            });
        });

        function refresh() {
            $('#table').bootstrapTable('refresh');
        }
    </script>
</head>
<body background="${pageContext.request.contextPath}/static/assets/images/tongji.jpeg">
<div class="panel panel-default">
    <div class="panel-body">
        <form class="form-inline">
            <label class="control-label">积分兑换记录（签到活动运营复盘）</label>
            <button onclick="refresh()" type="button" class="btn btn-default" style="float: right;">
                <span class="glyphicon glyphicon-refresh"></span> 刷新
            </button>
        </form>
    </div>
</div>
<div>
    <table id="table"></table>
</div>
</body>
</html>
