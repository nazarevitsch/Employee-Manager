package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.ActivationDTO;
import com.bida.employer.manager.domain.dto.CheckUserDTOResponse;
import com.bida.employer.manager.domain.dto.UserDTOResponse;
import com.bida.employer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/user/activation")
public class UserActivationController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDTOResponse> activateUser(@Valid @RequestBody ActivationDTO activation) {
        return new ResponseEntity<>(userService.activate(activation), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<CheckUserDTOResponse> checkIsUserActive(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.checkUser(email), HttpStatus.OK);
    }
}
