package cn.org.hentai.simulator.task.net;

import cn.org.hentai.simulator.jtt808.JTT808Decoder;
import cn.org.hentai.simulator.jtt808.JTT808Message;
import cn.org.hentai.simulator.jtt808.JTT808Package;
import cn.org.hentai.simulator.jtt808.util.JT808ProtocolUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by matrixy when 2020/5/10.
 */
public class JT808MessageDecoder extends ByteToMessageDecoder {
    static Logger logger = LoggerFactory.getLogger(JT808MessageDecoder.class);

    // 缓冲区，消息体长度最大10位，再加上消息结构
    byte[] buffer = new byte[1024 + 32];

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() <= 0) {
            return;
        }
        //数据的读取
        byte[] b = new byte[in.readableBytes()];
        in.readBytes(b);
        byte[] db = JT808ProtocolUtils.doEscape4Receive(b, 0, b.length);
        JTT808Package pkd = JTT808Decoder.bytesToPackageData(db);

        JTT808Message msg = new JTT808Message();
        msg.id = pkd.getMsgHeader().getMsgId();
        msg.sim = pkd.getMsgHeader().getPhone();
        msg.sequence = pkd.getMsgHeader().getFlowId();
        msg.body = pkd.getMsgBodyBytes();
        out.add(msg);
    }
}