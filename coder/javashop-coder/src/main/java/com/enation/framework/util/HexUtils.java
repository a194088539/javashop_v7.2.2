package com.enation.framework.util;

import java.io.UnsupportedEncodingException;

/**
 * Created with IntelliJ IDEA.
 * User: Dawei
 * Date: 13-10-27
 * Time: 下午3:34
 * To change this template use File | Settings | File Templates.
 */
public class HexUtils {

    private static final char[] hexLookUp = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将字节数组转换为Hex字符串
     * @param bytes
     * @return
     */
    public static String bytesToHexStr(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            stringBuffer.append(hexLookUp[(bytes[i] >>> 4) & 0x0f]);
            stringBuffer.append(hexLookUp[bytes[i] & 0x0f]);
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
    
    public static void main(String[] args) throws UnsupportedEncodingException {
		//System.out.println(bytesToHexStr("alert('易族智汇')".getBytes("UTF-8")) );
	}
}
