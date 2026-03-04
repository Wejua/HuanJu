package jieyi.lu.huanjuweflux.RTC;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class MessageEncryptor {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_TAG_LENGTH = 128;
    private static final int IV_LENGTH = 12;
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;

    @Value("${security.encryption.password}")
    private String encryptionPassword;

    @Value("${security.encryption.salt}")
    private String encryptionSalt;

    private SecretKey secretKey;

    private SecretKey getSecretKey() throws Exception {
        if (secretKey == null) {
            PBEKeySpec spec = new PBEKeySpec(
                    encryptionPassword.toCharArray(),
                    encryptionSalt.getBytes(),
                    ITERATION_COUNT,
                    KEY_LENGTH
            );
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            secretKey = new SecretKeySpec(keyBytes, "AES");
        }
        return secretKey;
    }

    /**
     * 加密消息
     */
    public String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), parameterSpec);

        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        // 组合 IV 和密文
        byte[] combined = new byte[IV_LENGTH + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, IV_LENGTH);
        System.arraycopy(cipherText, 0, combined, IV_LENGTH, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 解密消息
     */
    public String decrypt(String encryptedData) throws Exception {
        byte[] combined = Base64.getDecoder().decode(encryptedData);

        byte[] iv = new byte[IV_LENGTH];
        byte[] cipherText = new byte[combined.length - IV_LENGTH];

        System.arraycopy(combined, 0, iv, 0, IV_LENGTH);
        System.arraycopy(combined, IV_LENGTH, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), parameterSpec);

        byte[] plainText = cipher.doFinal(cipherText);
        return new String(plainText);
    }
}
