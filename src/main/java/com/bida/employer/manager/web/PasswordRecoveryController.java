package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.InitiatePasswordRecoveryDTO;
import com.bida.employer.manager.domain.dto.PasswordRecoveryDTO;
import com.bida.employer.manager.domain.dto.UserDTOResponse;
import com.bida.employer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/user/password_recovery")
public class PasswordRecoveryController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> passwordRecoveryInitiate(@Valid @RequestBody InitiatePasswordRecoveryDTO initiatePasswordRecovery) {
        userService.passwordRecovery(initiatePasswordRecovery);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<UserDTOResponse> recoverPassword(@Valid @RequestBody PasswordRecoveryDTO activation) {
        return new ResponseEntity<>(userService.recoverPassword(activation), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> cancelPasswordRecovery(@RequestParam("userId") UUID userId){
        userService.cancelPasswordRecovery(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
