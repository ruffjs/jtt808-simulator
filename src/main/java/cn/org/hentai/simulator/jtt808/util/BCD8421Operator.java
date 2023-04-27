package cn.org.hentai.simulator.jtt808.util;

public class BCD8421Operator {


    public static String bcd2String(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);
        for (byte aByte : bytes) {
            temp.append((aByte & 0xF0) >>> 4);
            temp.append(aByte & 0xF);
        }
        return temp.toString();
    }


    public static byte[] string2Bcd(String str) {
        if ((str.length() & 0x1) == 1) {
            str = "0" + str;
        }

        byte[] ret = new byte[str.length() / 2];
        byte[] bs = str.getBytes();
        for (int i = 0; i < ret.length; i++) {
            byte high = ascII2Bcd(bs[(2 * i)]);
            byte low = ascII2Bcd(bs[(2 * i + 1)]);


            ret[i] = ((byte) (high << 4 | low));
        }
        return ret;
    }

    private static byte ascII2Bcd(byte asc) {
        if ((asc >= 48) && (asc <= 57))
            return (byte) (asc - 48);
        if ((asc >= 65) && (asc <= 70))
            return (byte) (asc - 65 + 10);
        if ((asc >= 97) && (asc <= 102)) {
            return (byte) (asc - 97 + 10);
        }
        return (byte) (asc - 48);
    }
}
