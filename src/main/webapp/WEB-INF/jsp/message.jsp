<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script src="${pageContext.request.contextPath}/static/jquery-3.2.1.min.js">
    window.onunload(alert("jin"))
</script>
<html>
<head>
    <title>留言板</title>
    <meta charset="UTF-8">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/main.css">
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
            getjson(1);
        });

        //删除
        function del1(id){
            swal({
                    title: "确定删除吗？",
                    text: "您将无法恢复！",
                    type: "warning",
                    showCancelButton: true,
                    confirmButtonColor: "#DD6B55",
                    confirmButtonText: "确定删除！",
                    cancelButtonText: "取消删除！",
                    closeOnConfirm: false,
                    closeOnCancel: false
                },
                function (isConfirm) {
                    if (isConfirm) {

                        swal("删除！", "删除成功",
                            "success");

                    } else {
                        swal("取消！", "您已取消删除",
                            "error");
                    }
                });
        }

        function save(){
            if(!validateAdd()){
                return;
            }
            var message = $("#message").val().trim();
            var isTop = $("#isTop").is(":checked") ? 1 : 0;

            $.post("${pageContext.request.contextPath}/message/insert", {
                'content': message,
                "publisher.id": ${user.id},
                'isTop': isTop
            }, function (releset) {
                if (releset != null) {
                    fresh()
                } else {
                    swal(
                        {
                            title: "留言失败",
                            type: "warning",
                            timer: 1500
                        }
                    )
                }
            })
        }
        function validateAdd() {
            $("#message").parent().find("span").remove();

            var message = $("#message").val().trim();
            if(message == null || message == ""){
                $("#message").parent().append("<span style='color:red'>请输入留言</span>");
                return false;
            }
            return true;
        }

        function getjson(i) {
            $.ajax( {
                type : "get",
                url : "query/"+i,
                dataType:"json",
                success : function(jsondata) {
                    $("#comments").empty();
                    var data = eval(jsondata);    //解析json
                    var length = data.length;   //json对象数
                    for(var i =0; i < length; i++){
                        var type = "";
                        if(data[i].publisher.userType === 0){
                            type = "管理员："
                        }else if(data[i].publisher.userType === 1){
                            type = "教练："
                        }else if(data[i].publisher.userType === 2){
                            type = "会员："
                        }
                        //循环添加div
                        var html =
                            "<div class='comment col-sm-offset-1'>" +
                            "<div class='comment-head '>" +
                            "<div class='row'>" +
                            "<div class='comment-name col-sm-3'>" +


                            type + data[i].publisher.realName +
                            "</div>" +
                            "<div class='comment-body col-sm-3'>" +
                            data[i].content +
                            "</div>" +
                            "<div class='comment-date col-sm-3 col-sm-offset-1'>" +
                            "时间：" + data[i].publishTime +
                            "</div>" +
                            "</div>" +
                            "</div>" +
                            "<div class='clear'></div>" +
                            "</div>";
                        $("#comments").append(html);
                    }
                }
            });
            //记录当前页，为上一页和下一页做准备
            currentpage = i;
        }

        function fresh() {
            window.location.reload();
        }
    </script>
</head>
<body>
<h1 class="col-sm-offset-1">留言板</h1>
<div class="container">
    <form>
        <div class="form-group">
            <div class="col-sm-8">
                <textarea style="resize: none;" rows="6" class="form-control" id="message"
                          parsley-trigger="change" parsley-required="true" parsley-minlength="4"
                          parsley-type="email" parsley-validation-minlength="1"></textarea>
            </div>
        </div>
    </form>
    <div style="margin-top: 10px">
        <label style="font-weight: normal; margin-right: 15px;">
            <input type="checkbox" id="isTop"> 置顶此公告（会员端优先展示）
        </label>
        <button onclick="save()" id="add" type="button" class="btn btn-primary">提交留言</button>
        <button onclick="fresh()" id="fresh" type="button" class="btn btn-primary">刷新留言板</button>
    </div>
</div>
<div class="allcomment col-sm-offset-1 col-sm-10" id="comments">

</div>
<div class="col-sm-offset-2">
    <ul class="pagination">
        <li><a id="page-before" href="#!">&laquo;</a></li>
        <c:forEach begin="1" end="${pageCount}" var="v" >
            <li><a class="pages" href="#!">${v}</a></li>
        </c:forEach>
        <li><a id="page-after" href="#!">&raquo;</a></li>
    </ul>
</div>
<script>
    //闭包循环为分页标签添加监听事件
    var pages = document.getElementsByClassName('pages');
    for (var i = 0; i < pages.length; i++) {
        (function(i){
            pages[i].onclick = function(){
                getjson(i+1);
            }
        })(i);
    }
    var pagebefore = document.getElementById("page-before");
    var pageafter = document.getElementById("page-after");
    var currentpage = 1;
    //上一页添加监听
    pagebefore.onclick = function(){
        if(currentpage != 1) {
            currentpage = currentpage - 1;
            getjson(currentpage);
        }else {
            swal("提示！", "当前页为第一页", "success");
        }

    }
    //下一页添加监听
    pageafter.onclick = function(){
        if(currentpage != ${pageCount}) {
            currentpage = currentpage + 1;
            getjson(currentpage);
        }else {
            swal("提示！", "当前页为最后一页", "success");
        }
    }
</script>
</body>
</html>
