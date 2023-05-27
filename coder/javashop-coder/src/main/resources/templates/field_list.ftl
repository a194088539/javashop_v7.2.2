<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >

<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1>
            ${project.project_name }/<span id = "branch-name">${version.version}</span>/${module.module_name }/${dataModel.model_name }(${dataModel.table_name})
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
                分支：
                <select id="select_version" onchange="changeVersion()" style="width: 80px;height:33px;display: inline;margin-top: 0;">
                    <option value = '0' >master</option>
                    <#list versionList as version>
                        <option value="${version.id}"<#if version.id == versionId>selected="true"</#if>>${version.version}</option>
                    </#list>
                </select>
                <button type="button" style="width: 80px;display: inline;" class="btn btn-block btn-success" onclick="addProject()">新建字段</button>
            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>字段名</th>
                        <th>提示文字</th>
                        <th>类型</th>
                        <th>长度</th>
                        <th>是否主键</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    </tbody>
                </table>
                <button type="button" onclick="generateSql()" class="btn btn-info">生成建表sql</button>
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
                <h4 class="modal-title" id="myModalLabel">新增模型</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form" >
                    <div class="box-body">
                        <div class="form-group " >
                            <label for="domain">字段名:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="english_name" placeholder="请输入字段名称" data-rule-required="true"  data-msg-required="请输入项目名称">
                        </div>
                        <div class="form-group " >
                            <label for="domain">提示文字:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="china_name" placeholder="请输入提示文字"  >
                        </div>
                        <div class="form-group " >
                            <label for="domain">类型:</label>
                            <select name="data_type" class="form-control">
                            	<#list dataTypes?keys as key>
                            		<option value="${key}">${dataTypes["${key}"]}</option>
                            	</#list>
                            </select>
                        </div>
                        <div class="form-group " >
                            <label for="domain">长度:</label>
                            <input type="text" class="form-control"  autocomplete="false"   name="data_size" placeholder="请输入类型长度"  >
                         	<font color="red">提示：长度用于生成建表sql脚本使用，字符串和整数直接填写整数数值，浮点型需要长度和小数后几位例10,2</font>
                        </div>
                        <div class="form-group " >
                            <label for="domain">是否主键:</label>
                            
                            <div>
                                <label class="radio-inline">
							        <input type="radio" name="is_primary" id="optionsRadios3" value="1" > 是
							    </label>
							    <label class="radio-inline">
							        <input type="radio" name="is_primary" id="optionsRadios4"  value="0" checked> 否
							    </label>
                            </div>
                        </div>
                        <div class="form-group " >
                            <label for="domain">校验:</label>
                            <div>
							    <label class="radio-inline">
							        <input type="radio" name="validate_items" value="notempty"> 必填
							    </label>
							    <label class="radio-inline">
							        <input type="radio" name="validate_items" value="number"> 数字
							    </label>
							    <label class="radio-inline">
							        <input type="radio" name="validate_items" value="email"> 邮件
							    </label>
							    <button type="button" class="btn btn-primary btn-xs" onclick="clearitems()">清空</button>
							</div>
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
<!-- Modal -->
<div class="modal fade" id="sqlModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">sql语句</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form" >
                    <div class="box-body">
                        <div class="form-group " >
<!--                             <label for="domain">字段名:</label> -->
                            <textarea class="form-control" id="sqltext" rows="10"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
		                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
		            </div>
                </form>
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
                url: '/data/project/model/${model_id}/${versionId}/field',
            },

            columns: [ //定义列

                {
                    data: "english_name"
                },
                {
                    data: "china_name"
                },
                {
                    data: "data_type_text"
                },
                {
                    data: "data_size"
                },
                {
                    data: null,
                    "render": function (data, type, row) {
						if(data["is_primary"]==1){
							return "是";
						}else{
							return "否"
						}
                    }
                },
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
                        btnHtml+='        <li><a href="javascript:editField('+data['field_id']+') " >编辑</a></li>'
                        btnHtml+='        <li><a href="javascript:deleteField('+data['field_id']+')" >删除</a></li>'
                        btnHtml+='    </ul>';
                        btnHtml+='</div>';

                        return btnHtml;
                    }
                }
            ]

        });

		
		/**
         * 发出添加请求
         */
        $("#ok-btn").click(function () {
            var value = $("#select_version").val();
            if( $("#user-form").valid() ){
            $("#user-form").ajaxSubmit({
                url:url+"?version_id="+value,
                type:"post",
                success:function () {
                    alert("保存成功")
					$('#myModal').modal("hide");
                    var value = $("#select_version").val();
                    table.ajax.url("/data/project/model/${model_id}/"+value+"/field").load();
                }
            }) ;
            }

        });

    });

	//添加模型
	function addProject(){
		url = "/data/project/model/${model_id}/field";
		$('#myModal').modal();
	
	}
	
	//编辑
	function editField(field_id){
	
		url = "/data/project/model/${model_id}/field/"+field_id;
		$('#myModal').modal();
		
		$.ajax({
            url:url,
            dataType:"json",
            success:function (user) {
                $("#user-form .form-control").each(function (i,el) {
                    var value = user[el.name];
                    $(el).val(value);
                });
                //是否主键
                $("input[name='is_primary'][value="+user['is_primary']+"]").attr("checked","checked");
                $("input[name='validate_items']:checked").attr("checked",false);
                if(user['validate_items']){
	                $("input[name='validate_items'][value="+user['validate_items']+"]").attr("checked",true);
                }
            }
        });
	
	}
	
	//删除字段
	function deleteField(field_id){
		
		if( !confirm( "确认删除这个字段吗？" ) ){
            return false;
        }

        $.ajax({
            url:"/data/project/model/${model_id}/field/"+field_id,
            type:"DELETE",
            success:function(){
                var value = $("#select_version").val();
                table.ajax.url("/data/project/model/${model_id}/"+value+"/field").load();
            }
        });
	}

	//生成建表sql
	function generateSql(){
		$('#sqlModal').modal();
		$.ajax({
            url:"/data/project/model/${model_id}/sql",
            success:function (sql) {
            	$("#sqltext").text(sql);
            }
        });
	}

    //切换分支
    function changeVersion(){

        var value = $("#select_version").val();

        var text = $("#select_version").find("option:selected").text();

        table.ajax.url('/data/project/model/${model_id}/'+value+'/field').load();

        $("#branch-name").text(text);
    }
	
	function clearitems(){
		 $("input[name='validate_items']:checked").attr("checked",false);
	}
</script>
<script src="/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/plugins/select2/select2.full.min.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.numeric.extensions.js"></script>

<#include 'common/footer.ftl' />