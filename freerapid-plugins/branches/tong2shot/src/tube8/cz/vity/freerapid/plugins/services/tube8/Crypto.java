package cz.vity.freerapid.plugins.services.tube8;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

/**
 * @author tong2shot
 */
class Crypto {
    private SecretKey generateKey(byte[] pwBytes) throws Exception {
        byte[] _pwBytes = Arrays.copyOf(pwBytes, 32);
        SecretKey secretKey = new SecretKeySpec(_pwBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        _pwBytes = cipher.doFinal(_pwBytes);
        System.arraycopy(_pwBytes, 0, _pwBytes, 16, 16);
        return new SecretKeySpec(_pwBytes, "AES");
    }

    public String decrypt(String base64CipherText, String password) throws Exception {
        byte[] cipherTextBytes = Base64.decodeBase64(base64CipherText);
        byte[] nonceBytes = Arrays.copyOf(Arrays.copyOf(cipherTextBytes, 8), 16);
        IvParameterSpec nonce = new IvParameterSpec(nonceBytes);
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, generateKey(password.getBytes()), nonce);
        byte[] decrypted = cipher.doFinal(cipherTextBytes, 8, cipherTextBytes.length - 8);
        return new String(decrypted);
    }
}
