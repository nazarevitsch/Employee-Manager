package com.bida.employer.manager.web;

import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDTOResponse> login(@Valid @RequestBody UserLoginDTO userDTO, HttpServletResponse response) {
        return new ResponseEntity<>(userService.login(userDTO, response), HttpStatus.OK);
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<TokenDTOResponse> refreshToken(@CookieValue(name = "refreshToken") String token) {
        return new ResponseEntity<>(userService.generateAccessToken(token), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<UserDTOResponse> changePassword(@Valid @RequestBody ChangePasswordDTO changePassword) {
        return new ResponseEntity<>(userService.changePassword(changePassword), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserDTOResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsersOfCurrentOrganization(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<UserDTOResponse> getUser(@RequestParam("id") UUID userId) {
        return new ResponseEntity<>(userService.getUser(userId), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<UserDTOResponse> createUser(@Valid @RequestBody UserCreateDTO userDTO) {
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<UserDTOResponse> deleteUser(@RequestParam("id") UUID id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.CREATED);
    }
}
