package com.bida.employer.manager;

import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.validation.ValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

@SpringBootTest
public class ValidationTest {

    @Autowired
    private ValidationService validationService;

    private static Stream<Arguments> providePhoneNumbers() {
        return Stream.of(
                Arguments.of(true, "+380973646129"),
                Arguments.of(true, "+380503641234"),
                Arguments.of(true, "+380672612345"),
                Arguments.of(true, "+380973543365"),
                Arguments.of(false, "+390973646129"),
                Arguments.of(false, "380503641234"),
                Arguments.of(false, "+3806732345"),
                Arguments.of(false, "+3809735643365"),
                Arguments.of(false, "+380503641b34"),
                Arguments.of(false, "+673612345"),
                Arguments.of(false, "+38097364yu3365"),
                Arguments.of(false, "+38097yu3365"),
                Arguments.of(false, "-38097364yu3365"),
                Arguments.of(false, "+38097#3365")
        );
    }

    private static Stream<Arguments> provideEmails() {
        return Stream.of(
                Arguments.of(true, "email@example.com"),
                Arguments.of(true, "firstname.lastname@example.com"),
                Arguments.of(true, "email@subdomain.example.com"),
                Arguments.of(true, "firstname+lastname@example.com"),
                Arguments.of(true, "1234567890@example.com"),
                Arguments.of(true, "email@example.one.com"),
                Arguments.of(true, "email@example.name"),
                Arguments.of(true, "email@example.museum"),
                Arguments.of(true, "email@example.co.jp"),
                Arguments.of(true, "firstname-lastname@example.com"),

                Arguments.of(false, "plainaddress"),
                Arguments.of(false, "#@%^%#$@#$@#.com"),
                Arguments.of(false, "@example.com"),
                Arguments.of(false, "Joe Smith <email@example.com>"),
                Arguments.of(false, "email.example.com"),
                Arguments.of(false, "email@example@example.com"),
                Arguments.of(false, ".email@example.com"),
                Arguments.of(false, "email.@example.com"),
                Arguments.of(false, "email..email@example.com"),
                Arguments.of(false, "あいうえお@example.com"),
                Arguments.of(false, "email@example.com (Joe Smith)"),
                Arguments.of(false, "email@example"),
                Arguments.of(false, "email@-example.com"),
                Arguments.of(false, "email@111.222.333.44444"),
                Arguments.of(false, "email@example..com"),
                Arguments.of(false, "Abc..123@example.com")
        );
    }

    private static Stream<Arguments> providePasswords() {
        return Stream.of(
                Arguments.of(false, "eppfoefon", "email@example.com"),
                Arguments.of(false, "gklwpxmdat", "email@example.com"),
                Arguments.of(false, "GKLWPXMDAT", "email@example.com"),
                Arguments.of(false, "gklWpxmdAt", "email@example.com"),
                Arguments.of(false, "g1lWpx2dAt", "email@example.com"),
                Arguments.of(false, "g1$W_qwerx2At", "email@example.com"),
                Arguments.of(false, "g1$W_fghjx2At", "email@example.com"),
                Arguments.of(false, "g1$W_vbnmx2At", "email@example.com"),
                Arguments.of(false, "g1$W_rewqx2At", "email@example.com"),
                Arguments.of(false, "g1$W_jhgfx2At", "email@example.com"),
                Arguments.of(false, "g1$W_mnbvx2At", "email@example.com"),
                Arguments.of(false, "g1$W_ghij2At", "email@example.com"),
                Arguments.of(false, "g1$W_jihg2At", "email@example.com"),
                Arguments.of(false, "g1$W_mnbvx2At", "email@example.com"),
                Arguments.of(false, "g1$W_12345At", "email@example.com"),
                Arguments.of(false, "g1$W_98762At", "email@example.com"),
                Arguments.of(false, "g1$W_emailx2At", "email@example.com"),
                Arguments.of(false, "g1$W_mnvx2Amnvxt", "email@example.com"),
                Arguments.of(false, "g1b$W_mnbvx2bAbt", "email@example.com"),

                Arguments.of(true, "g1$W_x2dAt", "email@example.com"),
                Arguments.of(true, "Vato_Kioi97$", "email@example.com")
        );
    }

    @ParameterizedTest
    @MethodSource("providePhoneNumbers")
    public void testValidatePhoneNumbers(boolean expectedResult, String phoneNumber) {
        if (expectedResult) {
            Assertions.assertDoesNotThrow(() -> validationService.validatePhoneNumber(phoneNumber));
        } else {
            Assertions.assertThrows(BadRequestException.class, () -> validationService.validatePhoneNumber(phoneNumber));
        }
    }

    @ParameterizedTest
    @MethodSource("provideEmails")
    public void testValidateEmails(boolean expectedResult, String email) {
        if (expectedResult) {
            Assertions.assertDoesNotThrow(() -> validationService.validateEmail(email));
        } else {
            Assertions.assertThrows(BadRequestException.class, () -> validationService.validateEmail(email));
        }
    }

    @ParameterizedTest
    @MethodSource("providePasswords")
    public void testValidatePasswords(boolean expectedResult, String password, String email) {
        if (expectedResult) {
            Assertions.assertDoesNotThrow(() -> validationService.validatePassword(password, email));
        } else {
            Assertions.assertThrows(BadRequestException.class, () -> validationService.validatePassword(password, email));
        }
    }
}
