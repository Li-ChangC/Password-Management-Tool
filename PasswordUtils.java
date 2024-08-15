package pack;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PasswordUtils {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()_+";

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generatePassword(int length, boolean includeLowercase, boolean includeUppercase, boolean includeDigits, boolean includeSymbols) {
        if (length <= 0) {
            throw new IllegalArgumentException("Password length must be greater than 0.");
        }

        StringBuilder password = new StringBuilder(length);
        List<Character> charPool = new ArrayList<>();

        // Add character sets to the pool based on user selection
        if (includeLowercase) {
            charPool.addAll(LOWERCASE.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        }
        if (includeUppercase) {
            charPool.addAll(UPPERCASE.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        }
        if (includeDigits) {
            charPool.addAll(DIGITS.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        }
        if (includeSymbols) {
            charPool.addAll(SYMBOLS.chars().mapToObj(e -> (char) e).collect(Collectors.toList()));
        }

        // If no characters are selected, throw an exception
        if (charPool.isEmpty()) {
            throw new IllegalArgumentException("At least one character set must be selected.");
        }

        // Shuffle the pool to ensure randomness
        Collections.shuffle(charPool);

        // Generate the password
        for (int i = 0; i < length; i++) {
            password.append(charPool.get(RANDOM.nextInt(charPool.size())));
        }

        // Shuffle the final password to prevent predictability
        List<Character> finalPassword = password.chars()
                .mapToObj(e -> (char) e)
                .collect(Collectors.toList());
        Collections.shuffle(finalPassword);

        // Build the final password string
        return finalPassword.stream()
                .map(String::valueOf)
                .collect(Collectors.joining());
    }

    // Method to determine password strength
    public static double calculatePasswordStrength(String password) {
        int strongThreshold = 12;
        int moderateThreshold = 8;

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*()_+].*");
        boolean hasSequential = password.matches(".*(012|123|234|345|456|567|678|789|890|abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz).*");
        boolean hasRepeated = password.matches(".*(.)\\1{2,}.*");
        boolean hasSimpleRepeatPattern = password.matches("(\\b\\w+\\b)\\1+");
        boolean isCommonPassword = COMMON_PASSWORDS.contains(password);

        int strengthScore = (hasUppercase ? 1 : 0) + (hasLowercase ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSymbol ? 1 : 0);
        double strength = 0.0;

        if (password.length() >= strongThreshold) {
            strength += 0.5;
        } else if (password.length() >= moderateThreshold) {
            strength += 0.3;
        }

        double diversityBonus = 0.5 * (strengthScore / 4.0);
        strength += diversityBonus;

        // If the password is a common password, set the strength to very low, otherwise reduce the strength
        if (isCommonPassword) {
            strength = 0.1;
        } else if (hasSequential || hasRepeated || hasSimpleRepeatPattern) {
            strength -= 0.1;
        }

        // Ensure the strength is within the range [0, 1]
        strength = Math.min(1.0, strength);
        strength = Math.max(0, strength);

        return strength;
    }

    // Method to get the text representation of the password strength
    public static String getStrengthText(double strengthValue) {
        if (strengthValue < 0.3) {
            return "Weak";
        } else if (strengthValue < 0.6) {
            return "Medium";
        } else {
            return "Strong";
        }
    }

    // Common passwords to check against
    private static final Set<String> COMMON_PASSWORDS = Set.of(
            "123456", "password", "123456789", "12345", "12345678",
            "qwerty", "1234567", "111111", "123123", "abc123",
            "1234", "password1", "1234567890"
    );

}
