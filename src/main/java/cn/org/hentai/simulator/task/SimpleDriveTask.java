package cn.org.hentai.simulator.task;

import cn.org.hentai.simulator.entity.Point;
import cn.org.hentai.simulator.jtt808.JTT808Encoder;
import cn.org.hentai.simulator.jtt808.JTT808Message;
import cn.org.hentai.simulator.task.event.EventEnum;
import cn.org.hentai.simulator.task.event.Listen;
import cn.org.hentai.simulator.task.event.EventDispatcher;
import cn.org.hentai.simulator.task.log.LogType;
import cn.org.hentai.simulator.task.net.JT808MessageEncoder;
import cn.org.hentai.simulator.task.runner.Executable;
import cn.org.hentai.simulator.task.net.ConnectionPool;
import cn.org.hentai.simulator.util.ByteUtils;
import cn.org.hentai.simulator.util.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by matrixy when 2020/5/9.
 */
public class SimpleDriveTask extends AbstractDriveTask
{
    static Logger logger = LoggerFactory.getLogger(SimpleDriveTask.class);

    // 部标808协议连接id
    String connectionId;

    // 1078多媒体传输协议接连id
    String multimediaConnectionId;

    // 消息包流水号
    int sequence = 1;

    // 最后发送的消息ID
    int lastSentMessageId = 0;

    // 连接池
    ConnectionPool pool = ConnectionPool.getInstance();

    SimpleDateFormat sdf = new SimpleDateFormat("YYMMddHHmmss");

    final JTT808Message GENERAL_RESPONSE = new JTT808Message(0x0001);

    public SimpleDriveTask(long id, long routeId)
    {
        super(id, routeId);
    }

    @Override
    public void startup()
    {
        EventDispatcher.register(this);
        connectionId = pool.connect(getParameter("server.address"), Integer.parseInt(getParameter("server.port")), this);
    }

    @Override
    public void terminate()
    {
        super.terminate();
        pool.close(connectionId);
    }

    // 通用下行消息回调，先执行这个方法，后再按消息ID进行路由，所以最好不要在这里做应答
    @Listen(when = EventEnum.message_received)
    public void onServerMessage(JTT808Message msg)
    {
        log(LogType.MESSAGE_IN, ByteUtils.toString(JTT808Encoder.encode(msg)));
    }

    @Listen(when = EventEnum.connected)
    public void onConnected()
    {
        log(LogType.STATE, "connected");
        // 连接成功时，发送注册消息
        String sn = getParameter("device.sn");
        byte[] vin = new byte[0];
        try { vin = getParameter("vehicle.number").getBytes("GBK"); } catch(Exception ex) { }

        JTT808Message msg = new JTT808Message();
        msg.id = 0x0100;
        msg.body = Packet.create(64)
                .addShort((short)0x0001)
                .addShort((short)0x0001)
                .addBytes("CHINA".getBytes(), 5)
                .addBytes("HENTAI-SIMULATOR".getBytes(), 20)
                .addBytes(sn.getBytes(), 7)
                .addByte((byte)0x01)
                .addBytes(vin)
                .getBytes();

        send(msg);
    }

    @Listen(when = EventEnum.message_received, attachment = "8001")
    public void onGenericResponse(JTT808Message msg)
    {
        int answerSequence = ByteUtils.getShort(msg.body, 0, 2) & 0xffff;
        int answerMessageId = ByteUtils.getShort(msg.body, 2, 2) & 0xffff;
        int result = msg.body[4] & 0xff;
        logger.debug(String.format("answer -> seq: %4d, id: %04x, result: %02d", answerSequence, answerMessageId, result));

        // TODO: 应该整个hashmap保存上一次发送的消息ID，KEY为流水号
        switch (lastSentMessageId)
        {
            // 其它就不管了
        }

        lastSentMessageId = 0;
    }

    // 注册应答时
    @Listen(when = EventEnum.message_received, attachment = "8100")
    public void onRegisterResponsed(JTT808Message msg)
    {
        int result = msg.body[2] & 0xff;
        if (result == 0x00)
        {
            log(LogType.STATE, "registered");
            startSession();
        }
        else
        {
            log(LogType.EXCEPTION, "register failed");
            terminate();
        }
    }

    @Listen(when = EventEnum.disconnected)
    public void onDisconnected()
    {
        log(LogType.EXCEPTION, "disconnected");
        terminate();
    }

    // 接收到文本信息
    @Listen(when = EventEnum.message_received, attachment = "8300")
    public void onTTSMessage(JTT808Message msg)
    {
        Packet p = Packet.create(msg.body);
        int flag = p.nextByte() & 0xff;
        String text = null;
        try { text = new String(p.nextBytes(), "GBK"); } catch(Exception ex) { }
        boolean emergency = (flag & (1 << 0)) > 0;
        boolean display = (flag & (1 << 2)) > 0;
        boolean tts = (flag & (1 << 3)) > 0;
        boolean adScreen = (flag & (1 << 4)) > 0;
        boolean CANCode = (flag & (1 << 5)) > 0;
        String log = "标志：";
        if (emergency) log += "紧急，";
        if (display) log += "终端显示器显示，";
        if (tts) log += "终端TTS播读，";
        if (adScreen) log += "广告屏显示，";
        log += CANCode ? "CAN故障码，" : "中心导航信息，";
        log(LogType.STATE, log + "文本：" + text);

        // 回应一下
        GENERAL_RESPONSE.body = Packet.create(5).addShort((short) msg.sequence).addShort((short) msg.id).addByte((byte) 0x00).getBytes();
        send(GENERAL_RESPONSE);
    }

    // 开始正常会话，发送心跳与位置
    protected void startSession()
    {
        // 暂时先屏蔽掉，没发送心跳消息就暂时先不执行了
        /*
        executeConstantly(new Executable()
        {
            @Override
            public void execute(AbstractDriveTask driveTask)
            {
                ((SimpleDriveTask)driveTask).heartbeat();
            }
        }, 30000);
        */
        reportLocation();
    }

    public void reportLocation()
    {
        final Point point = getNextPoint();
        if (point == null)
        {
            // 10分钟后再关闭
            executeAfter(new Executable()
            {
                @Override
                public void execute(AbstractDriveTask driveTask)
                {
                    terminate();
                }
            }, 1000 * 60 * 10);
            return;
        }

        executeAfter(new Executable()
        {
            @Override
            public void execute(AbstractDriveTask driveTask)
            {
                JTT808Message msg = new JTT808Message();
                msg.id = 0x0200;
                Packet p = Packet.create(128)
                        .addInt(point.getWarnFlags() | getWarningFlags())                               // DWORD, 报警标志位
                        .addInt(point.getStatus() | getStateFlags())                                    // DWORD，状态
                        .addInt((int)(point.getLatitude() * 100_0000))                                  // DWORD，纬度
                        .addInt((int)(point.getLongitude() * 100_0000))                                 // DWORD，经度
                        .addShort((short)0)                                                             // WORD，海拔
                        .addShort((short)(point.getSpeed() * 10))                                       // WORD，速度
                        .addShort((short)point.getDirection())                                          // WORD，方向，暂未计算
                        .addBytes(ByteUtils.toBCD(sdf.format(new Date(point.getReportTime()))))         // BCD[6]，时间
                        ;
                // TODO: 增加附加信息
                msg.body = p.getBytes();
                send(msg);

                reportLocation();
            }
        }, (int)Math.max(point.getReportTime() - System.currentTimeMillis(), 0));
    }

    public void heartbeat()
    {
        // TODO: 需要完成心跳消息
        logger.debug("{}: heartbeat...", getParameter("device.sn"));
    }

    @Override
    public void send(JTT808Message msg)
    {
        try
        {
            msg.sim = getParameter("device.sim");
            msg.sequence = (short)((sequence++) & 0xffff);
            pool.send(connectionId, msg);

            lastSentMessageId = msg.id;

            logger.info("send: {} -> {} : {}", msg.sim, msg.sequence, String.format("%04x", msg.id));

            log(LogType.MESSAGE_OUT, ByteUtils.toString(JTT808Encoder.encode(msg)));
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}