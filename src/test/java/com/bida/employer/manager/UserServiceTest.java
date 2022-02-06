package com.bida.employer.manager;

import static org.mockito.Mockito.when;

import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.UserCreateDTO;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.repository.UserRepository;
import com.bida.employer.manager.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = Application.class)
public class UserServiceTest {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static UserCreateDTO invalidUser;

    @BeforeAll
    public static void before() {
        invalidUser = new UserCreateDTO();
        invalidUser.setEmail("alreadyUsedEmail@gmail.com");
        invalidUser.setPhoneNumber("+380671234567");
    }

    @Test
    public void testCreateUserWithUsedEmail() {
        when(userRepository.findUserByEmail("alreadyUsedEmail@gmail.com")).thenReturn(new User());
        Assertions.assertThrows(BadRequestException.class, () -> userService.create(invalidUser));
    }

    @Test
    public void testCreateUserWithUsedPhoneNumber() {
        when(userRepository.findUserByEmail("alreadyUsedEmail@gmail.com")).thenReturn(null);
        when(userRepository.findUserByPhoneNumber("+380671234567")).thenReturn(new User());
        Assertions.assertThrows(BadRequestException.class, () -> userService.create(invalidUser));
    }

//    @Test
//    public void testCreateUserAndOvercomeMaxOrganizationSize() {
//        when(userRepository.findUserByEmail("alreadyUsedEmail@gmail.com")).thenReturn(null);
//        when(userRepository.findUserByPhoneNumber("+380671234567")).thenReturn(null);
//
//        when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn();
//        Assertions.assertThrows(BadRequestException.class, () -> userService.create(invalidUser));
//    }
}
