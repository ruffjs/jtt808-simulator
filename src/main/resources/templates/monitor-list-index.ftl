<!DOCTYPE html>
<html lang="en">
<head>
    <#include "inc/resource.ftl">
    <title>行程任务</title>
</head>
<body>
<div class="container">
    <#include "inc/sidebar.ftl">
    <div class="content datasheet">
        <h2>行程任务</h2>
        <hr/>
        <div class="x-row">
            <div class="x-col-2" style="width: 100%" id="connectStates">
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="NotConnected"
                            value="NotConnected"/>
                    未连接
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="Connected"
                            value="Connected"/>
                    连接成功
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="Authed"
                            value="Authed"/>
                    认证成功
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="TaskEnd"
                            value="TaskEnd"/>
                    连接断开（任务结束）
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="RegisterFailed"
                            value="RegisterFailed"/>
                    连接断开（注册失败）
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="ManuallyClosed"
                            value="ManuallyClosed"/>
                    连接断开（手动关闭）
                </label>
                <label for="subscribeNews">
                    <input
                            type="checkbox"
                            id="Unknown"
                            value="Unknown"/>
                    连接断开（未知）
                </label>
                <button class="btn btn-blue pull-right" id="btn-search2" onclick="search2()">搜索</button>
                <button class="btn btn-blue pull-right" id="btn-terminateAll" onclick="terminateAll()">停止全部</button>

            </div>
        </div>
        <div id="route-table"></div>
        <ul class="pagination"></ul>
        <span id="totalCount">总条数：1</span>
    </div>
</div>
</body>
<#include "inc/footer.ftl">
<script type="text/javascript">

    const labels = $("label")
    for (let i = 0; i < labels.length; i++) {
        const label = $(labels[i]);
        const child = label.children("input");
        const id = child.prop("id");
        label.click(() => {
            const ch = $('#' + id);
            const checked = ch.is(':checked');
            ch.attr("checked", !checked);
            alert(checked);
        });
    }

    function terminateAll() {
        $("#btn-terminateAll").attr("disabled", "true");
        fetch("${context}/monitor/terminateAll").then(_ => {
            loadData();
            $("#btn-terminateAll").removeAttr("disabled");
            alert("停止成功")
        })
    }


    function search2() {
        const inputs = $('#connectStates').children("input");
        let qry = '';
        for (let i = 0; i < inputs.length; i++) {
            const it = inputs[i]
            if (it.checked) {
                qry = qry + 'connectionState=' + it.id + '&'
            }
        }
        if (qry == '') {
            qry = 'connectionState='
        }
        loadData(qry)
    }

    function loadData(qry) {
        if (qry == null) qry = 'connectionState='
        $('#route-table').paginate({
            url: '${context}/monitor/list/json?' + qry,
            fields: [
                {
                    name: 'routeName',
                    title: '线路名称',
                    align: 'center',
                },
                {
                    name: 'routeMileages',
                    title: '线路里程',
                    align: 'center',
                    formatter: function (i, v, r) {
                        return parseInt(v / 1000) + ' km';
                    }
                },
                {
                    name: 'vehicleNumber',
                    title: '车牌号',
                    align: 'center',
                },
                {
                    name: 'simNumber',
                    title: 'SIM卡号',
                    align: 'center',
                },
                {
                    name: 'deviceSn',
                    title: '终端ID',
                    align: 'center',
                },
                {
                    name: 'startTime',
                    title: '启动时间',
                    align: 'center',
                    formatter: function (i, v, r) {
                        return new Date(v).format('yyyy-MM-dd hh:mm:ss');
                    }
                },
                {
                    name: '',
                    title: '经纬度',
                    align: 'center',
                    formatter: function (i, v, r) {
                        const lon = String(r.longitude).replace(/^(\d+\.\d{6})\d+$/gi, '$1')
                        const lan = String(r.latitude).replace(/^(\d+\.\d{6})\d+$/gi, '$1')
                        if (lon == '0' && lan == '0') return '--'
                        return lon + "," + lan
                    }
                },
                {
                    name: 'reportTime',
                    title: '上报时间',
                    align: 'center',
                    formatter: function (i, v, r) {
                        if (r.reportTime == 0) {
                            return '--'
                        }
                        return new Date(v).format('yyyy-MM-dd hh:mm:ss');
                    }
                },
                {
                    name: 'connectionState',
                    title: '连接状态',
                    align: 'center',
                    formatter: function (i, v, r) {
                        const connectionStateMap = {
                            Connected: "连接成功",
                            Registered: "注册成功",
                            Authed: "认证成功",
                            TaskEnd: "连接断开（任务结束）",
                            ManuallyClosed: "连接断开（手动关闭）",
                            RegisterFailed: "连接断开（注册失败）",
                            AuthFailed: "连接断开（认证失败）",
                            Unknown: "连接断开（未知）"
                        }
                        let displayName = connectionStateMap[v]
                        if (displayName == null) {
                            displayName = connectionStateMap[r.disconnectReason]
                        }
                        return displayName ? displayName : "未连接"
                    }
                },
                {
                    name: 'id',
                    title: '操作',
                    align: 'center',
                    width: '160px',
                    formatter: function (i, v, r) {
                        var html = '';
                        html += '<a href="${context}/monitor/view?id=' + v + '" target="_blank" class="btn btn-sm btn-blue">详情</a>';
                        return html;
                    }
                },
            ]
        });
        fetch('${context}/monitor/list/json?' + qry).then(resp => resp.json().then(v => setTotalCount(v)))
    }

    function setTotalCount(v) {
        const rct = v?.data?.recordCount
        const cnt = rct ? rct : 0;
        $('#totalCount').text('总条数:' + cnt);
    }

    $(document).ready(function () {
        setCurrentMenu('list-monitor');
        loadData()
    });


</script>
</html>