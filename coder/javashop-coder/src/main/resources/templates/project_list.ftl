<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >

<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            项目管理
        </h1>
        <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
            <li class="active">Here</li>
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">

        <div class="box">
            <div class="box-header">
                <button type="button" style="width: 80px" class="btn btn-block btn-success" onclick="addProject()">
                    新建项目
                </button>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>项目名称</th>
                        <th>包名</th>
                        <th>添加时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
            </div>
            <!-- /.box-body -->
        </div>

    </section>
    <!-- /.content -->
</div>


<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">新增项目</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form">
                    <div class="box-body">
                        <div class="form-group ">
                            <label for="domain">项目名称:</label>
                            <input type="text" class="form-control" autocomplete="false" name="project_name"
                                   placeholder="请输入项目名称" data-rule-required="true" data-msg-required="请输入项目名称">
                        </div>

                        <div class="form-group ">
                            <label for="domain">项目包名:</label>
                            <input type="text" class="form-control" autocomplete="false" name="package_name"
                                   placeholder="请输入项目包名">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="ok-btn">确定</button>
            </div>
        </div>
    </div>
</div>
<!-- Modal -->

<script>

    var url;
    var table;
    $(function () {


        table = $("#example1").DataTable({
            serverSide: true,
            "pageLength": 20,

            ajax: {
                url: '/data/project',
            },

            columns: [ //定义列

                {
                    data: "project_name"
                },
                {
                    data: "package_name"
                }
                ,
                {
                    data: "add_time_text"
                },
                {
                    data: null,
                    "render": function (data, type, row) {
                        var btnHtml = "";
                        btnHtml += '<div class="btn-group deploy_btn_box">'
                        btnHtml += '    <button type="button" class="btn btn-info">操作</button>'
                        btnHtml += '    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-expanded="false">'
                        btnHtml += '        <span class="caret"></span>'
                        btnHtml += '        <span class="sr-only">Toggle Dropdown</span>'
                        btnHtml += '    </button>'
                        btnHtml += '    <ul class="dropdown-menu" role="menu">'
                        btnHtml += '        <li><a href="javascript:editProject(' + data["id"] + ') " >编辑</a></li>'
                        btnHtml += '        <li><a href="javascript:delProject(' + data["id"] + ') " >删除</a></li>'
                        btnHtml += '        <li><a href="javascript:copyProject(' + data["id"] + ') " >复制</a></li>'
                        btnHtml += '        <li><a href="javascript:location.href=\'' + data["id"] + '/module/list\'" >进入</a></li>'
                        btnHtml += '    </ul>';
                        btnHtml += '</div>';

                        return btnHtml;
                    }
                }
            ]

        });


        /**
         * 发出添加请求
         */
        $("#ok-btn").click(function () {
            if ($("#user-form").valid()) {
                $("#user-form").ajaxSubmit({
                    url: url,
                    type: "post",
                    success: function () {
                        alert("保存成功");
                        $('#myModal').modal('hide');
                        reload();
                    }
                });
            }

        });


    });

    //添加项目
    function addProject() {
        url = "/data/project";
        $('#myModal').modal();

    }

    //编辑
    function editProject(project_id) {

        url = "/data/project/" + project_id;
        $('#myModal').modal();
        $.ajax({
            url: url,
            dataType: "json",
            success: function (user) {
                $("#user-form .form-control").each(function (i, el) {
                    var value = user[el.name];
                    $(el).val(value);
                });
            }
        });

    }


    //删除
    function delProject(project_id) {

        url = "/data/project/" + project_id;
        loadding();
        $.ajax({
            url: url,
            method: "delete",
            success: function (data) {
                $(".overlay").remove();
                $(".deploy_btn_box button").removeClass("disabled");
                alert("删除成功");
                reload();
            },
            error: function() {

                $(".overlay").remove();
                $(".deploy_btn_box button").removeClass("disabled");
                alert("删除失败");
            }
        });

    }

    function reload(){

        table.ajax.url("/data/project").load();
    }


    //复制一个新的，包括里面的数据库和表结构
    function copyProject(project_id) {

        if(!confirm("确定复制一个新的项目吗？")){

            return false;
        }

        loadding();
        $.ajax({
            url: "/data/project/" + project_id+"/copy",
            method: "POST",
            success: function (data) {
                $(".overlay").remove();
                $(".deploy_btn_box button").removeClass("disabled");
                alert("复制成功");
                reload();
            },
            error: function() {
                $(".overlay").remove();
                $(".deploy_btn_box button").removeClass("disabled");
                alert("复制失败");
            }
        });


    }

    function loadding() {
        $(".deploy_btn_box button").addClass("disabled");

        var overflow ="";
        overflow +='<div class="overlay">';
        overflow +='<i class="fa fa-refresh fa-spin"></i>'
        overflow +='</div>'
        var oEl  = $(overflow);
        $(".deploy_btn_box").append(oEl)

        return oEl;
    }


</script>
<script src="/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/plugins/select2/select2.full.min.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.numeric.extensions.js"></script>

<#include 'common/footer.ftl' />