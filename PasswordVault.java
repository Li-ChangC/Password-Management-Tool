package pack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.io.Serializable;
import java.time.LocalDateTime;

// Description: Encapsulates the encrypted storage where user passwords are kept.
public class PasswordVault implements Serializable {

    // Attributes
    private List<Password> passwords;

    // Constructor
    public PasswordVault() {
        this.passwords = new ArrayList<>();
    }

    // Getters
    public List<Password> getPasswords() {
        return passwords;
    }

    // Methods
    // Add a new password to the vault
    public void addPassword(Password password) {
        this.passwords.add(password);
    }

    // Delete a password from the vault
    public void deletePassword(UUID identifier) {
        passwords.removeIf(password -> password.getIdentifier().equals(identifier));
    }

    // Update a password in the vault
    public void updatePassword(UUID identifier, String newPassword) {
        passwords.stream()
                .filter(password -> password.getIdentifier().equals(identifier))
                .findFirst()
                .ifPresent(password -> password.setPassword(newPassword));
    }

    // Search for passwords by a keyword in username or URL
    public List<Password> searchPasswords(String keyword) {
        String lowerCaseKeyword = keyword.toLowerCase();
        return passwords.stream()
                .filter(password ->
                        password.getUsername().toLowerCase().contains(lowerCaseKeyword) ||
                                password.getUrl().toLowerCase().contains(lowerCaseKeyword) ||
                                password.getPassword().toLowerCase().contains(lowerCaseKeyword))
                .collect(Collectors.toList());
    }

    // Retrieve a password from the vault by its identifier
    public Password getPassword(UUID identifier) {
        return passwords.stream()
                .filter(password -> password.getIdentifier().equals(identifier))
                .findFirst()
                .orElse(null);
    }

    public void addGeneratedPassword(String username, String url, int length, boolean includeLowercase, boolean includeUppercase, boolean includeDigits, boolean includeSymbols) {
        // Generate a new password
        String generatedPassword = PasswordUtils.generatePassword(length, includeLowercase, includeUppercase, includeDigits, includeSymbols);

        // Calculate the strength of the generated password
        double strength = PasswordUtils.calculatePasswordStrength(generatedPassword);

        // Create a new Password object
        Password newPassword = new Password(username, url, generatedPassword, strength);

        addPassword(newPassword);
    }
}
