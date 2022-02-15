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
    private PasswordRecoveryRepository passwordRecoveryRepository;
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
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        if (currentUser.getUserRole().equals(UserRole.EMPLOYEE)){
            return userMapper.entityToDto(userRepository.findOnlyActiveUsersByOrganizationId(currentUser.getOrganizationId()));
        }
        return userMapper.entityToDto(userRepository.findAllByOrganizationId(currentUser.getOrganizationId()));
    }

    public UserDTOResponse create(UserCreateDTO userDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        validator.validatePassword(userDTO.getPassword(), userDTO.getEmail());
        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed!");
        }
        if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User with phone number: " + userDTO.getPhoneNumber() + " is already existed!");
        }

        UUID organizationId = currentUser.getOrganizationId();
        Organization organization = organizationService.isOrganizationActive(organizationId);

        int count = userRepository.countEmployersByOrganizationId(organizationId);
        if (count >= organization.getOrganizationType().getSize()) {
            throw new BadRequestException("Organization with id: " + organizationId + " has max size!");
        }
        if (userDTO.getUserRole().equals(UserRole.OWNER) || userDTO.getUserRole().equals(UserRole.INTERNAL_ADMINISTRATOR)) {
            throw new BadRequestException("Owner can't be created!");
        }

        User user = userMapper.dtoToEntity(userDTO);
        user.setActive(true);
        user.setOrganizationId(organizationId);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        User user = findUserById(userId);

        if (!user.getOrganizationId().equals(currentUser.getOrganizationId())) {
            throw new BadRequestException("User with id: " + userId + " is from another organization!");
        }
        if (currentUser.getUserRole().equals(UserRole.EMPLOYEE)) {
            validateActiveUser(user);
        }
        return userMapper.entityToDto(user);
    }

    public UserDTOResponse recoverPassword(PasswordRecoveryDTO activation) {
        User user = findUserById(activation.getUserId());
        validateActiveUser(user);

        PasswordRecovery passwordRecovery = Optional.ofNullable(passwordRecoveryRepository.findByUserId(activation.getUserId()))
                .orElseThrow(() -> new NotFoundException("Password recovery for user: " + user.getId() + " wasn't initiated!"));

        if (passwordRecovery.getExpirationDate().isBefore(LocalDateTime.now())) {
            passwordRecoveryRepository.deleteByUserId(user.getId());
            throw new BadRequestException("Activation code is expired!");
        }
        if (!passwordEncoder.matches(activation.getToken(), passwordRecovery.getToken())) {
            passwordRecoveryRepository.deleteByUserId(user.getId());
            throw new BadRequestException("Wrong activation token!");
        }

        validator.validatePassword(activation.getPassword(), user.getEmail());
        user.setPassword(passwordEncoder.encode(activation.getPassword()));
        passwordRecoveryRepository.deleteByUserId(user.getId());
        return userMapper.entityToDto(user);
    }

    public TokenDTOResponse login(UserLoginDTO userLogin, HttpServletResponse response) {
        MyUserDetails userDetails = null;
        try {
            userDetails = (MyUserDetails) loadUserByUsername(userLogin.getEmail());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
        } catch (Exception e) {
            throw new BadRequestException("Email or Password are wrong!");
        }

        validateActiveUser(userDetails.getUser());

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
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        if (!passwordEncoder.matches(changePassword.getOldPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Old password is wrong!");
        }
        if (passwordEncoder.matches(changePassword.getNewPassword(), currentUser.getPassword())) {
            throw new BadRequestException("Old password and the new one are same!");
        }

        validator.validatePassword(changePassword.getNewPassword(), currentUser.getEmail());
        currentUser.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));

        return userMapper.entityToDto(userRepository.save(currentUser));
    }

    public void passwordRecovery(InitiatePasswordRecoveryDTO initiatePasswordRecovery) {
        User user = findUserByEmail(initiatePasswordRecovery.getEmail());
        validateActiveUser(user);
        passwordRecovery(user);
    }

    public UserDTOResponse updateUser(UUID userId, UpdateUserDTO updateUserDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        User userToUpdate = findUserById(userId);

        if (!currentUser.getOrganizationId().equals(userToUpdate.getOrganizationId())) {
            throw new BadRequestException("User with id: " + userId + " is from another organization!");
        }
        if (currentUser.getUserRole().equals(UserRole.EMPLOYEE)) {
            if (userToUpdate.getUserRole().equals(UserRole.ADMINISTRATOR) || userToUpdate.getUserRole().equals(UserRole.OWNER)) {
                throw new BadRequestException("You can't update this user!");
            }
            if (!currentUser.getId().equals(userId)) {
                throw new BadRequestException("You can't update this user!");
            }
        }
        if (currentUser.getUserRole().equals(UserRole.ADMINISTRATOR) && (userToUpdate.getUserRole().equals(UserRole.ADMINISTRATOR) || userToUpdate.getUserRole().equals(UserRole.OWNER))) {
            if (!currentUser.getId().equals(userId)) {
                throw new BadRequestException("You can't update this user!");
            }
        }
        if (!updateUserDTO.getEmail().equals(userToUpdate.getEmail())) {
            validator.validateEmail(updateUserDTO.getEmail());
            if (userRepository.findUserByEmail(updateUserDTO.getEmail()) != null) {
                throw new BadRequestException("User with email: " + updateUserDTO.getEmail() + " is already existed!");
            }
            userToUpdate.setEmail(updateUserDTO.getEmail());
        }

        if (!updateUserDTO.getPhoneNumber().equals(userToUpdate.getPhoneNumber())) {
            validator.validatePhoneNumber(updateUserDTO.getPhoneNumber());
            if (userRepository.findUserByPhoneNumber(updateUserDTO.getPhoneNumber()) != null) {
                throw new BadRequestException("User with phone number: " + updateUserDTO.getPhoneNumber() + " is already existed!");
            }
            userToUpdate.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }

        userToUpdate.setFirstName(updateUserDTO.getFirstName());
        userToUpdate.setLastName(updateUserDTO.getLastName());
        User updatedUser = userRepository.save(userToUpdate);

        return userMapper.entityToDto(updatedUser);
    }

    public UserDTOResponse changeActiveState(ActiveStateDTO activeStateDTO) {
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        if (currentUser.getId().equals(activeStateDTO.getUserId())) {
            throw new BadRequestException("You can't activate / deactivate yourself!");
        }

        User userToChangeActive = findUserById(activeStateDTO.getUserId());
        if (!currentUser.getOrganizationId().equals(userToChangeActive.getOrganizationId())) {
            throw new BadRequestException("You can't change active state of user from another organization!");
        }
        if (currentUser.getUserRole().equals(UserRole.ADMINISTRATOR) && userToChangeActive.getUserRole().equals(UserRole.OWNER)) {
            throw new BadRequestException("You can't activate / deactivate OWNER!");
        }
        if (userToChangeActive.isActive() == activeStateDTO.getActive()) {
            throw new BadRequestException("User with id: " + activeStateDTO.getUserId() + " is already " + (userToChangeActive.isActive() ? "active!" : "inactive!"));
        }

        UUID organizationId = currentUser.getOrganizationId();
        Organization organization = organizationService.isOrganizationActive(organizationId);
        int employeeCount = userRepository.countEmployersByOrganizationId(organizationId);
        if (employeeCount >= organization.getOrganizationType().getSize() && activeStateDTO.getActive()) {
            throw new BadRequestException("Organization with id: " + organizationId + " has max size!");
        }

        userToChangeActive.setActive(activeStateDTO.getActive());
        User newUser = userRepository.save(userToChangeActive);

        return userMapper.entityToDto(newUser);
    }

    public UserDTOResponse updateUserRole(UserRoleDTO userRoleDTO){
        User currentUser = ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
        validateActiveUser(currentUser);

        if (currentUser.getId().equals(userRoleDTO.getUserId())) {
            throw new BadRequestException("You can't change your user role!");
        }
        if (userRoleDTO.getUserRole().equals(UserRole.OWNER)) {
            throw new BadRequestException("Organization can have only one OWNER!");
        }

        User userToChangeUserRole = findUserById(userRoleDTO.getUserId());
        validateActiveUser(userToChangeUserRole);

        if (!currentUser.getOrganizationId().equals(userToChangeUserRole.getOrganizationId())) {
            throw new BadRequestException("You can't change role of user from another organization!");
        }
        if (userToChangeUserRole.getUserRole().equals(userRoleDTO.getUserRole())) {
            throw new BadRequestException("User already has this role!");
        }

        userToChangeUserRole.setUserRole(userRoleDTO.getUserRole());
        User newUser = userRepository.save(userToChangeUserRole);
        return userMapper.entityToDto(newUser);
    }

    public void ownerCreationValidation(UserRegistrationDTO userDTO) {
        if (userRepository.findUserByEmail(userDTO.getEmail()) != null) {
            throw new BadRequestException("User with email: " + userDTO.getEmail() + " is already existed!");
        }
        if (userRepository.findUserByPhoneNumber(userDTO.getPhoneNumber()) != null) {
            throw new BadRequestException("User with phone number: " + userDTO.getPhoneNumber() + " is already existed!");
        }
        validator.validateEmail(userDTO.getEmail());
        validator.validatePhoneNumber(userDTO.getPhoneNumber());
        validator.validatePassword(userDTO.getPassword(), userDTO.getEmail());
    }

    public User findUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findUserByEmail(email))
                .orElseThrow(() -> new NotFoundException("User with email: " + email + " wasn't found!"));
    }

    public User findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id: " + id + " doesn't exist!"));
    }

    public void passwordRecovery(User user) {
        passwordRecoveryRepository.deleteByUserId(user.getId());

        String activationToken = RandomStringUtils.randomAlphanumeric(25);
        String activationTokenEncoded = passwordEncoder.encode(activationToken);

        PasswordRecovery passwordRecovery = new PasswordRecovery();
        passwordRecovery.setToken(activationTokenEncoded);
        passwordRecovery.setExpirationDate(LocalDateTime.now().plusHours(3));
        passwordRecovery.setUserId(user.getId());

        passwordRecoveryRepository.save(passwordRecovery);

        // TODO: Refactor sending emails
        emailNotificationService.sendMessage(user.getEmail(), "Password recovery",
                linkTemplate
                        .replace("{userId}", user.getId().toString())
                        .replace("{restorationToken}", activationToken)
        );
    }

    public void cancelPasswordRecovery(UUID userId) {
        passwordRecoveryRepository.deleteByUserId(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return new MyUserDetails(findUserByEmail(s));
    }

    public void validateActiveUser(User user) {
        if (!user.isActive()) {
            throw new BadRequestException("User with email: " + user.getId() + " is inactive!");
        }
    }
}
