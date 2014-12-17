package com.meituan.masco.utils;

/**
 * Created by lihuihui on 14-12-16.
 */

import org.apache.commons.lang3.ArrayUtils;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSATool {

    public static void generateRsaKeyFile(String pubkeyfile, String privatekeyfile)
            throws NoSuchAlgorithmException, FileNotFoundException, IOException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为1024位
        keyPairGen.initialize(1024);
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 得到私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 得到公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 生成私钥
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(
                privatekeyfile));
        oos.writeObject(privateKey);
        oos.flush();
        oos.close();

        oos = new ObjectOutputStream(new FileOutputStream(pubkeyfile));
        oos.writeObject(publicKey);
        oos.flush();
        oos.close();

        System.out.println("make file ok!");
    }

    /**
     *
     * @param k
     * @param data
     * @param encrypt
     *            1 加密 0解密
     * @return
     * @throws javax.crypto.NoSuchPaddingException
     * @throws Exception
     */
    public static byte[] handleData(Key k, byte[] data, int encrypt)
            throws Exception {

        if (k != null) {

            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            byte[] resultBytes = new byte[] {};
            if (encrypt == 1) {
                cipher.init(Cipher.ENCRYPT_MODE, k);
                for (int i = 0; i < data.length; i += 100) {
                    byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,
                            i + 100));
                    resultBytes = ArrayUtils.addAll(resultBytes, doFinal);
                }
               return resultBytes;
            } else if (encrypt == 0) {
                cipher.init(Cipher.DECRYPT_MODE, k);
                for (int i = 0; i < data.length; i += 128) {
                    byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i,
                            i + 128));
                    resultBytes = ArrayUtils.addAll(resultBytes, doFinal);
                }
                return resultBytes;
            } else {
                System.out.println("参数必须为: 1 加密 0解密");
            }
        }
        return null;
    }

   public static byte[] encrypt(byte[] data)throws Exception{
       RSAPublicKey pubkey = getRsaPublicKey();
       System.out.println("原文: " + new String(data));
       byte[] result = handleData(pubkey, data, 1);
       System.out.println("加密:" + new String(result));
       return result;
   }

    public static byte[] decrypt(byte[] data)throws Exception{
        RSAPrivateKey privateKey=getRsaPrivateKey();
        System.out.println("原文:" + new String(data));
        byte[] deresult = handleData(privateKey, data, 0);
        System.out.println("解密: " + new String(deresult));
        return deresult;
    }

    private static RSAPublicKey getRsaPublicKey() throws IOException, ClassNotFoundException {
        String pubfile = "/Users/lihuihui/tmp/pub.key";
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                pubfile));
        RSAPublicKey pubkey = (RSAPublicKey) ois.readObject();
        ois.close();
        return pubkey;
    }

    private static RSAPrivateKey getRsaPrivateKey() throws IOException, ClassNotFoundException {
        String prifile = "/Users/lihuihui/tmp/pri.key";
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(prifile));
        RSAPrivateKey privateKey = (RSAPrivateKey) ois.readObject();
        ois.close();
        return privateKey;
    }

//    public static byte[] sign(byte[] data)throws Exception{
//        RSAPrivateKey privateKey=getRsaPrivateKey();
//        Signature sig=Signature.getInstance("SHA1WithRSA");
//        sig.initSign(privateKey);
//        sig.update(data);
//        byte[] sigDataBytes = sig.sign();
//        String signature = new String(sigDataBytes);
//        System.out.println("签名是:" + signature);
//        return sigDataBytes;
//    }
//
//    public static boolean signVerify(byte[] data,byte[]signData)throws Exception{
//        RSAPublicKey publicKey=getRsaPublicKey();
//        Signature sig=Signature.getInstance("SHA1WithRSA");
//        sig.initVerify(publicKey);
//        sig.update(data);
//        Boolean result=sig.verify(signData);
//        System.out.println("签名结果:"+result);
//        return result;
//    }

    public static byte[] signature(String token)throws Exception{
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(token.getBytes());
    }

    /**
     * 字节数组转化为大写16进制字符串
     *
     * @param b
     * @return
     */
    public static String byte2HexStr(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String s = Integer.toHexString(b[i] & 0xFF);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s.toUpperCase());
        }
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        //generateRsaKeyFile(pubfile, prifile);

        RSAPublicKey pubkey =getRsaPublicKey();
        RSAPrivateKey prikey = getRsaPrivateKey();

        String msg = "Hello,会会 Marlon!";
        String enc = "UTF-8";

        // 使用公钥加密
        byte[] result = encrypt(msg.getBytes(enc));
        // 使用私钥解密
        byte[] deresult = decrypt(result);

        msg = "嚯嚯";
        // 使用私钥加密
        byte[] result2 = encrypt(msg.getBytes(enc));
        //公钥解密
        byte[] deresult2 = decrypt(result2);

     }
}