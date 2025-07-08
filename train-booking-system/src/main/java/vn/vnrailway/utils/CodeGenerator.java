package vn.vnrailway.utils;

import java.security.SecureRandom;
import java.util.Random;

public class CodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 7;
    private static final Random random = new SecureRandom();

    /**
     * Generates a random booking code of a specified length using uppercase letters and numbers.
     * 
     * @return A randomly generated booking code string.
     */
    public static String generateBookingCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    // Optional: Main method for testing the generator
    public static void main(String[] args) {
        System.out.println("Generated Booking Code (Test 1): " + generateBookingCode());
        System.out.println("Generated Booking Code (Test 2): " + generateBookingCode());
        System.out.println("Generated Booking Code (Test 3): " + generateBookingCode());
    }
}
