<#include 'common/header.ftl' />
<#assign dateformat = "com.enation.framework.directive.DateformateDirective"?new() >
<style>
    .fileinput-button input {
        position: absolute;
        top: 0;
        left: 220px;
        margin: 0;
        opacity: 0;
        -ms-filter: 'alpha(opacity=0)';
        font-size: 35px !important;
        direction: ltr;
        cursor: pointer;
    }

</style>
<script src="/plugins/jqueryupload/jquery.ui.widget.js"></script>
<script src="/plugins/jqueryupload/jquery.fileupload.js"></script>
<div class="content-wrapper">
    <!-- Content Header (Page header) -->
    <section class="content-header">
        <h1 id = "tool-name">
            ${project.project_name }/<span id = "branch-name">${version.version}</span>/${module.module_name }
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
                        <option value="${version.id}"<#if version.id == version_id>selected="true"</#if>>${version.version}</option>
                    </#list>
                </select>

                <button type="button" style="width: 80px" class="btn  btn-success" onclick="addProject()">新建模型</button>


                <span class="btn btn-success fileinput-button">
                                <i class="glyphicon glyphicon-plus"></i>
                                <span>导入PDM</span>
                                <input type="file" name="file" id="fileupload" data-url="/file/upload/pdm">
                             </span>


            </div>
            <!-- /.box-header -->
            <div class="box-body">
                <table id="example1" class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>模型名称</th>
                        <th>英文名</th>
                        <th>数据表名</th>
                        <th>版本号</th>
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
                <h4 class="modal-title" id="myModalLabel">新增模型</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="user-form" >
                    <div class="box-body">
                        <div class="form-group " >
                            <label for="domain">模型名称:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="model_name" placeholder="请输入模型名称" data-rule-required="true"  data-msg-required="请输入项目名称">
                        </div>
                        <div class="form-group " >
                            <label for="domain">版本号:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="version" placeholder="请输入模型版本号" data-rule-required="true"  data-msg-required="请输入项目名称">
                        </div>
                        <div class="form-group " id="module">
                            <label for="domain">模块:</label>
                            <select name="module_id" class="form-control">
                            	<#list moduleList as module>
                            		<option value="${module.module_id}">${module.module_name}</option>
                            	</#list>
                            </select>
                        </div>
                        <div class="form-group " >
                            <label for="domain">英文名:</label>
                            <input type="text" class="form-control" autocomplete="false"   name="english_name" placeholder="请输入模型英文"  >
                         	<font color="red">提示：英文名会生成表名，多个英文组成的名字需要遵循驼峰格式，如商品sku，英文名是goodsSku生成的表名是es_goods_sku</font>
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
<div class="modal fade" id="generateModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="generateModalnName">模型名称</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="generate-user-form" >
                    <div class="box-body">
                        <div class="form-group " >
                            <label for="domain" id="englishname">英文名:goods</label>
                        </div>
                        <div class="form-group " >
                            <label for="domain" id="tablename">表名:es_goods</label>
                        </div>
                        <div class="form-group " >
                        	<label for="domain" id="tablename">生成项</label>
                            <div>
							    <label class="checkbox-inline">
							        <input type="checkbox" id="inlineCheckbox1" name="generateItems" value="service"> 业务类
							    </label>
							    <label class="checkbox-inline">
							        <input type="checkbox" id="inlineCheckbox2" name="generateItems" value="controller"> 控制器类
							    </label>
							</div>
                        </div>
                        <div class="form-group " >
                        	<label for="domain" id="tablename">包名</label>
                            <div> 
                            	<p id ="packagename"></p>
							    <input type="text" class="form-control" autocomplete="false"   name="packageName" placeholder="请输入剩余包名"  >
							</div>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="generate-ok-btn">确定</button>
            </div>
        </div>
    </div>
</div>
<!-- Modal -->



<!-- Modal -->
<div class="modal fade" id="table-modal" tabindex="-1" role="dialog" aria-labelledby="tableModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="myModalLabel">导入PDM</h4>
            </div>
            <div class="modal-body">

                <form role="form" id="import-form" >
                    <div class="box-body">
						<table class="table table-striped">
							<thead>
								<tr>
									<th style="width: 10px">
										<font style="vertical-align: inherit;"><font style="vertical-align: inherit;">
											<input type="checkbox" name="allCode" id="allCode"  >
										</font></font>
									</th>
									<th>
										<font style="vertical-align: inherit;">
											<font style="vertical-align: inherit;">表备注</font>
										</font>
									</th>
									<th>
										<font style="vertical-align: inherit;">
											<font style="vertical-align: inherit;">表名</font>
										</font>
									</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                <button type="button" class="btn btn-primary" id="tables-btn">确定</button>
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
                url: '/data/project/${module_id}/${version_id}/model',
            },

            columns: [ //定义列

                {
                    data: "model_name"
                },
                {
                    data: "english_name"
                }
                ,
                {
                    data: "table_name"
                }  ,
                {
                    data: "version"
                }  ,
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
                        btnHtml+='        <li><a href="javascript:editModel('+data['model_id']+') " >编辑</a></li>'
                        btnHtml+='        <li><a href="javascript:deleteModel('+data['model_id']+') " >删除</a></li>'
                        btnHtml+='        <li><a href="javascript:inmodel('+data['model_id']+')" >编辑字段</a></li>'
                        btnHtml+='        <li><a href="javascript:generateCoder('+data['model_id']+') " >生成</a></li>'
                        btnHtml+='        <li><a href="javascript:location.href=\'/data/project/model/'+data['model_id']+'/download\'" >下载</a></li>'
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
                    table.ajax.url("/data/project/${module_id}/"+value+"/model").load();
                }
            }) ;
            }

        });
		
		/**
         * 发出添加请求
         */
        $("#generate-ok-btn").click(function () {
            if( $("#generate-user-form").valid() ){
            $("#generate-user-form").ajaxSubmit({
            	url:url,
                type:"get",
                success:function () {
                    alert("生成成功")
                    $('#generateModal').modal("hide");
                }
            }) ;
            }

        });
		
		//全选
		$("#allCode").change(function(){
			if (this.checked) {  
	            $("#table-modal table tbody :checkbox").attr("checked", true);  
	        } else {  
	            $("#table-modal table tbody :checkbox").attr("checked", false);  
	        }  
		})
		
		//提交已选的表生成模型
		$("#tables-btn").click(function(){
			var code = [];
			$.each($("#table-modal table tbody input[name='code']:checked"),function(){
				code.push($(this).val());
            });
			
			$.ajax({
	            url:"/file/create/model?column_code="+code+"&projectId=${project_id}&moduleId=${module_id}",
	            type:"post",
	            success:function (data) {
		            	if(data=='ok'){
	        				alert("保存成功")
	                		location.reload();
	            		}else{
	            			alert(data);
	            		}
	            }
	        });
		})

    });

	//添加模型
	function addProject(){
		url = "/data/project/${module_id}/model";
		$("#module").hide();
		$('#myModal').modal();
	
	}


    $('#fileupload').fileupload({
        done: function (e, data) {
//            console.log(data)
            impotPDM(data.result)
        },
        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('.progress .progress-bar').css(
                    'width',
                    progress + '%'
            );
        }
    });


	//导入pdm文件
	function impotPDM(data){
		
		$("#table-modal table tbody").empty();

				if(data==null || data=="undefined"){
					alert("没有可解析的数据，请检查pdm文件");
				}
				$.each(data,function(index, value) {
					for(var key in value){
						
						var thHtml = '<tr>';
						thHtml += '	<td>';
						thHtml += '		<font style="vertical-align: inherit;"><font style="vertical-align: inherit;">';
						thHtml += '			<input type="checkbox" name="code" value="'+key+'" >';
						thHtml += ' 		</font></font>';
						thHtml += '	</td>';
						thHtml += '  <td>';
						thHtml += '		<font style="vertical-align: inherit;">';
						thHtml += '			<font style="vertical-align: inherit;">'+value[key]+'</font>';
						thHtml += '		</font>';
						thHtml += '	</td>';
						thHtml += '	<td>';
						thHtml += '		<font style="vertical-align: inherit;">';
						thHtml += '			<font style="vertical-align: inherit;">'+key+'</font>';
						thHtml += '		</font>';
						thHtml += '	</td>';
						thHtml += '</tr>';
						
						$("#table-modal table tbody").append(thHtml);
						$("#table-modal").modal();
					}
				});

	}

	function inmodel(model_id){
        var value = $("#select_version").val();
        location.href='/view/project/model/'+model_id+'/field/list?version_id='+value;
    }
	
	//编辑
	function editModel(model_id){
		url = "/data/project/${module_id}/model/"+model_id;
		$("#module").show();
		$('#myModal').modal();
		$.ajax({
            url:url,
            dataType:"json",
            success:function (user) {
                $("#user-form .form-control").each(function (i,el) {
                    var value = user[el.name];
                    $(el).val(value);
                });
            }
        });
	}
	
	function deleteModel(model_id){
		if(confirm("确定删除该模型吗？")){
			$.ajax({
	            url:"/data/project/${project_id}/model/"+model_id,
	            method:"delete",
	            success:function (user) {
	            	alert("删除成功")
                    var value = $("#select_version").val();
                    table.ajax.url("/data/project/${module_id}/"+value+"/model").load();
	            }
	        });
			
			
		}
	}
	
	
	function generateCoder(model_id){
		$('#generateModal').modal();
		$.ajax({
            url:"/data/project/${project_id}/model/"+model_id,
            dataType:"json",
            success:function (user) {
            	$("#generateModalnName").text("模型名称 "+user.model_name);
            	$("#englishname").text("英文名：  "+user.english_name);
            	$("#tablename").text("表  名：  "+user.table_name);
               console.log(user);
            }
        });
		
		$.ajax({
            url:"/data/project/${project_id}",
            dataType:"json",
            success:function (project) {
            	$("#packagename").text(project.package_name+".");
            }
        });
		url = "/data/project/model/"+model_id+"/generate";
	}

	//切换分支
    function changeVersion(){

        var value = $("#select_version").val();

        var text = $("#select_version").find("option:selected").text();

        table.ajax.url('/data/project/${module_id}/'+value+'/model').load();

        $("#branch-name").text(text);
    }

</script>

<script src="/plugins/input-mask/jquery.inputmask.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.date.extensions.js"></script>
<script src="/plugins/select2/select2.full.min.js"></script>
<script src="/plugins/input-mask/jquery.inputmask.numeric.extensions.js"></script>

<#include 'common/footer.ftl' />