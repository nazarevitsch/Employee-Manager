package com.bida.employer.manager.web;

import com.bida.employer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/password_recovery")
public class PasswordRecoveryController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> passwordRecovery(@RequestParam("email") String email) {
        userService.passwordRecovery(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity cancelPasswordRecovery(@RequestParam("userId") UUID userId){
        userService.cancelPasswordRecovery(userId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
