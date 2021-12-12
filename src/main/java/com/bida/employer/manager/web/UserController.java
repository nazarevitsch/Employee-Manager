package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<MessageDTOResponse> registration(@RequestBody UserRegistrationDTO userDTO){
        userService.create(userDTO);
        return new ResponseEntity<>(new MessageDTOResponse("User was successfully registered."), HttpStatus.CREATED);
    }

    @PostMapping("/activate")
    public ResponseEntity<UserDTOResponse> activate(@RequestBody ActivationDTO activation) {
        return new ResponseEntity<>(userService.activate(activation), HttpStatus.OK);
    }

    @GetMapping("/check_email/{email}")
    public ResponseEntity<UserDTOResponse> checkEmail(@PathVariable String email) {
        return new ResponseEntity<>(userService.checkEmail(email), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userDTO, HttpServletResponse response) {
        return new ResponseEntity<>(userService.login(userDTO, response), HttpStatus.OK);
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refreshToken") String token) {
        return new ResponseEntity<>(userService.generateAccessToken(token), HttpStatus.OK);
    }
}
