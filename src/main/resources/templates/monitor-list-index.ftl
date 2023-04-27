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
                <input
                        type="checkbox"
                        id="NotConnected"
                        value="NotConnected"/>
                <label id="label-NotConnected" for="NotConnected">未连接:<span id="cnt-NotConnected"></span> </label>
                <input
                        type="checkbox"
                        id="Connected"
                        value="Connected"/>
                <label id="label-Connected" for="Connected">

                    连接成功:<span id="cnt-Connected"></span>
                </label>
                <input
                        type="checkbox"
                        id="Authed"
                        value="Authed"/>
                <label id="" for="Authed">

                    认证成功:<span id="cnt-Authed"></span>
                </label>
                <input
                        type="checkbox"
                        id="TaskEnd"
                        value="TaskEnd"/>
                <label id=label-TaskEnd" for="TaskEnd">

                    连接断开（任务结束）:<span id="cnt-TaskEnd"></span>
                </label>
                <input
                        type="checkbox"
                        id="RegisterFailed"
                        value="RegisterFailed"/>
                <label for="RegisterFailed">

                    连接断开（注册失败）:<span id="cnt-RegisterFailed"></span>
                </label>
                <input
                        type="checkbox"
                        id="ManuallyClosed"
                        value="ManuallyClosed"/>
                <label for="ManuallyClosed">

                    连接断开（手动关闭）:<span id="cnt-ManuallyClosed"></span>
                </label>
                <input
                        type="checkbox"
                        id="Unknown"
                        value="Unknown"/>
                <label for="Unknown">

                    连接断开（未知）:<span id="cnt-Unknown"></span>
                </label>
                <label id="totalCount">总条数：<span id="totalCnt"></span></label>
                <button class="btn btn-blue pull-right" id="btn-removeAll" onclick="removeAll()">删除全部</button>
                <button class="btn btn-blue pull-right" id="btn-search2" onclick="search2()">搜索</button>
                <button class="btn btn-blue pull-right" id="btn-terminateAll" onclick="terminateAll()">停止全部</button>

            </div>
        </div>
        <div id="route-table"></div>
        <ul class="pagination"></ul>

    </div>
</div>
</body>
<#include "inc/footer.ftl">
<script type="text/javascript">

    function terminateAll() {
        $("#btn-terminateAll").attr("disabled", "true");
        fetch("${context}/monitor/terminateAll").then(_ => {
            loadData();
            $("#btn-terminateAll").removeAttr("disabled");
            alert("停止成功")
        })
    }

    function removeAll() {
        $("#btn-removeAll").attr("disabled", "true");
        fetch("${context}/monitor/removeAll").then(_ => {
            loadData();
            $("#btn-removeAll").removeAttr("disabled");
            alert("删除成功")
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
        fetch('${context}/monitor/list/json?pageIndex=1&pageSize=99999&connectionState=').then(resp => resp.json().then(v => setTotalCount(v)))
    }

    function setTotalCount(v) {
        const spanList = $("span");
        for (let i = 0; i < spanList.length; i++) {
            $(spanList[i]).text(0)
        }
        const rct = v?.data?.recordCount
        const cnt = rct ? rct : 0;
        $('#totalCnt').text(cnt);
        const list = v.data.list
        const map = new Map();
        list.forEach((d) => {
            let key = d.connectionState
            if (key == "Disconnected") {
                key = d.disconnectReason
            }
            let c = map.get(key)
            c = c ? c : 0;
            map.set(key, c + 1)
        })
        map.forEach((v, key) => {
            $("#cnt-" + key).text(v)
        })
    }

    $(document).ready(function () {
        setCurrentMenu('list-monitor');
        loadData()
    });


</script>
</html>