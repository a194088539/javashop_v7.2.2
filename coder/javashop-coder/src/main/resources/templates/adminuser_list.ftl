<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >

<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            管理员管理
            <small>对管理员进行管理</small>
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
                <button type="button" style="width: 150px" class="btn btn-block btn-success"  id="new-btn">新建管理员</button>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>用户名</th>
                        <th>姓名</th>
                        <th>角色</th>
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
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">新增管理员</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form" >
                    <div class="box-body">
                        <div class="form-group " >
                            <label for="domain">用户名:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="username" placeholder="请输入用户名" data-rule-required="true"  data-msg-required="请输入用户名">
                        </div>

                        <div class="form-group " >
                            <label for="domain">密码:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="password" placeholder="请输入密码"  >
                        </div>


                        <div class="form-group">
                            <label>角色:</label>
                            <select name="role" class="form-control select2" style="width: 100%;">
                                <option value="administrator" selected="selected">管理员</option>
                                <option value="commonuser">普通用户</option>
                            </select>
                        </div>
                        <div class="form-group " >
                            <label for="domain">姓名:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="realname" placeholder="请输入姓名" data-rule-required="true"  data-msg-required="请输入姓名">
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

    $(function () {

        activeMenu("user-menu")
        $("#user-form").validate();

        $("#example1").DataTable({
            serverSide: true,
            "pageLength": 20,

            ajax: {
                url: '/data/adminusers',
            },

            columns: [ //定义列

                {
                    data: "username"
                },
                {
                    data: "realname"
                }
                ,
                {
                    data: "role_name"
                }
                ,
                {
                    data: null,
                    "render": function (data, type, row) {
                        var btnHtml = "";
                        btnHtml+='<div class="btn-group">'
                        btnHtml+='    <button type="button" class="btn btn-info">操作</button>'
                        btnHtml+='    <button type="button" class="btn btn-info dropdown-toggle" data-toggle="dropdown" aria-expanded="false">'
                        btnHtml+='        <span class="caret"></span>'
                        btnHtml+='        <span class="sr-only">Toggle Dropdown</span>'
                        btnHtml+='    </button>'
                        btnHtml+='    <ul class="dropdown-menu" role="menu">'
                        btnHtml+='        <li><a href="javascript:deleteUser('+data["userid"]+') " >删除</a></li>'
                        btnHtml+='        <li><a  href="javascript:editUser('+data["userid"]+')  ">修改</a></li>'
                        btnHtml+='    </ul>';
                        btnHtml+='</div>';

                        return btnHtml;
                    }
                }
            ]

        });



        /**
         * 打开新建对话框
         */
        $("#new-btn").click(function () {
            url="/data/adminuser"; //添加的url

            restForm();

            $('#myModal').modal();
        });


        /**
         * 发出添加请求
         */
        $("#ok-btn").click(function () {
            if( $("#user-form").valid() ){
            $("#user-form").ajaxSubmit({
                url:url,
                type:"post",
                success:function () {
                    alert("保存成功")
                    location.reload();
                }
            }) ;
            }

        });


    });


    //编辑用户
    function editUser(user_id) {
        url="/data/adminuser/"+user_id; //修改的url

        restForm();
        $('#myModal').modal();
        $.ajax({
            url:url,
            dataType:"json",
            success:function (user) {
                $("#user-form .form-control").each(function (i,el) {
                    if(el.name!='password'){
                        var value = user[el.name];
                        $(el).val(value);
                    }

                });
            }
        });
    }


    function restForm() {
        $("#user-form").get(0).reset();
        $("form .has-success").removeClass( "has-success" );
        $("form .has-error").removeClass( "has-error" );

    }
    /**
     * 删除用户
     * @param user_id 用户id
     * @returns {boolean}
     */
    function deleteUser(user_id){
        if( !confirm( "确认删除这个管理员吗？" ) ){
            return false;
        }

        $.ajax({
            url:"/data/adminuser/"+ user_id,
            type:"DELETE",
            success:function(){
                location.reload();
            }
        });
    }

</script>
<script src="/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/plugins/select2/select2.full.min.js"></script>


<#include 'common/footer.ftl' />