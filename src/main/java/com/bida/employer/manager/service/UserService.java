package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.domain.enums.UserRole;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.UserMapper;
import com.bida.employer.manager.notification.EmailNotificationService;
import com.bida.employer.manager.repository.UserRepository;
import com.bida.employer.manager.validation.Validator;
import liquibase.util.StringUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtilService jwtUtilService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private Validator validator;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private EmailNotificationService emailNotificationService;

    public void create(UserRegistrationDTO userDTO) {
        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed.");
        }
        if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User with phone number: " + userDTO.getEmail() + " is already existed.");
        }

        User user = userMapper.dtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        userRepository.save(user);
    }

    public void createOwner(UserRegistrationDTO userDTO){
        User user = userMapper.dtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserRole(UserRole.OWNER);
        user.setActive(true);
        userRepository.save(user);
    }

    public UserDTOResponse checkEmail(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            String activationCode = RandomStringUtils.randomNumeric(8);
            userRepository.setNewActivationCode(user.getId(), activationCode);
            emailNotificationService.sendMessage(email, "Activation Code", "Your code: " + activationCode);
        }
        return userMapper.entityToDto(user);
    }

    public UserDTOResponse activate(ActivationDTO activation) {
        User user = findUserById(activation.getId());
        if (user.isActive()) {
            throw new BadRequestException("User with id: " + activation.getId() + " is active");
        }
        if (!user.getActivationCode().equals(activation.getActivationCode()) || user.getActivationCode() == null) {
            if (user.getActivationCode() != null) {
                userRepository.setNullActivationCode(activation.getId());
            }
            throw new BadRequestException("Wrong activation code!");
        }
        userRepository.activate(activation.getId(), passwordEncoder.encode(activation.getPassword()));
        return userMapper.entityToDto(user);
    }

    public TokenDTOResponse login(UserLoginDTO userLogin, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Email or Password are wrong!");
        }
//        UserDetails userDetails = loadUserByUsername(userLogin.getEmail());
        MyUserDetails userDetails = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (!userDetails.getUser().isActive()) {
            throw new BadRequestException("User with email: " + userDetails.getUsername() + " is inactive.");
        }
        Cookie cookie = new Cookie("refreshToken", generateRefreshToken(userDetails));
        cookie.setPath("/");
        response.addCookie(cookie);
        return new TokenDTOResponse("Bearer " + jwtUtilService.generateToken(userDetails));
    }

    public String generateRefreshToken(MyUserDetails userDetails) {
        return jwtUtilService.generateRefreshToken(userDetails);
    }

    public TokenDTOResponse generateAccessToken(String token) {
        UserDetails userDetails = loadUserByUsername(jwtUtilService.extractUsernameRefreshToken(token));
        if (!jwtUtilService.validateRefreshToken(token, userDetails)) {
            throw new BadRequestException("Refresh token is expired!");
        }
        return new TokenDTOResponse("Bearer " + jwtUtilService.generateToken(userDetails));
    }

    public void commonValidation(UserRegistrationDTO userDTO) {
        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        validator.validatePassword(userDTO.getPassword(), userDTO.getEmail());

        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed.");
        }
    }

    public User findUserByEmail(String email) {
        return Optional.of(userRepository.findUserByEmail(email))
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " wasn't found."));
    }

    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " doesn't exist."));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new MyUserDetails(findUserByEmail(s));
    }
}
