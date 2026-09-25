<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("jin"))
</script>
<html>
<head>
    <title>会员列表</title>
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
            $('#upsrdata').datetimepicker({
                //viewMode: 'day',
                format: 'MM-DD'
            });
            $('#dg').bootstrapTable({
                url:'${pageContext.request.contextPath}/member/query2',
                method:'post',
                contentType:"application/x-www-form-urlencoded",
                columns:[
                    { field:'id',title:'会员编号',sortable: true},
                    { field: 'realName',title:'名称',sortable: true},
                    { field:'phone',title:'电话',sortable: true},
                    { field:'sex',title:'性别',
                        formatter:function (value,row,index) {
                            if(row.sex==0){
                                return "男";
                            }else{
                                return "女";
                            }
                        }
                    },
                    { field:'age',title:'年龄',sortable: true},
                    { field:'memberBirthday',title:'生日',sortable: true},
                    { field:'memberType.typeName',title:'卡类型'},
                    { field:'memberDate',title:'录入日期'},
                    { field: 'status',title:'会员状态',
                        formatter:function (value,row,index) {
                            if(row.remainingDays!=null && row.remainingDays>0){
                                return "正常"
                            }else{
                                return "<p style='color:red'>请续卡</p>"
                            }
                        }
                    },
                    { field: 'remainingDays',title:'剩余天数',
                        formatter:function (value,row,index) {
                            if(row.remainingDays==null || row.remainingDays<=0){
                                return "<p style='color:red'>已过期</p>"
                            }else if(row.remainingDays<7){
                                return "<p style='color:red'>"+row.remainingDays+"天(快到期)</p>";
                            }else{
                                return row.remainingDays+"天";
                            }
                        }
                    },
                    { field:'memberXufei',title:'到期日期'},
                    /*{
                        field:'xx',title:'操作',
                        formatter : function(value, row, index) {
                            return "<a title='删除' href='javascript:del("
                                + row.memberId + ")'><span class='glyphicon glyphicon-trash'></span></a> | <a> <span class='glyphicon glyphicon-pencil'></span></a>";
                        }

                    }*/
                ],
                queryParamsType:'',
                queryParams:queryParams,
                height:360,
                pageList:[5,10,15],
                pageNumber:1,
                pageSize:5,
                pagination:true,
                sidePagination:'server',
                /*onDblClickRow:function(row,$element,field) {
                    $('#updateeModal').modal('show');
                    $.post("${pageContext.request.contextPath}/ktype/query",{},function (releset) {
                        var e=releset;
                        var tt ="";
                        $(e).each(function () {
                            tt += "<option value='"+this.id+"'>"+""+this.typeName+"</option>";
                            $('#upoptype').html(tt);
                        })
                    }),
                        $('#upname').val(row.memberName);
                    $('#upphone').val(row.memberPhone);
                    $('#upsex').val(row.memberSex);
                    $('#upsrdata').val(row.birthday);
                    $('#upoptype').val(row.memberType.id);
                    $('#updata').val(row.nenberDate);
                    $('#upage').val(row.memberAge);
                    $('#upid').val(row.memberId);
                }*/
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


            $.post("${pageContext.request.contextPath}/member/query2",{"pageSize":opt.pageSize,"pageNumber":opt.pageNumber,"hyname":hyid,"ktype":ktype},function (releset) {
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
            </div>
            <div  class="input-group input-daterange">
                <label for="ktype" class="control-label">卡型:</label>
                <select class="form-control" id="ktype">
                    <option value="0">请选择</option>
                </select>
            </div>
            <button onclick="search()" type="button" class="btn btn-default" style="margin-top: 20px" >查询</button>
            <button type="button" id="download" style="margin-left:20px;" id="btn_download" class="btn btn-primary" onClick ="$('#dg').tableExport({ type: 'excel', escape: 'false' })">数据导出</button>
             <%--<button type="button" class="btn btn-default" style="float: right; margin-top: 20px" data-toggle="modal" onclick="insert()"><span class="glyphicon glyphicon-plus"></span>添加会员</button>--%>
        </form>
    </div>

</div>
    <%--//页面数据展示--%>
    <div>
        <table id="dg"></table>
    </div>

</body>
</html>
