<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >

<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            ${project.project_name }/<span id="branch-name">master</span>
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
                分支：<select id="select_version" onchange="changeVersion()" style="width: 80px;height:33px;display: inline;margin-top: 0;">
                    <option value = '0' >master</option>
                    <#list versionList as version>
                        <option value="${version.id}">${version.version}</option>
                    </#list>
                </select>
                <button type="button" style="width: 80px;display: inline;" class="btn btn-block btn-success"
                        onclick="addProject()">新建模块
                </button>
                <button type="button" style="width: 80px;display: inline;margin-top: 0;"
                        class="btn btn-block btn-success" onclick="exportPdm()">导出pdm
                </button>
                <button type="button" style="width: 80px;display: inline;margin-top: 0;"
                        class="btn btn-block btn-warning" onclick="createnew()">新建分支
                </button>


            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>模块名称</th>
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
                <h4 class="modal-title" id="myModalLabel">新增模块</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form">
                    <div class="box-body">
                        <div class="form-group ">
                            <label for="domain">模块名称:</label>
                            <input type="text" class="form-control" autocomplete="false" name="module_name"
                                   placeholder="请输入模块名称" data-rule-required="true" data-msg-required="请输入模块名称">
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

<div class="modal fade" id="modal_create_branch" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="myModalLabel">新建分支</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form-branch">
                    <div class="box-body">
                        <div class="form-group ">
                            <label for="domain">起点:</label>
                            <select name = "from">
                                <option value = '0' >master</option>
                                <#list versionList as version>
                                    <option value="${version.id}">${version.version}</option>
                                </#list>
                            </select>
                        </div>
                        <div class="form-group ">
                            <label for="domain">新分支名称:</label>
                            <input type="text" class="form-control" autocomplete="false" name="version"
                                   placeholder="请输入新分支名称" data-rule-required="true" data-msg-required="请输入新分支名称">
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="ok-btn-branch">确定</button>
            </div>
        </div>
    </div>
</div>

<script>

    var url;
    var table;
    $(function () {

        table = $("#example1").DataTable({
            serverSide: true,
            "pageLength": 20,

            ajax: {
                url: '/data/project/${project_id}/0/module',
            },

            columns: [ //定义列

                {
                    data: "module_name"
                },
                {
                    data: null,
                    "render": function (data, type, row) {
                        var btnHtml = "";
                        btnHtml += '<div class="btn-group">'
                        btnHtml += '    <button type="button" class="btn btn-info">操作</button>'
                        btnHtml += '    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-expanded="false">'
                        btnHtml += '        <span class="caret"></span>'
                        btnHtml += '        <span class="sr-only">Toggle Dropdown</span>'
                        btnHtml += '    </button>'
                        btnHtml += '    <ul class="dropdown-menu" role="menu">'
                        btnHtml += '        <li><a href="javascript:editProject(' + data["module_id"] + ') " >编辑</a></li>'
                        btnHtml += '        <li><a href="javascript:deleteProject(' + data["module_id"] + ') " >删除</a></li>'
                        btnHtml += '        <li><a href="javascript:inmodule(' + data["module_id"] + ')" >进入</a></li>'
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
                        var value = $("#select_version").val();
                        table.ajax.url('/data/project/${project_id}/'+value+'/module').load();
                    }
                });
            }

        });

        /**
         * 发出创建新分支请求
         */
        $("#ok-btn-branch").click(function () {
            if ($("#user-form-branch").valid()) {
                $("#user-form-branch").ajaxSubmit({
                    url: "/data/project/${project_id}/version",
                    type: "post",
                    success: function () {
                        alert("保存成功");
                        $('#modal_create_branch').modal('hide');
                        location.reload();
                    }
                });
            }

        });


    });

    //添加项目
    function addProject() {
        var value = $("#select_version").val();
        url = "/data/project/${project_id}/"+value+"/module";
        $('#myModal').modal();

    }

    function inmodule(module_id){
        var value = $("#select_version").val();
        location.href='/view/module/' + module_id +'/'+value+'/model';
    }

    //新建一个项目的分支
    function createnew(){

        $('#modal_create_branch').modal();

    }

    function changeVersion(){

        var value = $("#select_version").val();

        var text = $("#select_version").find("option:selected").text();

        table.ajax.url('/data/project/${project_id}/'+value+'/module').load();

        $("#branch-name").text(text);
    }

    //编辑
    function editProject(module_id) {

        var value = $("#select_version").val();
        url = "/data/project/${project_id}/"+value+"/module/" + module_id;
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
    function deleteProject(module_id) {

        if (!confirm("确认删除这个模块吗？")) {
            return false;
        }

        $.ajax({
            url: "/data/project/${project_id}/0/module/" + module_id,
            type: "DELETE",
            success: function () {
                location.reload();
            }
        });

    }

    //导出pdm
    function exportPdm() {
        var value = $("#select_version").val();
        location.href = "/data/project/${project_id}/pdm?version_id="+value;
    }


</script>
<script src="/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/plugins/select2/select2.full.min.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.numeric.extensions.js"></script>

<#include 'common/footer.ftl' />