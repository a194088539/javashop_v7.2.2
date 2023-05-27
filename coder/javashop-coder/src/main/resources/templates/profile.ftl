<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >

<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            修改个人资料
            <small>维护密码</small>
        </h1>
        <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Level</a></li>
            <li class="active">Here</li>
        </ol>
    </section>

    <!-- Main content -->
    <section class="content">
        <form role="form" id="user-form" >

            <div class="box box-default">
                <!-- /.box-header -->

                <div class="box-body">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label>用户名：</label>
                                ${user.username}
                            </div>


                            <div class="form-group">
                                <label>新密码：</label>
                                <input type="text" class="form-control amount" autocomplete="false"   name="password" placeholder="如果输入密码则会更改密码"   >

                            </div>
                            <div class="form-group">
                                <label>真实姓名：</label>
                            ${user.realname}
                            </div>

                            <div class="form-group">
                                <label>角色：</label>
                                ${user.role_name}
                            </div>

                        </div>

                    </div>
                    <!-- /.row -->
                </div>

                <!-- /.box-body -->
                <div class="box-footer">
                    <button type="button" id="update-btn" class="btn btn-block btn-primary" style="width: 150px;">保存</button>
                </div>
            </div>
        </form>
    </section>
    <!-- /.content -->
</div>



<script>
    $(function () {
       // $("#customer-form").validate();

        /**
         * 发出保存客户请求
         */
        $("#update-btn").click(function () {
//            if( !$("#user-form").valid() ) {
//                return false;
//            }
            $("#user-form").ajaxSubmit({
                url:"/data/adminuser/profile",
                type:"post",
                success:function () {
                    alert("保存成功")
                }
            }) ;
        });


    });
</script>

<#include 'common/footer.ftl' />