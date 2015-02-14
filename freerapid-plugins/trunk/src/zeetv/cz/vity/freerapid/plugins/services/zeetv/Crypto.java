package cz.vity.freerapid.plugins.services.zeetv;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Crypto {
    private final static byte[] PASSWORD = "UTYHGFHYTRYTTETET".getBytes();
    private final static int KEY_SIZE = 8; // 8 words = 256-bit
    private final static int IV_SIZE = 4; // 4 words = 128-bit

    public static String decrypt(String cipherTextBase64, String saltHex) throws Exception {
        byte[] salt = Hex.decodeHex(saltHex.toCharArray());
        byte[] cipherText = Base64.decodeBase64(cipherTextBase64);
        byte[] key = new byte[KEY_SIZE * 4];
        byte[] iv = new byte[IV_SIZE * 4];
        evpKDF(salt, key, iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);
        byte[] byteDecrypted = cipher.doFinal(cipherText);
        return new String(byteDecrypted, "UTF-8");
    }

    /**
     * Copied from http://stackoverflow.com/a/27250883
     */
    private static byte[] evpKDF(byte[] salt, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException {
        return evpKDF(PASSWORD, KEY_SIZE, IV_SIZE, salt, 1, "MD5", resultKey, resultIv);
    }

    private static byte[] evpKDF(byte[] password, int keySize, int ivSize, byte[] salt, int iterations, String hashAlgorithm, byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException {
        int targetKeySize = keySize + ivSize;
        byte[] derivedBytes = new byte[targetKeySize * 4];
        int numberOfDerivedWords = 0;
        byte[] block = null;
        MessageDigest hasher = MessageDigest.getInstance(hashAlgorithm);
        while (numberOfDerivedWords < targetKeySize) {
            if (block != null) {
                hasher.update(block);
            }
            hasher.update(password);
            block = hasher.digest(salt);
            hasher.reset();

            // Iterations
            for (int i = 1; i < iterations; i++) {
                block = hasher.digest(block);
                hasher.reset();
            }

            System.arraycopy(block, 0, derivedBytes, numberOfDerivedWords * 4,
                    Math.min(block.length, (targetKeySize - numberOfDerivedWords) * 4));

            numberOfDerivedWords += block.length / 4;
        }

        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4);
        System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4);

        return derivedBytes; // key + iv
    }
}
