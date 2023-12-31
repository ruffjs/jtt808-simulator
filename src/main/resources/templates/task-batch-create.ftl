<!DOCTYPE html>
<html lang="en">
<head>
    <#include "inc/resource.ftl">
    <title>批量创建行程</title>
    <style type="text/css">
        .container .content .x-row
        {
            xheight: 40px;
            line-height: 40px;
        }
        .container .content
        {
            padding: 20px 20px 20px 20px;
        }
        .content .x-col-2 input { width: 100%; }
        .x-hint
        {
            color: #999999;
            padding: 0px 10px 0px 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <#include "inc/sidebar.ftl">
    <div class="content">
        <h2>批量创建行程任务</h2>
        <hr />
        <div class="x-row">
            <div class="x-col-2 text-right">行驶线路：</div>
            <div class="x-col-6">
                <select name="routeId" id="routeId" multiple size="20" style="height: 300px;">
                    <option value="0" selected>- 随机分配 -</option>
                    <#list routes as item>
                        <option value="${item.id}">${item.name} - ${item.kilometers!'NaN'}公里</option>
                    </#list>
                </select>
            </div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">车辆数量：</div>
            <div class="x-col-2"><input type="text" name="vehicleCount" id="vehicleCount" placeholder="最高不超过100000" value="1000" /></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">车牌号：</div>
            <div class="x-col-2"><input type="text" name="vehicleNumberPattern" id="vehicleNumberPattern" placeholder="如：测%06d" value="测%06d" /></div>
            <div class="x-col-4"><span class="x-hint">如<strong>%06d</strong>表示六个数字，左侧补0，从0~车辆数量的序号生成车牌号</span></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">终端ID：</div>
            <div class="x-col-2"><input type="text" name="deviceSnPattern" id="deviceSnPattern" placeholder="如：T%06d" value="T%06d" /></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">终端SIM卡号：</div>
            <div class="x-col-2"><input type="text" name="simNumberPatter" id="simNumberPattern" placeholder="如：013800%06d，12位数字" value="013800%06d" /></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">808网关服务器：</div>
            <div class="x-col-2"><input type="text" name="serverAddress" id="serverAddress" value="${vehicleServerAddr}" /></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">808网关服务器端口：</div>
            <div class="x-col-2"><input type="text" name="serverPort" id="serverPort" value="${vehicleServerPort}" /></div>
            <div class="x-clearfix"></div>
        </div>
        <div class="x-row">
            <div class="x-col-2 text-right">&nbsp;</div>
            <div class="x-col-4">
                <button class="btn btn-blue" id="btn-run">创建</button>
            </div>
            <div class="x-clearfix"></div>
        </div>
    </div>
</div>
</body>
<#include "inc/footer.ftl">
<script type="text/javascript">
    $(document).ready(function()
    {
        setCurrentMenu('batch');

        $('#btn-run').click(function()
        {
            $("#btn-run").attr("disabled","true");
            var idList = [];
            $('#routeId option:selected').each(function()
            {
                idList.push(this.value);
            });

            var params = {
                routeIdList : idList,
                vehicleCount : $('#vehicleCount').val(),
                vehicleNumberPattern : $('#vehicleNumberPattern').val(),
                deviceSnPattern : $('#deviceSnPattern').val(),
                simNumberPattern : $('#simNumberPattern').val(),
                serverAddress : $('#serverAddress').val(),
                serverPort : $('#serverPort').val()
            };
            $.post('./run', params, function(result)
            {
                if (result.error && result.error.code) return toastr('error', result.error.reason);
                else toastr('success', '启动成功');
                setTimeout(()=>$("#btn-run").removeAttr("disabled"),100)
            });
        });
    });
</script>
</html>