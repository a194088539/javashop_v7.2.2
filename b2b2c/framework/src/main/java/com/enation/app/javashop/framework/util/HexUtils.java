package com.enation.app.javashop.framework.util;


import org.apache.commons.codec.binary.Hex;

/**
 * To change this template use File | Settings | File Templates.
 * @author Dawei
 * @version v1.0
 * @since v7.0.0
 * 2018年3月23日 上午10:26:41
 */
public class HexUtils {

    private static final char[] HEX_LOOK_UP = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将字节数组转换为Hex字符串
     * @param bytes
     * @return
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append(HEX_LOOK_UP[(bytes[i] >>> 4) & 0x0f]);
            stringBuffer.append(HEX_LOOK_UP[bytes[i] & 0x0f]);
        }
        return stringBuffer.toString();
    }

    /**
     * 将Hex字符串转换为字节数组
     * @param str
     * @return
     */
    public static byte[] hexStrToBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(str.substring(2 * i, 2 * i + 2),
                    16);
        }
        return bytes;
    }

    /**
     * 将字符串进行hex
     * @param str
     * @return
     */
    public static String encode(String str){
        try{


//			return new Hex().encodeHexString(str.getBytes("UTF-8"));
            return new String(Hex.encodeHex(str.getBytes("UTF-8")));
        }catch(Exception ex){
            return str;
        }
    }

    /**
     * 将hex后的字符串解密
     * @param encodedStr
     * @return
     */
    public static String decode(String encodedStr){
        try{
            return new String(new Hex().decode(encodedStr.getBytes()), "UTF-8");
        }catch(Exception ex){
            return encodedStr;
        }
    }
}
