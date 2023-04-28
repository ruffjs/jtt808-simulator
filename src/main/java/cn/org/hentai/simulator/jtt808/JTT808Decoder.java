package cn.org.hentai.simulator.jtt808;

import cn.org.hentai.simulator.jtt808.util.BitOperator;
import cn.org.hentai.simulator.jtt808.util.MsgParseUtils;
import cn.org.hentai.simulator.util.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class JTT808Decoder
{

    private static final Logger log = LoggerFactory.getLogger(JTT808Decoder.class);

    public static JTT808Package bytesToPackageData(byte[] data) {
        JTT808Package pData = new JTT808Package();
        //数据主要由三部分组成
        //1. 消息头 16byte 或者12byte
        JTT808Package.MsgHeader msgHeader = parseMsgHeaderFromBytes(data);
        pData.setMsgHeader(msgHeader);
        int msgBodyByteStartIndex = 12;
        //2.消息内容
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        pData.setMsgBodyBytes(tmp);
        //3.校检码 1byte 去除分割符后最后一位就是校验码
        int checkSumInPkg = data[data.length - 1];
        int calculatedCheckSum = BitOperator.getCheckSum4JT808(data, 0, data.length - 1);
        pData.setCheckSum(checkSumInPkg);
        if (checkSumInPkg != calculatedCheckSum) {
            String scHex = String.format("%02x", checkSumInPkg);
            String calHex = String.format("%02x", calculatedCheckSum);
            String dataHex = ByteUtils.bytesToHex(data);
            log.error("检验码不一致,msgId:{}, pkg: {}, calculated: {}, data: {}", msgHeader.getMsgId(), scHex, calHex, dataHex);
        }
        return pData;
    }

    //消息头的解析
    private static JTT808Package.MsgHeader parseMsgHeaderFromBytes(byte[] data) {
        JTT808Package.MsgHeader msgHeader = new JTT808Package.MsgHeader();
        // 1. 消息ID word(16)
        // byte[] tmp = new byte[2];
        // System.arraycopy(data, 0, tmp, 0, 2);
        // msgHeader.setMsgId(this.bitOperator.twoBytesToInteger(tmp));
        msgHeader.setMsgId(MsgParseUtils.parseIntFromBytes(data, 0, 2));
        // 2. 消息体属性 word(16)=================>
        // System.arraycopy(data, 2, tmp, 0, 2);
        // int msgBodyProps = this.bitOperator.twoBytesToInteger(tmp);
        int msgBodyProps = MsgParseUtils.parseIntFromBytes(data, 2, 2);
        msgHeader.setMsgBodyPropsField(msgBodyProps);
        // [ 0-9 ] 0000,0011,1111,1111(3FF)(消息体长度)
        msgHeader.setMsgBodyLength(msgBodyProps & 0x3ff);
        // [10-12] 0001,1100,0000,0000(1C00)(加密类型)
        msgHeader.setEncryptionType((msgBodyProps & 0x1c00) >> 10);
        // [ 13_ ] 0010,0000,0000,0000(2000)(是否有子包)
        msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
        // [14-15] 1100,0000,0000,0000(C000)(保留位)
        msgHeader.setReservedBit(((msgBodyProps & 0xc000) >> 14) + "");
        // 消息体属性 word(16)<=================
        // 3. 终端手机号 bcd[6]
        // tmp = new byte[6];
        // System.arraycopy(data, 4, tmp, 0, 6);
        // msgHeader.setTerminalPhone(this.bcd8421Operater.bcd2String(tmp));
        msgHeader.setPhone(MsgParseUtils.parseBcdStringFromBytes(data, 4, 6));
        // 4. 消息流水号 word(16) 按发送顺序从 0 开始循环累加
        // tmp = new byte[2];
        // System.arraycopy(data, 10, tmp, 0, 2);
        // msgHeader.setFlowId(this.bitOperator.twoBytesToInteger(tmp));
        msgHeader.setFlowId(MsgParseUtils.parseIntFromBytes(data, 10, 2));
        // 5. 消息包封装项
        // 有子包信息
        if (msgHeader.isHasSubPackage()) {
            // 消息包封装项字段
            msgHeader.setPackageInfoField(MsgParseUtils.parseIntFromBytes(data, 12, 4));
            // byte[0-1] 消息包总数(word(16))
            // tmp = new byte[2];
            // System.arraycopy(data, 12, tmp, 0, 2);
            // msgHeader.setTotalSubPackage(this.bitOperator.twoBytesToInteger(tmp));
            msgHeader.setTotalSubPackage(MsgParseUtils.parseIntFromBytes(data, 12, 2));
            // byte[2-3] 包序号(word(16)) 从 1 开始
            // tmp = new byte[2];
            // System.arraycopy(data, 14, tmp, 0, 2);
            // msgHeader.setSubPackageSeq(this.bitOperator.twoBytesToInteger(tmp));
            msgHeader.setSubPackageSeq(MsgParseUtils.parseIntFromBytes(data, 14, 2));
        }
        return msgHeader;
    }
}
