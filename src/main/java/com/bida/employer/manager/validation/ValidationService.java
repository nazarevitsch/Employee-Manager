package com.bida.employer.manager.validation;

import com.bida.employer.manager.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class ValidationService {
    private static final Pattern emailRecipientPartPattern = Pattern.compile("^[a-zA-Z0-9]([a-zA-Z0-9][_!#$%&\\\"’*+/=?`{|}~^.\\-]?)+[a-zA-Z0-9]$");
    private static final Pattern emailDomainPartPattern = Pattern.compile("^[a-zA-Z0-9]{2,}(\\.[a-zA-Z0-9]{2,})?\\.[a-z]{2,}$");
    private static final Pattern phoneNumberPattern = Pattern.compile("^\\+380\\d{9}$");

    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final int MAX_PASSWORD_LENGTH = 30;

    private static final int MIN_UPPER_CASE_LETTERS_AMOUNT = 2;
    private static final int MIN_LOWER_CASE_LETTERS_AMOUNT = 2;
    private static final int MIN_SPECIAL_CHARACTER_AMOUNT = 2;
    private static final int MIN_DIGIT_AMOUNT = 2;

    private static final int MAX_AMOUNT_OF_SAME_CHARS = 3;

    private static final String KEYBOARD_PATTERN_NUMBERS = "01234567890";
    private static final String KEYBOARD_PATTERN_NUMBERS_REVERSED = "09876543210";

    private static final String KEYBOARD_PATTERN_CHARACTER = "qwertyuiopasdfghjklzxcvbnm";
    private static final String KEYBOARD_PATTERN_CHARACTER_REVERSED = "mnbvcxzlkjhgfdsapoiuytrewq";

    private static final String ENGLISH_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final String ENGLISH_ALPHABET_REVERSED = "zyxwvutsrqponmlkjihgfedcba";

    private static final String ALLOWED_SPECIAL_CHARS = "/*!@#$%^&*(){}_[]|?<>,.";


    public void validatePassword(String password, String email) {
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password is too small, min: " + MIN_PASSWORD_LENGTH);
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new BadRequestException("Password is too big, max: " + MAX_PASSWORD_LENGTH);
        }

        char[] passwordArray = password.toCharArray();
        int upperLetterCount = 0;
        int lowerLettersCount = 0;
        int specialCharacterCount = 0;
        int digitCount = 0;
        for (char value : passwordArray) {
            if (Character.isLowerCase(value)) {
                lowerLettersCount++;
                continue;
            }
            if (Character.isUpperCase(value)) {
                upperLetterCount++;
                continue;
            }
            if (Character.isDigit(value)) {
                digitCount++;
                continue;
            }
            if (ALLOWED_SPECIAL_CHARS.contains(String.valueOf(value))) {
                specialCharacterCount++;
                continue;
            }
            throw new BadRequestException("Password contains non allowed chars.");
        }

        if (upperLetterCount < MIN_UPPER_CASE_LETTERS_AMOUNT) {
            throw new BadRequestException("Password has to contain at last " + MIN_UPPER_CASE_LETTERS_AMOUNT + " letters in upper case.");
        }

        if (lowerLettersCount < MIN_LOWER_CASE_LETTERS_AMOUNT) {
            throw new BadRequestException("Password has to contain at last " + MIN_LOWER_CASE_LETTERS_AMOUNT + " letters in lower case.");
        }

        if (digitCount < MIN_DIGIT_AMOUNT) {
            throw new BadRequestException("Password has to contain at last " + MIN_DIGIT_AMOUNT + " digit.");
        }

        if (specialCharacterCount < MIN_SPECIAL_CHARACTER_AMOUNT) {
            throw new BadRequestException("Password has to contain at last " + MIN_SPECIAL_CHARACTER_AMOUNT + " special characters.");
        }

        String lowerCasePassword = password.toLowerCase();
        for (int i = 0; i < KEYBOARD_PATTERN_CHARACTER.length() - 3; i++) {
            if (lowerCasePassword.contains(KEYBOARD_PATTERN_CHARACTER.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain keyboard sequence more then 3.");
            }
            if (lowerCasePassword.contains(KEYBOARD_PATTERN_CHARACTER_REVERSED.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain reversed keyboard sequence more then 3.");
            }
        }

        for (int i = 0; i < KEYBOARD_PATTERN_NUMBERS.length() - 3; i++) {
            if (password.contains(KEYBOARD_PATTERN_NUMBERS.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain keyboard sequence more then 3.");
            }
            if (password.contains(KEYBOARD_PATTERN_NUMBERS_REVERSED.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain reversed keyboard sequence more then 3.");
            }
        }

        String firstPartOfEmail = email.split("@")[0].toLowerCase();
        for (int i = 0; i < firstPartOfEmail.length() - 3; i++) {
            if (lowerCasePassword.contains(firstPartOfEmail.substring(i, i + 4))) {
                throw new BadRequestException("Password should not contain parts of email.");
            }
        }

        for (int i = 0; i < ENGLISH_ALPHABET.length() - 3; i++) {
            if (lowerCasePassword.contains(ENGLISH_ALPHABET.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain alphabet sequence more then 3.");
            }
            if (lowerCasePassword.contains(ENGLISH_ALPHABET_REVERSED.substring(i, i + 4))) {
                throw new BadRequestException("Password can't contain reversed alphabet sequence more then 3.");
            }
        }

        String cutPassword;
        for (int i = 0; i < password.length() - 3; i++) {
            cutPassword = (password.substring(0, i) + password.substring(i + 4)).toLowerCase();
            if (cutPassword.contains(password.substring(i, i + 4).toLowerCase())) {
                throw new BadRequestException("Password should not contain repeated parts.");
            }
        }

        for (char c : passwordArray) {
            int count = 0;
            for (char letter : passwordArray) {
                if (c == letter) {
                    count++;
                }
            }
            if (count > MAX_AMOUNT_OF_SAME_CHARS) {
                throw new BadRequestException("Password can't contain more then 3 same character.");
            }
        }
    }

    public void validateEmail(String email) {
        String[] emailsParts = email.split("@");
        if (emailsParts.length != 2) {
            throw new BadRequestException("Email: " + email + " is not valid");
        }
        if(!emailRecipientPartPattern.matcher(emailsParts[0]).matches()) {
            throw new BadRequestException("Email: " + email + " is not valid");
        }
        if(!emailDomainPartPattern.matcher(emailsParts[1]).matches()) {
            throw new BadRequestException("Email: " + email + " is not valid");
        }
    }

    public void validatePhoneNumber(String phoneNumber) {
        if(!phoneNumberPattern.matcher(phoneNumber).matches()) {
            throw new BadRequestException("Phone number: " + phoneNumber + " is not valid");
        }
    }
}
