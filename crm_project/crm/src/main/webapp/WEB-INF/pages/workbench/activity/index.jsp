<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<base href="<%=basePath%>">

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">

<link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
<link href="jquery/bootstrap-datetimepicker-master/css/bootstrap-datetimepicker.min.css" type="text/css" rel="stylesheet" />
	<link rel="stylesheet" type="text/css" href="jquery/bs_pagination-master/css/jquery.bs_pagination.min.css">

<script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
<script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/js/bootstrap-datetimepicker.js"></script>
<script type="text/javascript" src="jquery/bootstrap-datetimepicker-master/locale/bootstrap-datetimepicker.zh-CN.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/js/jquery.bs_pagination.min.js"></script>
<script type="text/javascript" src="jquery/bs_pagination-master/localization/en.js"></script>

<script type="text/javascript">

	$(function(){
		//给创建按钮添加单机事件
		$("#createActivityBtn").click(function(){
			//之所以使用这种方式，在这里你可以添加其他的js代码
			//重置表单（将jquery对象转换成dom对象，以此来清除表单数据）
			$("#createActivityForm").get(0).reset();
			//创建市场活动的模态窗口
			$("#createActivityModal").modal("show");
		});

		//给保存按钮，添加单击事件
		$("#saveCreateActivity").click(function (){
			//收集参数
			var owner = $("#create-marketActivityOwner").val();
			var name = $.trim($("#create-marketActivityName").val());
			var startDate = $("#create-startDate").val();
			var endDate = $("#create-endDate").val();
			var cost = $.trim($("#create-cost").val());
			var description = $.trim($("#create-description").val());

			//表单验证
			if (owner==""){
				alert("所有者不能为空");
				return;
			}
			if (name==""){
				alert("名称不能为空");
				return;
			}
			if (startDate!=""&&endDate!=""){
				//使用字符串大小代替日期的大小
				if (endDate<startDate){
					alert("结束日期不能比开始日期小");
					return;
				}
			}
			/*
			正则表达式：
				1，语言，语法：定义字符串的匹配模式，可以用来判断指定的具体字符串是否符合匹配模式
			 */
			var regExp=/^(([1-9]\d*)|0)$/;
			if (!regExp.test(cost)){
				alert("成本只能是非负整数！！！");
				return;
			}
			//发送请求
			$.ajax({
				url:'workbench/activity/saveCreateActivity.do',
				data:{
					owner:owner,
					name:name,
					startDate:startDate,
					endDate:endDate,
					cost:cost,
					description:description
				},
				type:'post',
				dataType:'json',
				success:function(data){
					 if (data.code=="1"){
						 //关闭模态窗口
						 $("#createActivityModal").modal("hide");
						 //刷新市场活动列，显示第一页数据，保持每页显示条数不变
						 queryActivityForPage(1,$("#demo_page1").bs_pagination('getOption','rowsPerPage'));
					 }else{
						 //提示信息
						 alert(data.message);
						 //模态窗口不关闭（可以不写，但是还是写一下）
						 $("#createActivityModal").modal("show");
					 }
				}
			});

		});


		//当容器加载晚场之后，对容器调用工具函数
		$(".mydate").datetimepicker(
				{
					language:'zh-CN',//语言
					format:'yyyy-mm-dd',//日期的格式
					minView:'month',//可以选择的最小视图
					initialDate:true,//初始化显示的日期
					autoclose:true,//设置选择完日期或者时间后，是否关闭日历
					todayBtn:true,//设置是否显示“今天”按钮，默认是false
					clearBtn:true//设置是否显示“清空”按钮，默认是false，这里可能是测试功能，所以是中文，我们需要去文件中改成中文

				}
		);

		queryActivityForPage(1,10);

		//给查询按钮添加单击事件
		$("#queryActivityBtn").click(function (){
			queryActivityForPage(1,$("#demo_page1").bs_pagination('getOption','rowsPerPage'));
		});

		//给全选按钮添加单击事件
        $("#checkAll").click(function (){
            $("#tbody input[type='checkbox']").prop("checked",this.checked)
        });

       /* $("#tbody input[type='checkbox']").click(function (){
            //判断列表中的checkbox是否都选中中了，要是全都选中了，那么全选按钮也得选中
            //获取列表中所有的checkbox
            $("#tbody input[type='checkbox']:checked").size()
            if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()){
                $("#checkAll").prop("checked",true);
            }else{
                $("#checkAll").prop("checked",false);
            }
        });*/

        $("#tbody").on("click","input[type='checkbox']",function (){
            $("#tbody input[type='checkbox']:checked").size()
            if ($("#tbody input[type='checkbox']").size() == $("#tbody input[type='checkbox']:checked").size()){
                $("#checkAll").prop("checked",true);
            }else{
                $("#checkAll").prop("checked",false);
            }
        });

		//给“删除”按钮添加单击事件
		$("#deleteActivityBtn").click(function (){
			//收集参数
			//获取列表中所有被选中的checkbox
			var checkedIds = $("#tbody input[type='checkbox']:checked");
			if(checkedIds.size()==0){
				alert("请选择要删除的市场活动");
				return;
			}

			if (window.confirm("确定删除吗？")){
				var ids = ""

				$.each(checkedIds,function (index,obj){
					ids +="id="+obj.value+"&";
				});
				ids.substring(0,ids.length-1);//这一步是为了删除最后的“&”

				//发送请求
				$.ajax({
					url:'workbench/activity/deleteActivityByIds.do',
					dataType:"json",
					type:"POST",
					data:ids,
					success:function(data){
						if (data.code=="1"){
							//刷新市场活动列表，显示第一页数据，保持每页数据条数不变
							queryActivityForPage(1,$("#demo_page1").bs_pagination('getOption','rowsPerPage'));
						}else{
							//提示信息
							alert(data.message);
						}
					}
				})
			}
		});

		//给修改按钮添加单击事件
		$("#updateBtn").click(function (){
			var checkedIds = $("#tbody input[type='checkbox']:checked");
			if(checkedIds.size()==0){
				alert("请选择要修改的市场活动");
				return;
			}else if (checkedIds.size()>1){
				alert("单词修改数量不能大于1");
				return;
			}

			var id = checkedIds[0].value;

			$.ajax({
				url:"workbench/activity/queryActivityById.do",
				dataType:"json",
				type:"post",
				data:{id:id},
				success:function (data){
					//把市场活动的信息显示在修改得模态窗口中
					$("#edit-marketActivityOwner").val(data.owner)
					$("#edit-marketActivityName").val(data.name);
					$("#edit-startTime").val(data.startDate);
					$("#edit-endTime").val(data.endDate);
					$("#edit-cost").val(data.cost);
					$("#edit-describe").val(data.description);

					//弹出模态窗口
					$("#editActivityModal").modal("show");

				}
			});
		});

		//给更新按钮添加单击事件
		$("#updateNewBtn").click(function (){
			//获取数据
			var id = $("#tbody input[type='checkbox']:checked").val();
			var owner = $("#edit-marketActivityOwner").val();
			var name = $("#edit-marketActivityName").val();
			var startDate = $("#edit-startTime").val();
			var endDate = $("#edit-endTime").val();
			var cost = $("#edit-cost").val();
			var description = $("#edit-describe").val();


			$.ajax({
				url:"workbench/activity/updateActivity.do",
				type:"POST",
				dataType:"json",
				data:{"id":id,"owner":owner,"name":name,"startDate":startDate,"endDate":endDate,"cost":cost,"description":description},
				success:function (data){
					if (data.code=="1"){
						//刷新市场活动列表，显示第一页数据，保持每页数据条数不变
						$("#editActivityModal").modal("hide");
						queryActivityForPage(1,$("#demo_page1").bs_pagination('getOption','rowsPerPage'));
					}else{
						//提示信息
						alert(data.message);
					}
				}
			})

		});

		//给“批量导出”按钮添加单机事件
		$("#exportActivityAllBtn").click(function (){
			//发送同步请求
			window.location.href="workbench/activity/queryAllActivitys.do";
		});

		//给“选择导出”添加单击事件
		$("#exportActivityXzBtn").click(function (){
			var checkedIds = $("#tbody input[type='checkbox']:checked");
			if (checkedIds.size() == 0){
				alert("请选择要导出的数据");
				return;
			}
			var ids = ""
			$.each(checkedIds,function (index,obj){
				ids +="id="+obj.value+"&";
			});
			ids.substring(0,ids.length-1);//这一步是为了删除最后的“&”

			//发送同步请求
			window.location.href="workbench/activity/queryAllActivitysById.do"+"?"+ids;

			/*$.ajax({
				url:"workbench/activity/queryAllActivitysById.do",
				async:false,
				data:ids,
				type:"GET"
			})*/
		});


		//给“导入”按钮添加单击事件
		$("#importActivityBtn").click(function (){

			var activityFileName=$("#activityFile").val();
			var suffix=activityFileName.substr(activityFileName.lastIndexOf(".")+1).toLocaleLowerCase();//xls,XLS,Xls,xLs,....
			if (suffix != "xls"){
				alert("请上传正确的文件格式！！！");
				return;
			}
			//验证文件大小是否合理
			var activityFile = $("#activityFile")[0].files[0];//这里不适用value，value无法保存文件，同时用files不用file，为了以后的扩展性
			if (activityFile.size>5*1024*1024){
				alert("文件过大，上传失败");
				return;
			}

			//获取文件信息，传给后端
			var fd = new FormData();
			fd.append("activityFile",activityFile);

			$.ajax({
				url:"workbench/activity/importActivity.do",
				type:"POST",
				processData:false,//设置ajax向后台提交参数之前，是否把参数统一转换成字符串：true--是,false--不是,默认是true
				contentType:false,//设置ajax向后台提交参数之前，是否把所有的参数统一按urlencoded编码：true--是,false--不是，默认是true
				data:fd,
				dataType:"json",
				success:function (data){
					if (data.code == "1"){
						$("#importActivityModal").modal("hide");
						alert("成功插入 "+data.retData+" 条数据");
						queryActivityForPage(1,$("#demo_page1").bs_pagination('getOption','rowsPerPage'));
					}else{
						$("#importActivityModal").modal("show");
						alert(data.message);
					}
				}
			})
		});
	});

	//封装函数在入口函数外面封装
	function queryActivityForPage(pageNo,pageSize){
		//当市场哦东主页面加载完成，查询所有数据的第一页以及所有数据的总条数
		var name=$("#query-name").val();
		var owner=$("#query-owner").val();
		var startDate=$("#query-startDate").val();
		var endDate=$("#query-endDate").val();
		/*var pageNo=1;
		var pageSize=10;*/
		//发送请求
		$.ajax({
			url:'workbench/activity/queryActivityByConditionForPage.do',//前面加了base标签，都是从pages目录出发，并且斜杠写完了
			type: 'POST',
			dataType: 'json',
			data:{
				name:name,
				owner:owner,
				startDate:startDate,
				endDate:endDate,
				pageNo:pageNo,
				pageSize:pageSize
			},
			success:function(data){
				//显示总条数
				//$("#totalCount").html(data.totalCount);
				//显示市场活动的列表
				//遍历activityList，拼接所有行数据
				var htmlStr = "";
				$.each(data.activityList,function (index,obj){//在里面使用this也是表示obj，遍历出来的元素
					htmlStr +="<tr class=\"active\">"
					htmlStr +="<td><input type=\"checkbox\" value=\"" + obj.id + "\"></td>"
					htmlStr +=	"<td><a style=\"text-decoration: none; cursor: pointer;\" onclick=\"window.location.href='workbench/activity/detailActivity.do?id="+obj.id+"'\">"+ obj.name +"</a></td>"
					htmlStr +=	"<td>"+ obj.owner +"</td>"
					htmlStr +=	"<td>"+ obj.startDate +"</td>"
					htmlStr +=	"<td>"+ obj.endDate +"</td>"
					htmlStr +="</tr>"
				});
				$("#tbody").html(htmlStr);

				//计算总页数
				var totalPages =1;
				if (data.totalCount%pageSize == 0){
					totalPages = data.totalCount/pageSize;
				}else{
					totalPages = parseInt(data.totalCount/pageSize)+1;
				}


				//对容器调用bs_pagination工具函数，显示翻页信息
				$("#demo_page1").bs_pagination({

					currentPage:pageNo,//当前也好==页号，相当于pageNo

					rowsPerPage:pageSize,//每页显示条数，相当于pageSize
					totalRows:data.totalCount,//每页显示条数，相当于pageSize
					totalPages:totalPages,//总页数，必填参数。

					visiblePageLinks: 5,//最多可以显示的卡片数。

					showGoToPage: true,//是否显示“跳转到”补分，默认true--显示
					showRowsPerPage: true,//是否显示“每页显示条数”部分。默认true---显示
					showRowsInfo:true,//是否显示记录的信息，默认true---显示

					//用户每次切换页号，都自动出发本函数
					//每次返回切换页号之后的pageNo和pageSize
					onChangePage:function (event,pageObj){
						//js代码
						/*alert(pageObj.currentPage);
						alert(pageObj.rowsPerPage)*/
						queryActivityForPage(pageObj.currentPage,pageObj.rowsPerPage)
					}
				});

                //取消全选按钮
                $("#checkAll").prop("checked",false);
			}
		});
	}
	
</script>
</head>
<body>

	<!-- 创建市场活动的模态窗口 -->
	<div class="modal fade" id="createActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel1">创建市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form" id="createActivityForm">
					
						<div class="form-group">
							<label for="create-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="create-marketActivityOwner">
								  <c:forEach items="${requestScope.userList}" var="u">
									  <option value="${u.id}">${u.name}</option>
								  </c:forEach>
								</select>
							</div>
                            <label for="create-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-marketActivityName">
                            </div>
						</div>
						
						<div class="form-group">
							<label for="create-startDate" class="col-sm-2 control-label" >开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="create-startDate" readonly>
							</div>
							<label for="create-endDate" class="col-sm-2 control-label" >结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="create-endDate" readonly>
							</div>
						</div>
                        <div class="form-group">

                            <label for="create-cost" class="col-sm-2 control-label">成本</label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="create-cost">
                            </div>
                        </div>
						<div class="form-group">
							<label for="create-description" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="create-description"></textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					<button type="button" class="btn btn-primary" id="saveCreateActivity">保存</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 修改市场活动的模态窗口 -->
	<div class="modal fade" id="editActivityModal" role="dialog">
		<div class="modal-dialog" role="document" style="width: 85%;">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
					<h4 class="modal-title" id="myModalLabel2">修改市场活动</h4>
				</div>
				<div class="modal-body">
				
					<form class="form-horizontal" role="form">
					
						<div class="form-group">
							<label for="edit-marketActivityOwner" class="col-sm-2 control-label">所有者<span style="font-size: 15px; color: red;">*</span></label>
							<div class="col-sm-10" style="width: 300px;">
								<select class="form-control" id="edit-marketActivityOwner">
									<c:forEach items="${requestScope.userList}" var="u">
										<option value="${u.id}">${u.name}</option>
									</c:forEach>
								</select>
							</div>
                            <label for="edit-marketActivityName" class="col-sm-2 control-label">名称<span style="font-size: 15px; color: red;">*</span></label>
                            <div class="col-sm-10" style="width: 300px;">
                                <input type="text" class="form-control" id="edit-marketActivityName" value="发传单">
                            </div>
						</div>

						<div class="form-group">
							<label for="edit-startTime" class="col-sm-2 control-label" >开始日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-startTime" value="2020-10-10" readonly>
							</div>
							<label for="edit-endTime" class="col-sm-2 control-label" >结束日期</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control mydate" id="edit-endTime" value="2020-10-20" readonly>
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-cost" class="col-sm-2 control-label">成本</label>
							<div class="col-sm-10" style="width: 300px;">
								<input type="text" class="form-control" id="edit-cost" value="5,000">
							</div>
						</div>
						
						<div class="form-group">
							<label for="edit-describe" class="col-sm-2 control-label">描述</label>
							<div class="col-sm-10" style="width: 81%;">
								<textarea class="form-control" rows="3" id="edit-describe">市场活动Marketing，是指品牌主办或参与的展览会议与公关市场活动，包括自行主办的各类研讨会、客户交流会、演示会、新产品发布会、体验会、答谢会、年会和出席参加并布展或演讲的展览会、研讨会、行业交流会、颁奖典礼等</textarea>
							</div>
						</div>
						
					</form>
					
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" >关闭</button>
					<button type="button" class="btn btn-primary" id="updateNewBtn">更新</button>
				</div>
			</div>
		</div>
	</div>
	
	<!-- 导入市场活动的模态窗口 -->
    <div class="modal fade" id="importActivityModal" role="dialog">
        <div class="modal-dialog" role="document" style="width: 85%;">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">
                        <span aria-hidden="true">×</span>
                    </button>
                    <h4 class="modal-title" id="myModalLabel">导入市场活动</h4>
                </div>
                <div class="modal-body" style="height: 350px;">
                    <div style="position: relative;top: 20px; left: 50px;">
                        请选择要上传的文件：<small style="color: gray;">[仅支持.xls]</small>
                    </div>
                    <div style="position: relative;top: 40px; left: 50px;">
                        <input type="file" id="activityFile">
                    </div>
                    <div style="position: relative; width: 400px; height: 320px; left: 45% ; top: -40px;" >
                        <h3>重要提示</h3>
                        <ul>
                            <li>操作仅针对Excel，仅支持后缀名为XLS的文件。</li>
                            <li>给定文件的第一行将视为字段名。</li>
                            <li>请确认您的文件大小不超过5MB。</li>
                            <li>日期值以文本形式保存，必须符合yyyy-MM-dd格式。</li>
                            <li>日期时间以文本形式保存，必须符合yyyy-MM-dd HH:mm:ss的格式。</li>
                            <li>默认情况下，字符编码是UTF-8 (统一码)，请确保您导入的文件使用的是正确的字符编码方式。</li>
                            <li>建议您在导入真实数据之前用测试文件测试文件导入功能。</li>
                        </ul>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button id="importActivityBtn" type="button" class="btn btn-primary">导入</button>
                </div>
            </div>
        </div>
    </div>
	
	
	<div>
		<div style="position: relative; left: 10px; top: -10px;">
			<div class="page-header">
				<h3>市场活动列表</h3>
			</div>
		</div>
	</div>
	<div style="position: relative; top: -20px; left: 0px; width: 100%; height: 100%;">
		<div style="width: 100%; position: absolute;top: 5px; left: 10px;">
		
			<div class="btn-toolbar" role="toolbar" style="height: 80px;">
				<form class="form-inline" role="form" style="position: relative;top: 8%; left: 5px;">
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">名称</div>
				      <input class="form-control" type="text" id="query-name">
				    </div>
				  </div>
				  
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">所有者</div>
				      <input class="form-control" type="text" id="query-owner">
				    </div>
				  </div>


				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">开始日期</div>
					  <input class="form-control" type="text" id="query-startDate" />
				    </div>
				  </div>
				  <div class="form-group">
				    <div class="input-group">
				      <div class="input-group-addon">结束日期</div>
					  <input class="form-control" type="text" id="query-endDate">
				    </div>
				  </div>
				  
				  <button type="button" class="btn btn-default" id="queryActivityBtn">查询</button>
				  
				</form>
			</div>
			<div class="btn-toolbar" role="toolbar" style="background-color: #F7F7F7; height: 50px; position: relative;top: 5px;">
				<div class="btn-group" style="position: relative; top: 18%;">
				  <button type="button" class="btn btn-primary" id="createActivityBtn"><span class="glyphicon glyphicon-plus"></span> 创建</button>
				  <button type="button" class="btn btn-default" id="updateBtn"><span class="glyphicon glyphicon-pencil"></span> 修改</button>
				  <button type="button" class="btn btn-danger" id="deleteActivityBtn"><span class="glyphicon glyphicon-minus"></span> 删除</button>
				</div>
				<div class="btn-group" style="position: relative; top: 18%;">
                    <button id = "importActivityAllBtn" type="button" class="btn btn-default" data-toggle="modal" data-target="#importActivityModal" ><span class="glyphicon glyphicon-import"></span> 上传列表数据（批量导入）</button>
                    <button id="exportActivityAllBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（批量导出）</button>
                    <button id="exportActivityXzBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-export"></span> 下载列表数据（选择导出）</button>
                </div>
			</div>
			<div style="position: relative;top: 10px;">
				<table class="table table-hover">
					<thead>
						<tr style="color: #B3B3B3;">
							<td><input type="checkbox" id="checkAll"/></td>
							<td>名称</td>
                            <td>所有者</td>
							<td>开始日期</td>
							<td>结束日期</td>
						</tr>
					</thead>
					<tbody id="tbody">
						<%--<tr class="active">
							<td><input type="checkbox" /></td>
							<td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
							<td>2020-10-10</td>
							<td>2020-10-20</td>
						</tr>
                        <tr class="active">
                            <td><input type="checkbox" /></td>
                            <td><a style="text-decoration: none; cursor: pointer;" onclick="window.location.href='detail.jsp';">发传单</a></td>
                            <td>zhangsan</td>
                            <td>2020-10-10</td>
                            <td>2020-10-20</td>
                        </tr>--%>
					</tbody>
				</table>

				<div id="demo_page1"></div>
			</div>


			<%--<div style="height: 50px; position: relative;top: 30px;">
				<div>
					<button type="button" class="btn btn-default" style="cursor: default;">共<b id="totalCount">50</b>条记录</button>
				</div>
				<div class="btn-group" style="position: relative;top: -34px; left: 110px;">
					<button type="button" class="btn btn-default" style="cursor: default;">显示</button>
					<div class="btn-group">
						<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
							10
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" role="menu">
							<li><a href="#">20</a></li>
							<li><a href="#">30</a></li>
						</ul>
					</div>
					<button type="button" class="btn btn-default" style="cursor: default;">条/页</button>
				</div>
				<div style="position: relative;top: -88px; left: 285px;">
					<nav>
						<ul class="pagination">
							<li class="disabled"><a href="#">首页</a></li>
							<li class="disabled"><a href="#">上一页</a></li>
							<li class="active"><a href="#">1</a></li>
							<li><a href="#">2</a></li>
							<li><a href="#">3</a></li>
							<li><a href="#">4</a></li>
							<li><a href="#">5</a></li>
							<li><a href="#">下一页</a></li>
							<li class="disabled"><a href="#">末页</a></li>
						</ul>
					</nav>
				</div>
			</div>--%>
			
		</div>
		
	</div>
</body>
</html>