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
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
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

    public UserDTOResponse create(UserCreateDTO userDTO) {
        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed.");
        }
        if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User with phone number: " + userDTO.getPhoneNumber() + " is already existed.");
        }

        MyUserDetails userDetails =((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        organizationService.checkOrganizationIsActive(userDetails.getUser().getOrganizationId());

//        TO DO check organization size

        User user = userMapper.dtoToEntity(userDTO);
        user.setActive(false);
        user.setOrganizationId(userDetails.getUser().getOrganizationId());
        user = userRepository.save(user);
        return userMapper.entityToDto(user);
    }

    public void createOwner(UserRegistrationDTO userDTO, UUID organizationId){
        User user = userMapper.dtoToEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setUserRole(UserRole.OWNER);
        user.setActive(true);
        user.setOrganizationId(organizationId);
        userRepository.save(user);
    }

    public UserDTOResponse checkEmail(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            String activationCode = RandomStringUtils.randomNumeric(8);
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
            userRepository.setActivationCodeAndCodeExpirationDateById(user.getId(), expirationTime, activationCode);
            emailNotificationService.sendMessage(email, "Activation Code", "Your code: " + activationCode);
        }
        return userMapper.entityToDto(user);
    }

    public UserDTOResponse activate(ActivationDTO activation) {
        User user = findUserById(activation.getId());
        if (user.isActive()) {
            throw new BadRequestException("User with id: " + activation.getId() + " is active.");
        }
        if (user.getActivationCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Activation code is expired");
        }
        if (!user.getActivationCode().equals(activation.getActivationCode()) || user.getActivationCode() == null) {
            if (user.getActivationCode() != null) {
                userRepository.setNullActivationCodeById(activation.getId());
            }
            throw new BadRequestException("Wrong activation code!");
        }
        validator.validatePassword(activation.getPassword(), user.getEmail());
        userRepository.activate(activation.getId(), passwordEncoder.encode(activation.getPassword()));
        user.setActive(true);
        return userMapper.entityToDto(user);
    }

    public TokenDTOResponse login(UserLoginDTO userLogin, HttpServletResponse response) {
        MyUserDetails userDetails = (MyUserDetails) loadUserByUsername(userLogin.getEmail());
        if (!userDetails.getUser().isActive()) {
            throw new BadRequestException("User with email: " + userDetails.getUsername() + " is inactive.");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException("Email or Password are wrong!");
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

    public UserDTOResponse changePassword(ChangePasswordDTO changePassword) {
        User user = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();

        if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is wrong!");
        }
        if (passwordEncoder.matches(changePassword.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Old password and new one are same!");
        }

        validator.validatePassword(changePassword.getNewPassword(), user.getEmail());
        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));

        return userMapper.entityToDto(userRepository.save(user));
    }

    public void passwordRestoration(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            throw new BadRequestException("User with email: " + email + " is inactive!");
        }
        user.setActive(false);
        String activationCode = RandomStringUtils.randomNumeric(8);
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        userRepository.setActivationCodeAndCodeExpirationDateById(user.getId(), expirationTime, activationCode);
        emailNotificationService.sendMessage(email, "Activation Code", "Your code: " + activationCode);
    }

    public void commonValidation(UserRegistrationDTO userDTO) {
        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed.");
        }
        if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User with phone number: " + userDTO.getPhoneNumber() + " is already existed.");
        }
        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        validator.validatePassword(userDTO.getPassword(), userDTO.getEmail());
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
