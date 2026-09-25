<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("appointmentManage"))
</script>
<html>
<head>
    <title>预约管理</title>
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
                url: '${pageContext.request.contextPath}/appointment/queryAll',
                method: 'post',
                contentType: "application/x-www-form-urlencoded",
                columns: [
                    { field: 'id', title: '预约编号', sortable: true },
                    { field: 'member.realName', title: '会员姓名' },
                    { field: 'coach.realName', title: '教练姓名' },
                    { field: 'subject.courseName', title: '课程' },
                    { field: 'appointmentDate', title: '预约日期', sortable: true },
                    { field: 'status', title: '状态',
                        formatter: function (value, row, index) {
                            if (row.status == 0) {
                                return "<span style='color:orange'>已预约</span>";
                            } else if (row.status == 1) {
                                return "<span style='color:green'>已打卡</span>";
                            } else {
                                return "<span style='color:gray'>已取消</span>";
                            }
                        }
                    },
                    { field: 'checkinTime', title: '打卡时间' },
                    { field: 'record.count', title: '剩余次数',
                        formatter: function (value, row) {
                            return row.record ? row.record.count + " 节" : "-";
                        }
                    },
                    { field: 'remark', title: '备注' },
                    { field: 'createTime', title: '创建时间' }
                ],
                height: 360,
                pageList: [5, 10, 15],
                pageNumber: 1,
                pageSize: 10,
                pagination: true
            });
        });

        function refresh() {
            $('#dg').bootstrapTable('refresh');
        }

        function openCreate() {
            $('#createModal').modal('show');
        }

        function loadMemberRecords() {
            var memberId = $('#memberSel').val();
            if (!memberId) {
                $('#recordSel').html('<option value="">请先选择会员</option>');
                return;
            }
            $.post("${pageContext.request.contextPath}/appointment/memberRecords",
                { memberId: memberId },
                function (list) {
                    var html = '<option value="">--请选择--</option>';
                    $(list).each(function () {
                        var txt = "记录#" + this.id + " - " + this.subject.courseName
                                + " - " + this.coach.realName + " - 剩余" + this.count + "节";
                        html += '<option value=' + this.id + '>' + txt + '</option>';
                    });
                    $('#recordSel').html(html);
                }
            );
        }

        function submitCreate() {
            var recordId = $('#recordSel').val();
            var date = $('#apptDate').val();
            var remark = $('#apptRemark').val();
            if (!recordId) { swal("提示", "请选择私教购买记录", "warning"); return; }
            if (!date) { swal("提示", "请选择预约日期", "warning"); return; }

            $.post("${pageContext.request.contextPath}/appointment/create",
                { recordId: recordId, appointmentDate: date, remark: remark },
                function (data) {
                    if (data.code == 0) {
                        swal("成功", data.msg, "success");
                        $('#createModal').modal('hide');
                        refresh();
                    } else {
                        swal("失败", data.msg, "error");
                    }
                }
            );
        }
    </script>
</head>
<body background="${pageContext.request.contextPath}/static/HTmoban/images/tongji.jpeg">
    <div class="panel panel-default">
        <div class="panel-body">
            <form class="form-inline">
                <label>预约管理(管理员可代会员发起预约)</label>
                <button onclick="openCreate()" type="button" class="btn btn-primary" style="float:right;">
                    <span class="glyphicon glyphicon-plus"></span> 新增预约
                </button>
                <button onclick="refresh()" type="button" class="btn btn-default" style="float:right;margin-right:10px;">
                    <span class="glyphicon glyphicon-refresh"></span> 刷新
                </button>
            </form>
        </div>
    </div>
    <div>
        <table id="dg"></table>
    </div>

    <div class="modal fade" id="createModal">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title">新增课程预约</h4>
                </div>
                <div class="modal-body">
                    <form>
                        <div class="form-group">
                            <label>选择会员:</label>
                            <select id="memberSel" class="form-control" onchange="loadMemberRecords()">
                                <option value="">--请选择--</option>
                                <c:forEach items="${members}" var="m">
                                    <option value="${m.id}">${m.realName}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>选择私教购买记录:</label>
                            <select id="recordSel" class="form-control">
                                <option value="">请先选择会员</option>
                            </select>
                        </div>
                        <div class="form-group">
                            <label>预约上课日期:</label>
                            <input type="date" id="apptDate" class="form-control">
                        </div>
                        <div class="form-group">
                            <label>备注:</label>
                            <input type="text" id="apptRemark" class="form-control" placeholder="可选">
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button onclick="submitCreate()" type="button" class="btn btn-primary">提交</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
