package com.truthower.suhang.mangareader.utils;


import java.io.ByteArrayOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author yanshaodong@hkrt.cn
 * @version 1.0
 * @Title: ThreeDESUtil.java
 * @Package com.icardpay.util
 * @Description: 字符�?DESede(3DES) 加密.
 * @date 2013-9-25
 */
public class ThreeDESUtil {
    private static final String Algorithm = "DESede"; // 定义 加密算法,可用
    // DES,DESede,Blowfish
    private static final String hexString = "0123456789ABCDEF";

    /**
     * @param keybyte 加密密钥，长度为24字节
     * @param src     字节数组(根据给定的字节数组构造一个密钥�? )
     * @return
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        try {
            // 根据给定的字节数组和算法构�?�?��密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 加密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * @param keybyte 密钥
     * @param src     �?��解密的数�?
     * @return
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, Algorithm);
            // 解密
            Cipher c1 = Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (java.security.NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (javax.crypto.NoSuchPaddingException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return null;
    }

    /**
     * 字符串转�?6进制
     *
     * @param str
     * @return
     */
    public static String encode(String str) {
        // 根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);

        // 将字节数组中每个字节拆解�?�?6进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * @param bytes
     * @return �?6进制数字解码成字符串,适用于所有字符（包括中文�?
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(
                bytes.length() / 2);
        // 将每2�?6进制整数组装成一个字�?
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString
                    .indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;
            else
                hs = hs + stmp;
            if (n < b.length - 1)
                hs = hs + ":";
        }
        return hs.toUpperCase();
    }

//    public static void main(String[] args) {
//        // 添加新安全算�?如果用JCE就要把它添加进去
//        // 这里addProvider方法是增加一个新的加密算法提供�?(个人理解没有找到好的答案,求补�?
//        // Security.addProvider(new com.sun.crypto.provider.SunJCE());
//        // byte数组(用来生成密钥�?
//        // final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte)0x88, 0x10,
//        // 0x40, 0x38, 0x28, 0x25, 0x79, 0x51, (byte) 0xCB, (byte) 0xDD,
//        // 0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36,
//        // (byte) 0xE2 };
//
//        byte[] keyBytes = Configure.des_pre_key.getBytes();
//        String szSrc = "16000006225881014235625";
//
//        StringBuilder sb1 = new StringBuilder();
//        for (int i = 0; i < keyBytes.length; i++) {
//            sb1.append(toHex(keyBytes[i]));
//        }
//        System.out.println("key :" + sb1.toString());
//
//        byte[] val = szSrc.getBytes();
//        StringBuilder sb2 = new StringBuilder();
//        for (int i = 0; i < val.length; i++) {
//            sb2.append(toHex(val[i]));
//        }
//        System.out.println("value : " + sb2.toString());
//
//        byte[] encoded = encryptMode(keyBytes, szSrc.getBytes());
//        System.out.println("加密后的字符�?" + new String(encoded));
//
//        // StringBuilder sb1 = new StringBuilder();
//        //
//        // for (int i = 0; i < encoded.length; i++) {
//        // sb1.append(toHex(encoded[i]));
//        // }
//        // System.out.println(new String(sb1));
//        //
//        String decString = "FBC7BE2F41171BDD8DD39254BF197BFB66AE98CDBB59C99B";
//
//        byte[] bb = hexStringToByte(decString);
//        byte[] srcBytes = decryptMode(keyBytes, bb);
//        System.out.println("解密后的字符�?" + new String(srcBytes));
//        // ------------------------------------------------------------
//        String byt = "字符串转换为16进制";
//        byt = encode(byt);
//        System.out.println(byt);
//        byt = decode(byt);
//        System.out.println(byt);
//
//    }

    /**
     * 3des加密之后再base64加密，最后返回字符串
     *
     * @param key
     * @param data
     * @return
     */
    public static String encode(String key, String data) {
        byte[] keyBytes = key.getBytes();
        byte[] dataBytes = data.getBytes();
        byte[] encoded = encryptMode(keyBytes, dataBytes);
        byte[] base64 = Base64.encodeBase64(encoded);
        return new String(base64);
    }

    /**
     * 解密（base64 + DES�?
     *
     * @param key
     * @param data
     * @return
     */
    public static String decode(String key, String data) {
        byte[] des3 = Base64.decodeBase64(data.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < des3.length; i++) {
            sb.append(ThreeDESUtil.toHex(des3[i]));
        }
        // Logger.i("==========toHex : " + sb.toString() + "===========");

        byte[] cardNoHex = ThreeDESUtil.hexStringToByte(sb.toString());

        // Logger.i("============hex to byte : " + new String(cardNoHex) +
        // "==========");
        byte[] srcBytes = ThreeDESUtil.decryptMode(key.getBytes(), cardNoHex);
        return new String(srcBytes);
    }

    public static final String toHex(byte b) {
        return ("" + "0123456789ABCDEF".charAt(0xf & b >> 4) + "0123456789ABCDEF"
                .charAt(b & 0xf));
    }

    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }
}