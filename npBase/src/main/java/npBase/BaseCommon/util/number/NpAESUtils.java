package npBase.BaseCommon.util.number;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 */
public class NpAESUtils {

    /**
     * 加密
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encode(byte[] key, byte[] data) {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = null;//"算法/模式/补码方式"
        try {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(data);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




    /**
     * 解密
     *
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decode(byte[] key, byte[] data) {
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        Cipher cipher = null;//"算法/模式/补码方式"
        try {
            cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(data);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
