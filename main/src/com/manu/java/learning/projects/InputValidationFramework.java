package com.manu.java.learning.projects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InputValidationFramework {

    static final String CANCEL_COMMAND = "cancel";

    // Valid choices for gender field
    static final String[] VALID_GENDERS = { "MALE", "FEMALE", "OTHER" };

    // Valid choices for appointment type
    static final String[] VALID_APPOINTMENT_TYPES = {
            "CONSULTATION", "CHECKUP", "FOLLOWUP", "EMERGENCY"
    };

    // Valid choices for yes/no fields
    static final String[] VALID_YES_NO = { "YES", "NO" };

    // Phone pattern — Kenyan numbers: 07XXXXXXXX or 01XXXXXXXX
    static final String PHONE_PATTERN   = "^(07|01)[0-9]{8}$";
    static final String PHONE_HINT      = "Phone must be 10 digits starting with 07 or 01";

    // Date pattern — DD/MM/YYYY
    static final String DATE_PATTERN    = "^\\d{2}/\\d{2}/\\d{4}$";

    // Name pattern — letters and spaces only
    static final String NAME_PATTERN    = "^[a-zA-Z ]{2,50}$";
    static final String NAME_HINT       = "Name must be 2-50 letters and spaces only";

    static class FieldResult {
        boolean wasCancelled;
        String  value;

        private FieldResult(boolean wasCancelled, String value) {
            this.wasCancelled = wasCancelled;
            this.value        = value;
        }

        // user canceled
        static FieldResult cancelled() {
            return new FieldResult(true, null);
        }

        // user gave a value
        static FieldResult of(String value) {
            return new FieldResult(false, value);
        }
    }

    static final Scanner scanner = new Scanner(System.in);

    // MAIN MENU LOOP
    public static void main(String[] args) {
        System.out.println("Input Validation Framework Demo");
        // Main menu loop , keeps running until user chooses Exit
        while (true) {
            System.out.println("MAIN MENU");
            System.out.println("Enter 1.to Register User");
            System.out.println("Enter 2. Book Appointment");
            System.out.println("Enter 3. Exit");
            System.out.print("Enter choice: " );

            String menuInput = scanner.nextLine().trim();

            if (menuInput.equals("1")) {
                runUserRegistration();
            } else if (menuInput.equals("2")) {
                runAppointmentBooking();
            } else if (menuInput.equals("3")) {
                System.out.println("Goodbye! You've exit the program");
                break;
            } else {
                System.out.println("Invalid choice. Please enter 1, 2 or 3.");
            }
        }
    }

    // FORM 1 — USER REGISTRATION
    // Fields: name, age, phone, gender, newsletter
    static void runUserRegistration() {
        System.out.println(" USER REGISTRATION ");
        System.out.println("(type 'cancel' at any prompt to return to main menu)");

        // Outer loop — keeps re-prompting until all fields are valid
        while (true) {
            // We read everything before validating anything.
            FieldResult nameResult = readField("Full Name");
            if (nameResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult ageResult = readField("Age (1-120)");
            if (ageResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult phoneResult = readField("Phone (07XXXXXXXX)");
            if (phoneResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult genderResult = readField(
                    "Gender (" + String.join(", ", VALID_GENDERS) + ")");
            if (genderResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult newsletterResult = readField(
                    "Subscribe to newsletter? (YES / NO)");
            if (newsletterResult.wasCancelled) {
                printCancelled();
                return;
            }

            // Validate every field and collect ALL errors.
            List<String> allErrors = new ArrayList<>();

            // Validate name
            allErrors.addAll(
                    validatePattern(nameResult.value, NAME_PATTERN, NAME_HINT)
            );

            // Validate age — integer in range 1 to 120
            allErrors.addAll(
                    validateInteger(ageResult.value, 1, 120, "Age")
            );

            // Validate phone
            allErrors.addAll(
                    validatePattern(phoneResult.value, PHONE_PATTERN, PHONE_HINT)
            );

            // Validate gender — must be one of the valid choices
            allErrors.addAll(
                    validateChoice(genderResult.value, VALID_GENDERS, "Gender")
            );

            // Validate newsletter — must be YES or NO
            allErrors.addAll(
                    validateChoice(newsletterResult.value, VALID_YES_NO, "Newsletter")
            );

            if (!allErrors.isEmpty()) {
                // There were errors — show ALL of them, then loop back to re-prompt
                printAllErrors(allErrors);
                System.out.println("  Please correct the above and try again.\n");
                // Loop continues → user re-enters all fields

            } else {
                // All fields valid — show success summary
                System.out.println("Registration successful!");
                System.out.println("Name : " + nameResult.value);
                System.out.println("Age : " + ageResult.value);
                System.out.println("Phone : " + phoneResult.value);
                System.out.println("Gender : " + genderResult.value.toUpperCase());
                System.out.println("Newsletter : " + newsletterResult.value.toUpperCase());
                return;
            }
        }
    }

    // FORM 2 — APPOINTMENT BOOKING
    // Fields: name, date, time slot, type, notes
    static void runAppointmentBooking() {

        System.out.println("APPOINTMENT BOOKING");
        System.out.println("(type 'cancel' at any prompt to return to menu)");

        while (true) {
            // READ ALL FIELDS
            FieldResult nameResult = readField("Patient Name");
            if (nameResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult dateResult = readField("Appointment Date (DD/MM/YYYY)");
            if (dateResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult timeSlotResult = readField("Time Slot (hour 8-17, e.g. 9)");
            if (timeSlotResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult typeResult = readField(
                    "Appointment Type ("
                            + String.join(", ", VALID_APPOINTMENT_TYPES) + ")");
            if (typeResult.wasCancelled) {
                printCancelled();
                return;
            }

            FieldResult notesResult = readField(
                    "Notes (max 100 chars, or press Enter to skip)");
            if (notesResult.wasCancelled) {
                printCancelled();
                return;
            }

            // COLLECT ALL ERRORS
            List<String> allErrors = new ArrayList<>();

            // Validate name
            allErrors.addAll(
                    validatePattern(nameResult.value, NAME_PATTERN, NAME_HINT)
            );

            // Validate date format AND logical validity
            allErrors.addAll(
                    validateDate(dateResult.value)
            );

            // Validate time slot integer in range 8 to 17
            allErrors.addAll(
                    validateInteger(timeSlotResult.value, 8, 17, "Time slot (hour)")
            );

            // Validate appointment type
            allErrors.addAll(
                    validateChoice(typeResult.value,
                            VALID_APPOINTMENT_TYPES,
                            "Appointment type")
            );

            // Validate notes optional but if provided max 100 chars
            allErrors.addAll(
                    validateOptionalMaxLength(notesResult.value, 100, "Notes")
            );

            // SHOW ALL ERRORS OR ACCEPT
            if (!allErrors.isEmpty()) {
                printAllErrors(allErrors);
                System.out.println("  Please correct the above and try again.\n");

            } else {
                System.out.println(" Appointment booked!");
                System.out.println("Patient : " + nameResult.value);
                System.out.println("Date : " + dateResult.value);
                System.out.println("Time : " + timeSlotResult.value + ":00");
                System.out.println("Type : " + typeResult.value.toUpperCase());
                System.out.println("Notes : "
                        + (notesResult.value.isEmpty() ? "(none)" : notesResult.value));
                return;
            }
        }
    }

    // Prompts the user and reads one line of input.
    static FieldResult readField(String fieldLabel) {
        System.out.print("  " + fieldLabel + ": ");
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase(CANCEL_COMMAND)) {
            return FieldResult.cancelled();
        }

        return FieldResult.of(input);
    }

    // VALIDATOR 1 INTEGER IN RANGE
    static List<String> validateInteger(String input,
                                        int    min,
                                        int    max,
                                        String fieldName) {

        List<String> errors = new ArrayList<>();

        // Check if it is a valid integer WITHOUT using try/catch
        if (!isValidInteger(input)) {
            errors.add(fieldName + " must be a whole number (got: '"
                    + input + "')");
            return errors;
        }

        // Now safe to parse — guaranteed to succeed
        int number = Integer.parseInt(input);

        if (number < min) {
            errors.add(fieldName + " must be at least " + min
                    + " (got: " + number + ")");
        }

        if (number > max) {
            errors.add(fieldName + " must be at most " + max
                    + " (got: " + number + ")");
        }
        return errors;
    }

    // VALIDATOR 2 — DATE (DD/MM/YYYY)
    static List<String> validateDate(String input) {

        List<String> errors = new ArrayList<>();

        // Level 1 — check format first
        if (!input.matches(DATE_PATTERN)) {
            errors.add("Date must be in format DD/MM/YYYY (got: '" + input + "')");
            return errors; // cannot check logic if format is wrong
        }

        // Level 2 — parse the parts
        String[] parts = input.split("/");
        int day   = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year  = Integer.parseInt(parts[2]);

        // Validate year range
        if (year < 1900 || year > 2100) {
            errors.add("Year must be between 1900 and 2100 (got: " + year + ")");
        }

        // Validate month
        if (month < 1 || month > 12) {
            errors.add("Month must be between 01 and 12 (got: " + month + ")");
            return errors; // cannot validate day if month is invalid
        }

        // Validate day using days-in-month logic
        int maxDayInMonth = daysInMonth(month, year);
        if (day < 1 || day > maxDayInMonth) {
            errors.add("Day must be between 01 and " + maxDayInMonth
                    + " for month " + month + " (got: " + day + ")");
        }

        return errors;
    }

    // VALIDATOR 3 — CHOICE FROM LIST
    static List<String> validateChoice(String   input,
                                       String[] validOptions,
                                       String   fieldName) {

        List<String> errors = new ArrayList<>();

        // Check each valid option — case-insensitive
        for (String option : validOptions) {
            if (input.equalsIgnoreCase(option)) {
                return errors;
            }
        }

        // No match found
        errors.add(fieldName + " must be one of: "
                + Arrays.toString(validOptions)
                + " (got: '" + input + "')");

        return errors;
    }

    // VALIDATOR 4 REGEX PATTERN
    static List<String> validatePattern(String input,
                                        String regex,
                                        String hint) {

        List<String> errors = new ArrayList<>();

        if (!input.matches(regex)) {
            errors.add(hint + " (got: '" + input + "')");
        }

        return errors;
    }

    // VALIDATOR 5 — OPTIONAL MAX LENGTH
    static List<String> validateOptionalMaxLength(String input,
                                                  int    maxLength,
                                                  String fieldName) {

        List<String> errors = new ArrayList<>();

        // Empty is fine — field is optional
        if (input.isEmpty()) {
            return errors;
        }

        if (input.length() > maxLength) {
            errors.add(fieldName + " must be at most " + maxLength
                    + " characters (got: " + input.length() + ")");
        }

        return errors;
    }

    // HELPER — IS VALID INTEGER
    static boolean isValidInteger(String input) {

        if (input == null || input.isEmpty()) {
            return false;
        }

        int startIndex = 0;

        // Allow optional leading minus sign for negative numbers
        if (input.charAt(0) == '-') {
            startIndex = 1;
            // A lone "-" is not valid
            if (input.length() == 1) {
                return false;
            }
        }

        // Every remaining character must be a digit 0-9
        for (int i = startIndex; i < input.length(); i++) {
            char character = input.charAt(i);
            if (character < '0' || character > '9') {
                return false; // found a non-digit
            }
        }

        try {
            Integer.parseInt(input);
        } catch (NumberFormatException overflow) {
            return false;
        }

        return true;
    }

    // HELPER — DAYS IN MONTH
    static int daysInMonth(int month, int year) {
        // Months with 31 days
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            return 31;
        }

        // Months with 30 days
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        }

        // February — check leap year
        boolean isLeapYear = (year % 4 == 0 && year % 100 != 0)
                || (year % 400 == 0);
        return isLeapYear ? 29 : 28;
    }

    // PRINT HELPERS
    static void printAllErrors(List<String> errors) {
        System.out.println(" Found " + errors.size() + " error(s):");
        for (int i = 0; i < errors.size(); i++) {
            System.out.println("    " + (i + 1) + ". " + errors.get(i));
        }
        System.out.println();
    }

    static void printCancelled() {
        System.out.println(" Cancelled. Returning to main menu.");
    }
}
