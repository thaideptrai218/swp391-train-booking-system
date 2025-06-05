package vn.vnrailway.utils;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {

    /**
     * Hashes a plain text password using BCrypt with a securely generated salt.
     *
     * @param plainPassword The plain text password to hash.
     * @return The hashed password.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12)); // 12 rounds is common
    }

    /**
     * Verifies a plain text password against a hashed password.
     *
     * @param plainPassword The plain text password to check.
     * @param hashedPassword The hashed password to compare against.
     * @return true if the plain password matches the hashed password, false otherwise.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static void main(String[] args) {
        // Example usage:
        String password = "mysecretpassword";
        String hashedPassword = hashPassword(password);
        System.out.println("Hashed password: " + hashedPassword);

        boolean isMatch = checkPassword(password, hashedPassword);
        System.out.println("Password matches: " + isMatch); // true

        boolean isMismatch = checkPassword("wrongpassword", hashedPassword);
        System.out.println("Wrong password matches: " + isMismatch); // false
    }
}
