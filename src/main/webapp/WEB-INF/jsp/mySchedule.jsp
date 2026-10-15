<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("mySchedule"))
</script>
<html>
<head>
    <title>我的课程安排</title>
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
            var today = new Date();
            var y = today.getFullYear();
            var m = String(today.getMonth() + 1).padStart(2, '0');
            var d = String(today.getDate()).padStart(2, '0');
            $('#today').val(y + '-' + m + '-' + d);

            $('#startDate').datetimepicker({format: 'YYYY-MM-DD'});
            $('#endDate').datetimepicker({format: 'YYYY-MM-DD'});

            var weekAgo = new Date(today.getTime() - 7*24*3600*1000);
            var wy = weekAgo.getFullYear();
            var wm = String(weekAgo.getMonth()+1).padStart(2,'0');
            var wd = String(weekAgo.getDate()).padStart(2,'0');
            $('#startDate').val(wy+'-'+wm+'-'+wd);
            $('#endDate').val(y+'-'+m+'-'+d);

            loadData();
        });

        function loadData(){
            $.post("${pageContext.request.contextPath}/appointment/myAppointments",{
                "startDate":$('#startDate').val(),
                "endDate":$('#endDate').val()
            },function (list) {
                $("#dg").bootstrapTable('load', list);
            })
        }

        function search(){
            loadData();
        }

        function resetToday(){
            var today = new Date();
            var y = today.getFullYear();
            var m = String(today.getMonth() + 1).padStart(2, '0');
            var d = String(today.getDate()).padStart(2, '0');
            var s = y+'-'+m+'-'+d;
            $('#startDate').val(s);
            $('#endDate').val(s);
            loadData();
        }

        function resetWeek(){
            var today = new Date();
            var y = today.getFullYear();
            var m = String(today.getMonth() + 1).padStart(2, '0');
            var d = String(today.getDate()).padStart(2, '0');
            var weekAgo = new Date(today.getTime() - 7*24*3600*1000);
            var wy = weekAgo.getFullYear();
            var wm = String(weekAgo.getMonth()+1).padStart(2,'0');
            var wd = String(weekAgo.getDate()).padStart(2,'0');
            $('#startDate').val(wy+'-'+wm+'-'+wd);
            $('#endDate').val(y+'-'+m+'-'+d);
            loadData();
        }

        function checkin(appointmentId, name){
            swal({
                title: "确认打卡？",
                text: "将为会员【"+name+"】完成本次预约并扣减1节次数",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#5cb85c",
                confirmButtonText: "确认打卡",
                cancelButtonText: "取消",
                closeOnConfirm: false,
                closeOnCancel: true
            },
            function(isConfirm){
                if(isConfirm){
                    $.post('${pageContext.request.contextPath}/appointment/checkin',
                        { appointmentId: appointmentId },
                        function(data){
                            if(data.code == 0){
                                swal("打卡成功", data.msg, "success");
                                loadData();
                            }else{
                                swal("打卡失败", data.msg, "error");
                            }
                        }
                    );
                }
            });
        }

        function cancel(appointmentId, name){
            swal({
                title: "确认取消？",
                text: "将取消会员【"+name+"】的本次预约(不扣次数)",
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#d9534f",
                confirmButtonText: "确认取消",
                cancelButtonText: "返回",
                closeOnConfirm: false,
                closeOnCancel: true
            },
            function(isConfirm){
                if(isConfirm){
                    $.post('${pageContext.request.contextPath}/appointment/cancel',
                        { appointmentId: appointmentId },
                        function(data){
                            if(data.code == 0){
                                swal("已取消", data.msg, "success");
                                loadData();
                            }else{
                                swal("操作失败", data.msg, "error");
                            }
                        }
                    );
                }
            });
        }

        $(function(){
            $('#dg').bootstrapTable({
                data: [],
                columns: [
                    { field:'id',title:'预约编号',sortable: true},
                    { field:'appointmentDate',title:'预约日期',sortable: true,
                        formatter:function(value,row){
                            var t = row.appointmentDate;
                            var today = $('#today').val();
                            if(t == today){
                                return "<span style='color:red;font-weight:bold;'>"+t+" (今天)</span>";
                            }
                            return t;
                        }
                    },
                    { field:'member.realName',title:'会员姓名',sortable: true},
                    { field:'member.phone',title:'会员电话'},
                    { field:'subject.courseName',title:'课程'},
                    { field:'status',title:'状态',
                        formatter:function(value,row){
                            if(row.status == 0){
                                return "<span style='color:orange'>已预约</span>";
                            }else if(row.status == 1){
                                return "<span style='color:green'>已打卡</span> ("+row.checkinTime+")";
                            }else{
                                return "<span style='color:gray'>已取消</span>";
                            }
                        }
                    },
                    { field:'record.count',title:'剩余次数',
                        formatter:function(value,row){
                            if(row.record){
                                return row.record.count + " 节";
                            }
                            return "-";
                        }
                    },
                    { field:'remark',title:'备注'},
                    { field:'xx',title:'操作',
                        formatter:function(value,row,index){
                            if(row.status == 0){
                                return "<button class='btn btn-success btn-xs' onclick='checkin("+row.id+",\""+row.member.realName+"\")'>"
                                    + "<span class='glyphicon glyphicon-ok'></span> 打卡</button> "
                                    + "<button class='btn btn-danger btn-xs' onclick='cancel("+row.id+",\""+row.member.realName+"\")'>"
                                    + "<span class='glyphicon glyphicon-remove'></span> 取消</button>";
                            }else if(row.status == 1){
                                return "<span style='color:green;'>已完成</span>";
                            }else{
                                return "<span style='color:gray;'>已取消</span>";
                            }
                        }
                    }
                ],
                height: 420,
                pageList: [5, 10, 15, 20],
                pageNumber: 1,
                pageSize: 10,
                pagination: true
            });
        });
    </script>
</head>
<body background="${pageContext.request.contextPath}/static/assets/images/tongji.jpeg">
    <input type="hidden" id="today">
    <div class="panel panel-default">
        <div class="panel-body">
            <form class="form-inline">
                <div class="form-group">
                    <label>日期范围:</label>
                    <input type="text" id="startDate" class="form-control" placeholder="开始日期">
                    <span> 至 </span>
                    <input type="text" id="endDate" class="form-control" placeholder="结束日期">
                </div>
                <button onclick="search()" type="button" class="btn btn-primary">查询</button>
                <button onclick="resetToday()" type="button" class="btn btn-info">今日</button>
                <button onclick="resetWeek()" type="button" class="btn btn-success">本周</button>
                <button onclick="$('#startDate').val('');$('#endDate').val('');loadData();"
                        type="button" class="btn btn-default">全部</button>
                <button onclick="loadData()" type="button" class="btn btn-default" style="float:right;">
                    <span class="glyphicon glyphicon-refresh"></span> 刷新
                </button>
            </form>
            <div style="margin-top:8px;color:#666;">
                提示:此列表展示会员预约您的单次课程,打卡后该预约标记为「已打卡」,无法再次打卡;取消则不扣减次数。
            </div>
        </div>
    </div>
    <div>
        <table id="dg"></table>
    </div>
</body>
</html>
