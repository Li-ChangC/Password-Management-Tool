package pack;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.security.SecureRandom;
import java.util.Arrays;

// Description: Utility class for encryption and decryption using AES-GCM.
public class CryptoUtils {
    private static final String AES = "AES";
    private static final int AES_KEY_SIZE = 256; // AES length
    public static final int GCM_NONCE_LENGTH = 12; // GCM IV length
    private static final int GCM_TAG_LENGTH = 16; // GCM tag length
    public static final int SALT_LENGTH = 16; // 

    // Generate IV
    public static byte[] generateIV() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] nonce = new byte[GCM_NONCE_LENGTH];
        secureRandom.nextBytes(nonce);
        return nonce;
    }

    // Generate salt
    public static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    // Generate AES key
    public static byte[] encrypt(byte[] data, SecretKey key, byte[] nonce) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmParameterSpec);

        byte[] encryptedData = cipher.doFinal(data);
        byte[] encryptedDataWithNonce = new byte[nonce.length + encryptedData.length];
        System.arraycopy(nonce, 0, encryptedDataWithNonce, 0, nonce.length);
        System.arraycopy(encryptedData, 0, encryptedDataWithNonce, nonce.length, encryptedData.length);

        return encryptedDataWithNonce;
    }

    // Decrypt data
    public static byte[] decrypt(byte[] encryptedDataWithNonce, SecretKey key) throws Exception {
        if (encryptedDataWithNonce.length < GCM_NONCE_LENGTH + GCM_TAG_LENGTH) {
            throw new IllegalArgumentException("Invalid encrypted data length.");
        }

        byte[] nonce = Arrays.copyOfRange(encryptedDataWithNonce, 0, GCM_NONCE_LENGTH);
        byte[] encryptedData = Arrays.copyOfRange(encryptedDataWithNonce, GCM_NONCE_LENGTH, encryptedDataWithNonce.length);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, nonce);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmParameterSpec);

        return cipher.doFinal(encryptedData);
    }

    public static SecretKey deriveKeyFromPassword(String password, byte[] salt) throws Exception {
        // Create a PBEKeySpec object with the password and salt, and specify the number of iterations and key length
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        // Create a SecretKeyFactory object, specifying the algorithm used
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        // Generate the secret key from the PBEKeySpec object
        SecretKey secretKey = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secretKey;
    }
}
