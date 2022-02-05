package com.bida.employer.manager.service;

import com.bida.employer.manager.domain.MyUserDetails;
import com.bida.employer.manager.domain.Organization;
import com.bida.employer.manager.domain.PasswordRecovery;
import com.bida.employer.manager.domain.User;
import com.bida.employer.manager.domain.dto.*;
import com.bida.employer.manager.domain.enums.UserRole;
import com.bida.employer.manager.exception.BadRequestException;
import com.bida.employer.manager.exception.NotFoundException;
import com.bida.employer.manager.mapper.UserMapper;
import com.bida.employer.manager.notification.EmailNotificationService;
import com.bida.employer.manager.repository.PasswordRecoveryRepository;
import com.bida.employer.manager.repository.UserRepository;
import com.bida.employer.manager.validation.ValidationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Value("${link.server.password.restoration}")
    private String linkTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordRecoveryRepository restorePasswordRepository;
    @Autowired
    private JWTUtilService jwtUtilService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ValidationService validator;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private EmailNotificationService emailNotificationService;

    public List<UserDTOResponse> getAllUsersOfCurrentOrganization() {
        MyUserDetails userDetails =((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        List<User> users = userRepository.findAllByOrganizationId(userDetails.getUser().getOrganizationId());
        return userMapper.entityToDto(users);
    }

    public UserDTOResponse deleteUser(UUID userId) {
        User user = findUserById(userId);
        MyUserDetails userDetails =((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if (user.getId().equals(userDetails.getUser().getId())) {
            throw new BadRequestException("You can't delete yourself.");
        }
        if (!user.getOrganizationId().equals(userDetails.getUser().getOrganizationId())) {
            throw new BadRequestException("User with id: " + userId + " is from organization.");
        }
        if (user.isDeleted()) {
            throw new BadRequestException("User with id: " + userId + " is deleted.");
        }

        userRepository.deleteById(userId);
        user.setDeleted(true);

        return userMapper.entityToDto(user);
    }

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
        UUID organizationId = userDetails.getUser().getOrganizationId();

        Organization organization = organizationService.isOrganizationActive(organizationId);

        int count = userRepository.countEmployersByOrganizationId(organizationId);
        if (count >= organization.getOrganizationType().getSize()) {
            throw new BadRequestException("Organization with id: " + organizationId + " has max size.");
        }

        if (userDTO.getUserRole().equals(UserRole.OWNER) || userDTO.getUserRole().equals(UserRole.INTERNAL_ADMINISTRATOR)) {
            throw new BadRequestException("Owner can't be created");
        }

        User user = userMapper.dtoToEntity(userDTO);
        user.setActive(false);
        user.setOrganizationId(organizationId);
        user.setPassword(passwordEncoder.encode(RandomStringUtils.randomAlphanumeric(20)));

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

    public UserDTOResponse getUser(UUID userId) {
        MyUserDetails userDetails = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        User user = findUserById(userId);

        if (user.getOrganizationId().equals(userDetails.getUser().getOrganizationId())) {
            throw new BadRequestException("User with id: " + userId + " is from another organization.");
        }
        return userMapper.entityToDto(user);
    }

    public CheckUserDTOResponse checkUser(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            passwordRecovery(user);
        }
        return userMapper.entityToCheckEmailDto(user);
    }

    public UserDTOResponse activate(ActivationDTO activation) {
        User user = findUserById(activation.getUserId());
        PasswordRecovery restorePassword = Optional.of(restorePasswordRepository.findByUserId(activation.getUserId()))
                .orElseThrow(() -> new NotFoundException("Password restore for user: " + user.getId() + " wasn't initiate."));

        if (restorePassword.getExpirationDate().isBefore(LocalDateTime.now())) {
            restorePasswordRepository.deleteByUserId(user.getId());
            throw new BadRequestException("Activation code is expired");
        }
        if (!passwordEncoder.matches(activation.getToken(), restorePassword.getToken())) {
            throw new BadRequestException("Wrong activation token!");
        }
        validator.validatePassword(activation.getPassword(), user.getEmail());
        userRepository.setNewPassword(user.getId(), passwordEncoder.encode(activation.getPassword()));
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
            throw new BadRequestException("Old password and the new one are same!");
        }

        validator.validatePassword(changePassword.getNewPassword(), user.getEmail());
        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));

        return userMapper.entityToDto(userRepository.save(user));
    }

    public void passwordRecovery(String email) {
        User user = findUserByEmail(email);
        if (!user.isActive()) {
            throw new BadRequestException("User with email: " + email + " is inactive!");
        }
        passwordRecovery(user);
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

    public void passwordRecovery(User user) {
        restorePasswordRepository.deleteByUserId(user.getId());

        String activationToken = RandomStringUtils.randomAlphanumeric(20);
        String activationTokenEncoded = passwordEncoder.encode(activationToken);

        PasswordRecovery restorePassword = new PasswordRecovery();
        restorePassword.setToken(activationTokenEncoded);
        restorePassword.setExpirationDate(LocalDateTime.now().plusHours(3));
        restorePassword.setUserId(user.getId());

        restorePasswordRepository.save(restorePassword);

        // TO DO Refactor sending emails
        emailNotificationService.sendMessage(user.getEmail(), "Password recovery",
                linkTemplate
                        .replace("{userId}", user.getId().toString())
                        .replace("{restorationToken}", activationToken)
        );
    }

    public void cancelPasswordRecovery(UUID userId) {
        restorePasswordRepository.deleteByUserId(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new MyUserDetails(findUserByEmail(s));
    }
}
