package pack;

import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class FileIO {

    // Save PasswordVault to file, with encryption
    public static void saveToFile(PasswordVault vault, String filename, SecretKey key, byte[] salt) throws Exception {
        try {
            // Serialize the vault to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(vault);
            } // The ObjectOutputStream is closed automatically

            // Generate IV and encrypt the data
            byte[] iv = CryptoUtils.generateIV(); // Ensure this method doesn't throw an exception
            byte[] encryptedData = CryptoUtils.encrypt(bos.toByteArray(), key, iv); // Check for exceptions here

            // Write salt, IV, and encrypted data to the file
            try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
                fileOutputStream.write(salt);
                fileOutputStream.write(encryptedData);
            } // The FileOutputStream is closed automatically
        } catch (Exception ex) {
            System.out.println("An error occurred while saving the vault: " + ex.getMessage());
            throw ex;
        }
    }

    // Load PasswordVault from file, with decryption
    public static PasswordVault loadFromFile(String filename, SecretKey key) throws Exception {
        // Read the file data
        byte[] fileData = Files.readAllBytes(Paths.get(filename));

        // Extract the salt, IV, and encrypted data
        byte[] encryptedData = Arrays.copyOfRange(fileData, CryptoUtils.SALT_LENGTH, fileData.length);

        // Decrypt the data
        byte[] decryptedData = CryptoUtils.decrypt(encryptedData, key);

        // Deserialize the decrypted data
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(decryptedData))) {
            return (PasswordVault) ois.readObject();
        } // try-with-resources will automatically close the ObjectInputStream
    }

    // Get the salt from an encrypted file
    public static byte[] getSaltFromEncryptedFile(String filename) throws IOException {
        // Create a FileInputStream to read the file
        try (FileInputStream fis = new FileInputStream(filename)) {
            // Create a byte array to store the salt
            byte[] salt = new byte[CryptoUtils.SALT_LENGTH];
            // Read the salt from the file
            int read = fis.read(salt);
            if (read != CryptoUtils.SALT_LENGTH) {
                throw new IOException("Could not read salt from file.");
            }
            return salt;
        }
    }
}