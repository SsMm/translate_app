package com.zgy.translate.utils;

/**
 * Created by zhouguangyue on 2017/12/20.
 */

public class ByteTransform {


    /*
     * 字节转10进制
     */
    public static int byte2Int(byte b){
        return (int) b;
    }

    /*
     * 10进制转字节
     */
    public static byte int2Byte(int i){
        return (byte) i;
    }

    /*
     * 字节数组转16进制字符串
     */
    public static String bytes2HexString(byte[] bt) {
        String r = "";

       /* for (int i = 0; i < bt.length; i++) {
            String hex = Integer.toHexString(bt[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }*/

        for (byte b : bt){
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    /*
    * 字符转换为字节
    */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /*
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }
    }

    /*
     * 字节数组转字符串
     */
    public static String bytes2String(byte[] b) throws Exception {
        return new String (b, "UTF-8");
    }

    //使用1字节就可以表示b
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进行数
    }
    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }
    //需要使用4字节表示b
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }

}
