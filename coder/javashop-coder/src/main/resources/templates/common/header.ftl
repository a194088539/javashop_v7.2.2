<!DOCTYPE html>
<!--
This is a starter template page. Use this page to start your new project from
scratch. This page gets rid of all links and provides the needed markup only.
-->
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>易族代码生成器</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.5 -->
    <link rel="stylesheet" href="/bootstrap/css/bootstrap.min.css">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="../../dist/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="/dist/css/ionicons.min.css">
    <link rel="stylesheet" href="/plugins/select2/select2.min.css">


    <!-- Theme style -->
    <link rel="stylesheet" href="/dist/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. We have chosen the skin-blue for this starter
          page. However, you can choose any other skin. Make sure you
          apply the skin class to the body tag so the changes take effect.
    -->
    <link rel="stylesheet" href="/dist/css/skins/skin-blue.min.css">

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="https://oss.maxcdn.com/html5shiv/3.7.3/html5shiv.min.js"></script>
    <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>

    <!-- jQuery 2.1.4 -->
    <script src="/plugins/jQuery/jQuery-2.1.4.min.js"></script>
    <script src="/plugins/form/jquery.form-3.51.js"></script>
    <script src="/plugins/jqueryvalidate/jquery.validate.min.js"></script>
    <!-- Bootstrap 3.3.5 -->
    <script src="/bootstrap/js/bootstrap.min.js"></script>
    <!-- AdminLTE App -->
    <script src="/dist/js/app.min.js"></script>

    <!-- DataTables -->
    <script src="/plugins/datatables/jquery.dataTables.min.js"></script>
    <script src="/plugins/datatables/dataTables.bootstrap.min.js"></script>

    <script>
function activeMenu(menuid) {
    $(".sidebar-menu .active").removeClass("active");
    $("#"+menuid).addClass("active");
}

//异步全局设置
$.ajaxSetup( {
    error: function(e){
        if( e.responseJSON.error_code=='not_login'){
            alert("会话超时");
            $('#loginModal').modal();

            //location.href="/index.html";
        }else {
            alert(e.responseJSON.error_message);
        }

    }
} );

//校验全局设置
$.validator.setDefaults({
    errorPlacement: function ( error, element ) {

        element.parents( ".form-group" ).addClass( "has-error" ).removeClass( "has-success" );
        error.insertAfter( element );

    },
    unhighlight: function ( element, errorClass, validClass ) {
        $( element ).parents( ".form-group" ).addClass( "has-success" ).removeClass( "has-error" );
    }
});

$(function () {

    $("#code-img").click(function () {
        $(this).attr("src","/image/code");
    })

    //登录
    $("#login-btn").click(function () {
        $("#login-form").ajaxSubmit({
            url:"/user/login",
            type:"post",
            success:function () {
                $('#loginModal').modal('hide');
            }
        }) ;
    })

    //登出
    $("#logout-btn").click(function () {
        $("#login-form").ajaxSubmit({
            url:"/user/logout",
            type:"post",
            success:function () {
                location.href="/index.html";
            }
        }) ;
    })
});



    </script>
    <![endif]-->
</head>

<body class="hold-transition skin-blue sidebar-mini">


<!-- Modal -->
<div class="modal fade" id="loginModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-body">

                <div class="login-box">
                    <div class="login-logo">
                        <a href="/index.html"><b>Java</b>shop</a>
                    </div>
                    <!-- /.login-logo -->
                    <div class="login-box-body">
                        <p class="login-box-msg">Sign in to start your session</p>

                        <form  id="login-form" method="post">
                            <div class="form-group has-feedback">
                                <input type="text" class="form-control" name="username" placeholder="用户名">
                                <span class="glyphicon glyphicon-envelope form-control-feedback"></span>
                            </div>
                            <div class="form-group has-feedback">
                                <input type="password" class="form-control" name="password" placeholder="密码">
                                <span class="glyphicon glyphicon-lock form-control-feedback"></span>
                            </div>

                            <div class="form-group has-feedback">
                                <input type="text" name="code" class="form-control" placeholder="验证码">
                                <span class="glyphicon glyphicon-picture form-control-feedback"></span>
                            </div>


                            <div class="row">
                                <div class="col-xs-8">
                                    <img src="/image/code" style="height: 30px ;" id="code-img">

                                </div>
                                <!-- /.col -->
                                <div class="col-xs-4">
                                    <button id="login-btn" type="button" class="btn btn-primary btn-block btn-flat">Sign In</button>
                                </div>
                                <!-- /.col -->
                            </div>
                        </form>
                        <!-- /.social-auth-links -->

                    </div>
                    <!-- /.login-box-body -->
                </div>
                <!-- /.login-box -->


            </div>
        </div>
    </div>
</div>

<div class="wrapper">
    <!-- Main Header -->
    <header class="main-header">

        <!-- Logo -->
        <a href="index2.html" class="logo">
            <!-- mini logo for sidebar mini 50x50 pixels -->
            <span class="logo-mini"><b>Java</b>Shop</span>
            <!-- logo for regular state and mobile devices -->
            <span class="logo-lg"><b>Java</b>Shop</span>
        </a>

        <!-- Header Navbar -->
        <nav class="navbar navbar-static-top" role="navigation">
            <!-- Sidebar toggle button-->
            <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
                <span class="sr-only">Toggle navigation</span>
            </a>
            <!-- Navbar Right Menu -->
            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <!-- User Account Menu -->
                    <li class="dropdown user user-menu">
                        <!-- Menu Toggle Button -->
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                            <!-- The user image in the navbar-->
                            <img src="/dist/img/default-face.png" class="user-image" alt="User Image">
                            <!-- hidden-xs hides the username on small devices so only the image appears. -->
                            <span class="hidden-xs">${user.realname!''}</span>
                        </a>
                        <ul class="dropdown-menu">
                            <!-- The user image in the menu -->
                            <li class="user-header">
                                <img src="/dist/img/default-face.png" class="img-circle" alt="User Image">

                                <p>
                                ${user.realname}- ${user.role_name}
                                </p>
                            </li>
                            <!-- Menu Footer-->
                            <li class="user-footer">
                                <div class="pull-left">
                                    <a href="/view/adminuser/profile" class="btn btn-default btn-flat">Profile</a>
                                </div>
                                <div class="pull-right">
                                    <a href="#" id="logout-btn" class="btn btn-default btn-flat">Sign out</a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <!-- Control Sidebar Toggle Button -->
                    <li>
                        <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
                    </li>
                </ul>
            </div>
        </nav>
    </header>

    <aside class="main-sidebar">

        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">

            <!-- Sidebar user panel (optional) -->
            <div class="user-panel">
                <div class="pull-left image">
                    <img src="/dist/img/default-face.png" class="img-circle" alt="User Image">
                </div>
                <div class="pull-left info">
                    <p>${user.realname}</p>
                    <!-- Status -->
                    <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
                </div>
            </div>



            <!-- Sidebar Menu -->
            <ul class="sidebar-menu">
                <!-- Optionally, you can add icons to the links -->
                <li id="customer-menu" class="active"><a href="/view/project/list"><i class="fa fa-users"></i> <span>项目列表</span></a></li>
                <#if user.role == "administrator">
                	<li id="user-menu"><a href="/view/adminuser/list"><i class="fa fa-cog"></i> <span>管理员</span></a></li>
                </#if>
                
            </ul>
            <!-- /.sidebar-menu -->
        </section>
        <!-- /.sidebar -->
    </aside>