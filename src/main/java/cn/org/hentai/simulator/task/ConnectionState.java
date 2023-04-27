package cn.org.hentai.simulator.task;

public enum ConnectionState {
    NotConnected,//初始状态
    Connected,//已连接
    Registered,//已注册
    Authed,//已认证
    Disconnected;//断开连接


    public enum DisconnectReason {
        TaskEnd,//任务结束
        ManuallyClosed,//手动关闭
        RegisterFailed,//注册失败
        AuthFailed,//认证失败
        Unknown //未知错误
    }
}
