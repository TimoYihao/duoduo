<!DOCTYPE html>
<#import "../../macro/common.ftl" as common>
<html>
	<head>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<title>JeeBoom — 系统用户列表</title>
		<link rel="stylesheet" href="${springMacroRequestContext.contextPath}/static/layui/css/layui.css" />
		<link rel="stylesheet" href="${springMacroRequestContext.contextPath}/static/css/global.css" />
		<link rel="stylesheet" type="text/css" href="http://www.jq22.com/jquery/font-awesome.4.6.0.css">
        <style>
            .layui-table th{
                text-align: center;
            }
            td{
                text-align: center;
            }
        </style>
	</head>
	<body class="anim-fadeInUp">
		<div style="margin: 15px;">
			<div class="layui-tab-brief" >
				<ul class="layui-tab-title">
					<li class="layui-this">系统用户列表</li>
				</ul>
			</div>
	 		<!--
             	作者：196410791@qq.com
             	时间：2017-03-15
             	描述：表单搜索
             -->
			<form id="searchForm" class="layui-form" style="margin-top: 20px;">
				<div class="layui-form-item">
					<input type ="hidden" name="pageNo" value="${(pager.pageNo)!}">
					<input type ="hidden" name="pageSize" value="15">
					<div class="layui-inline">
						<input  type = "text" name="username" value="${(sysUser.username)!}" placeholder="用户账号" class="layui-input" />
					</div>
					<#--<div class="layui-inline">
						<input  type = "text" name="name" value="${(sysUser.name)!}" placeholder="用户姓名" class="layui-input" />
					</div>-->
					<div class="layui-inline">
						<input  type = "text" name="phone" value="${(sysUser.phone)!}" placeholder="手机号码" class="layui-input" />
					</div>
					<div class="layui-inline">
						<button onclick="layui.submitForm()" class="layui-btn">搜索&nbsp;<i class="layui-icon" >&#xe615;</i></button>
					</div>
					<@common.permission per='sys:sysUser:edit'>
						<div class="layui-inline">
							<a href="javascript:layui.add('${(departId)!}');" class="layui-btn">添加用户&nbsp;<i class="layui-icon" >&#xe61f;</i></a>
						</div>
					</@common.permission>
                    <div class="layui-inline fr">
                        <button type="button" class="layui-btn layui-btn-primary" id="upload">
                            导入用户&nbsp;<i class="layui-icon">&#xe67c;</i>
                        </button>
                    </div>
                    <div class="layui-inline fr">
                        <a href="javascript:layui.outputExl();" class="layui-btn layui-btn-primary">导出用户&nbsp;<i class="layui-icon">&#xe601;</i></a>
                    </div>
                    <#--<div class="layui-inline fr">
                        <a href="javascript:layui.tempDown();" class="layui-btn layui-btn-primary">下载模板&nbsp;<i class="layui-icon">&#xe601;</i></a>
                    </div>-->
				</div>
			</form>
						
			<!--
            	作者：196410791@qq.com
            	时间：2017-03-15
            	描述：表格
            -->
            <div style="margin-top: 20px;">
				<table class="layui-table layui-form">
				    <thead>
						<tr>
                            <th><input type="checkbox" name="" lay-skin="primary" lay-filter="allChoose"></th>
							<th>序号</th>
							<th>用户姓名</th>
							<th>用户账号</th>
							<th>手机号码</th>
							<th>是否禁用</th>
							<th>登录时间</th>
							<@common.permission per='sys:sysUser:edit'>
								<th>操作</th>
							</@common.permission>
						</tr>
					</thead>
					<tbody>
						<#list pager.list as entity>
							<tr>
                                <td><input type="checkbox" name="" lay-skin="primary"></td>
								<td>${(entity_index+1)!}</td>
								<td><a href="javascript:layui.showInfo('${(entity.id)!}','${(departId)!}')">${(entity.name)!}</a></td>
								<td>${(entity.username)!}</td>
								<td>${(entity.phone)!}</td>
								<td>${(entity.status == 2)?string('禁用','正常')}</td>
								<td><#if entity.loginTime?? >${entity.loginTime?string("yyyy年MM月dd日 HH:mm:ss")}</#if></td>
								<@common.permission per='sys:sysUser:edit'>
									<td>
										<a href="javascript:layui.edit('${(entity.id)!}','${(departId)!}')" class="layui-btn layui-btn-xs">编辑</a>
										<a href="javascript:layui.accredit('${(entity.id)!}','${(entity.status)!}')" class="layui-btn layui-btn-danger layui-btn-xs">
											${(entity.status?? && entity.status == 2)?string('开启','禁用')}
										</a>
									</td>
								</@common.permission>
							</tr>
						</#list>
					</tbody>
				</table>
				<div id="pager" class="fr"></div>
			</div>
		</div>
		<script type="text/javascript" src="${springMacroRequestContext.contextPath}/static/layui/layui.js"></script>
		<script>
			layui.use(['form','laydate','laypage','layer','upload'],function(){
				var form = layui.form;
				var laypage = layui.laypage;
				var layer = layui.layer;
                var upload = layui.upload;
				var $ = layui.jquery;

                //全选
                form.on('checkbox(allChoose)', function(data){
                    var child = $(data.elem).parents('table').find('tbody input[type="checkbox"]');
                    child.each(function(index, item){
                        item.checked = data.elem.checked;
                    });
                    form.render('checkbox');
                });

				laypage.render({
                    elem: 'pager',
                    count: '${(pager.count)!1}',
                    limit: '15',
					jump:function(obj,first){
						if(!first){
							$("input[name=pageNo]").val(obj.curr);
							layui.submitForm();
						}
					}
				});

                //搜索按钮点击事件
                layui.submitForm = function(){
                    $("#searchForm").submit();
                }

                //查看菜单信息
                layui.showInfo = function(id,depart){
                    if(depart!=''){
                        layui.save("查看用户信息",depart,id);
                    }else{
                        layer.msg("请选择部门!");
                    }
                }

				//编辑
                layui.edit = function(id,depart){
                    if(depart!=''){
                        layui.save("修改用户信息",depart,id)
                    }else{
                        layer.msg("请选择部门!");
                    }
                }

				//添加
                layui.add = function(depart){
					if(depart!=''){
                        layui.save("添加用户信息",depart);
					}else{
                        layer.msg("请选择部门!");
					}
				}

				//添加or编辑弹窗
				layui.save = function(title,depart,id){
					var url ='${springMacroRequestContext.contextPath}/sysUser/saveFrom?departmentId='+depart;
                    if(id!=null&&id!='undfined'){
                        url += "&id="+id
                    }
					layer.open({
						type: 2,
						title:title,
						area: ['800px', '500px'],
						fixed: false, //不固定
						maxmin: true,
						content: url
					});
				}

                //上下架
                layui.accredit = function(id,status){
                    var title,b;
                    if(status == 1){
                        title = "您是否确定要开启该菜单！";
                        b = ['开启', '取消'];
                    }else{
                        title = "您是否确定要禁用该菜单！";
                        b = ['禁用', '取消'];
                    }
                    layer.msg(title, {
                        btn: b,
                        yes:function(){
                            window.location.href="${springMacroRequestContext.contextPath}/sysUser/accredit?id="+id+"&status="+status;
                        }
                    });
                }

                //导出
				layui.outputExl = function(){
                    window.open("${springMacroRequestContext.contextPath}/sysUser/outputExl");
				}

				//上传
                upload.render({
                    elem: '#upload',
                    url: '${springMacroRequestContext.contextPath}/sysUser/importExl',
                    accept: 'file',
                    exts: 'xls|xlsx',
                    done: function(res){ //上传成功后的回调
                        if(res.code == 200){
							layer.msg("您导入的数据为:" + res.data);
						}else{
                            layer.msg(res.message);
						}
                    }
                });

				//下载模板
				layui.tempDown = function(){
                    window.open("${springMacroRequestContext.contextPath}/userTemp.xls");
				}
			});
		</script>
	</body>
</html>
