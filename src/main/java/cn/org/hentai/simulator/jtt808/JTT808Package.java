package cn.org.hentai.simulator.jtt808;


import io.netty.channel.Channel;

import java.util.Arrays;

/**
 * 消息体的封装
 */
public class JTT808Package {

    //消息头内容
    protected MsgHeader msgHeader;
    //消息体字节数组
    protected byte[] msgBodyBytes;
    //校检码
    protected int checkSum;

    protected Channel channel;

    public MsgHeader getMsgHeader() {
        return msgHeader;
    }

    public void setMsgHeader(MsgHeader msgHeader) {
        this.msgHeader = msgHeader;
    }

    public byte[] getMsgBodyBytes() {
        return msgBodyBytes;
    }

    public void setMsgBodyBytes(byte[] msgBodyBytes) {
        this.msgBodyBytes = msgBodyBytes;
    }

    public int getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(int checkSum) {
        this.checkSum = checkSum;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "PackageData{" +
                "msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", channel=" + channel +
                '}';
    }

    public static class MsgHeader {
        //1. 消息ID 字节位byte[0,1]
        protected int msgId;

        //2. 消息体属性,byte[2,3]  等价于2byte =  16bit
        //=========消息体属性 start==========
        protected int msgBodyPropsField;
        //消息体包括16bit位
        //2.1 消息体长度：[0-9]bit
        protected int msgBodyLength;
        //2.2 加密方式: [10-12]
        protected int encryptionType;
        //2.3 分包： [13]
        protected boolean hasSubPackage;
        //2.4 保留位 [14-15]
        protected String reservedBit;
        //=========消息体属性 end==========
        //3. 终端手机号(车辆ID) byte[4-9]
        protected String phone;
        //4. 流水号 byte[10-11]
        protected int flowId;

        //5. 消息包封装项 byte[12-15]
        protected int packageInfoField;
        //=========消息包封装项 start=========
        //5.1 消息包总数 word(16) byte[0-1]
        protected long totalSubPackage;
        //5.2 包序号 word(16) byte[2-3]
        protected long subPackageSeq;
        //=========消息包封装项 start=========

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public int getMsgBodyPropsField() {
            return msgBodyPropsField;
        }

        public void setMsgBodyPropsField(int msgBodyPropsField) {
            this.msgBodyPropsField = msgBodyPropsField;
        }

        public int getMsgBodyLength() {
            return msgBodyLength;
        }

        public void setMsgBodyLength(int msgBodyLength) {
            this.msgBodyLength = msgBodyLength;
        }

        public int getEncryptionType() {
            return encryptionType;
        }

        public void setEncryptionType(int encryptionType) {
            this.encryptionType = encryptionType;
        }

        public boolean isHasSubPackage() {
            return hasSubPackage;
        }

        public void setHasSubPackage(boolean hasSubPackage) {
            this.hasSubPackage = hasSubPackage;
        }

        public String getReservedBit() {
            return reservedBit;
        }

        public void setReservedBit(String reservedBit) {
            this.reservedBit = reservedBit;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public int getFlowId() {
            return flowId;
        }

        public void setFlowId(int flowId) {
            this.flowId = flowId;
        }

        public int getPackageInfoField() {
            return packageInfoField;
        }

        public void setPackageInfoField(int packageInfoField) {
            this.packageInfoField = packageInfoField;
        }

        public long getTotalSubPackage() {
            return totalSubPackage;
        }

        public void setTotalSubPackage(long totalSubPackage) {
            this.totalSubPackage = totalSubPackage;
        }

        public long getSubPackageSeq() {
            return subPackageSeq;
        }

        public void setSubPackageSeq(long subPackageSeq) {
            this.subPackageSeq = subPackageSeq;
        }

        @Override
        public String toString() {
            return "MsgHeader{" +
                    "msgId=" + msgId +
                    ", msgBodyPropsField=" + msgBodyPropsField +
                    ", msgBodyLenth=" + msgBodyLength +
                    ", encryptionType=" + encryptionType +
                    ", hasSubPackage=" + hasSubPackage +
                    ", reservedBit='" + reservedBit + '\'' +
                    ", phone='" + phone + '\'' +
                    ", flowId=" + flowId +
                    ", packageInfoField=" + packageInfoField +
                    ", totalSubPackage=" + totalSubPackage +
                    ", subPackageSeq=" + subPackageSeq +
                    '}';
        }
    }
}
