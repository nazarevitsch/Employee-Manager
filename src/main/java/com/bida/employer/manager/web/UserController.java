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

    @PostMapping("/activate")
    public ResponseEntity<UserDTOResponse> activate(@Valid @RequestBody ActivationDTO activation) {
        return new ResponseEntity<>(userService.activate(activation), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity cancelPasswordRecovery(@RequestParam("userId") UUID userId){
        userService.cancelPasswordRecovery(userId);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/password_recovery")
    public ResponseEntity<?> passwordRestoration(@RequestParam("email") String email) {
        userService.passwordRestoration(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/check_email")
    public ResponseEntity<CheckEmailDTOResponse> checkEmail(@RequestParam("email") String email) {
        return new ResponseEntity<>(userService.checkEmail(email), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTOResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsersOfCurrentOrganization(), HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<UserDTOResponse> registration(@Valid @RequestBody UserCreateDTO userDTO) {
        return new ResponseEntity<>(userService.create(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTOResponse> login(@Valid @RequestBody UserLoginDTO userDTO, HttpServletResponse response) {
        return new ResponseEntity<>(userService.login(userDTO, response), HttpStatus.OK);
    }

    @GetMapping("/refresh_token")
    public ResponseEntity<TokenDTOResponse> refreshToken(@CookieValue(name = "refreshToken") String token) {
        return new ResponseEntity<>(userService.generateAccessToken(token), HttpStatus.OK);
    }

    @PostMapping("/change_password")
    public ResponseEntity<UserDTOResponse> changePassword(@Valid @RequestBody ChangePasswordDTO changePassword) {
        return new ResponseEntity<>(userService.changePassword(changePassword), HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMINISTRATOR')")
    public ResponseEntity<UserDTOResponse> delete(@RequestParam("id") UUID id) {
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.CREATED);
    }
}
