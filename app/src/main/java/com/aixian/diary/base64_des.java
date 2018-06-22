package com.aixian.diary;

        import java.security.Key;
        import javax.crypto.Cipher;
        import javax.crypto.SecretKeyFactory;
        import javax.crypto.spec.DESedeKeySpec;
        import javax.crypto.spec.IvParameterSpec;
        import android.util.Base64;
        import android.util.Log;


public class base64_des {
    private static byte[] key=Base64.decode("YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4".getBytes(),Base64.DEFAULT);
   /* private static byte[] keyiv = { 1, 2, 3, 4, 5, 6, 7, 8 };*/
    /**
     * ECB加密,不要IV
     * @param key 0
     * @param data 明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeECB(byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, deskey);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }

    /**
     * ECB解密,不要IV
     * @param key 密钥
     * @param data Base64编码的密文
     * @return 明文
     * @throws Exception
     */
    public static byte[] ees3DecodeECB(byte[] data)
            throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/ECB/PKCS5Padding");
        Log.i("des", "decode init before");
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        Log.i("des", "decode init after" + new String(data,"UTF-8"));
        byte[] bOut = cipher.doFinal(data);
        Log.i("des", "decode doFinal after");
        return bOut;

    }
}


