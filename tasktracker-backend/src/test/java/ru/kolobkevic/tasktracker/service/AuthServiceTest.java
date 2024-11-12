package ru.kolobkevic.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kolobkevic.tasktracker.dto.EmailSendingDto;
import ru.kolobkevic.tasktracker.dto.JwtAuthenticationResponse;
import ru.kolobkevic.tasktracker.dto.SignInRequest;
import ru.kolobkevic.tasktracker.dto.SignUpRequest;
import ru.kolobkevic.tasktracker.exception.ObjectAlreadyExistsException;
import ru.kolobkevic.tasktracker.exception.UserBadCredentialsException;
import ru.kolobkevic.tasktracker.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserService userService;

    @Mock
    private KafkaTemplate<String, EmailSendingDto> kafkaTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void signUp_ValidRequest_ShouldReturnJwtToken() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("testUser");
        signUpRequest.setPassword("password123");
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setFirstname("Test");
        signUpRequest.setLastname("User");

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authService.signUp(signUpRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        verify(kafkaTemplate, times(1)).send(eq("EMAIL_SENDING_TASKS"), any(EmailSendingDto.class));
    }

    @Test
    void signUp_DuplicateUser_ShouldThrowObjectAlreadyExistsException() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("duplicateUser");
        signUpRequest.setPassword("password123");

        when(passwordEncoder.encode(signUpRequest.getPassword())).thenReturn("encodedPassword");
        when(userService.createUser(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(ObjectAlreadyExistsException.class, () -> authService.signUp(signUpRequest));
        verify(kafkaTemplate, never()).send(anyString(), any(EmailSendingDto.class));
    }

    @Test
    void signIn_ValidCredentials_ShouldReturnJwtToken() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("validUser");
        signInRequest.setPassword("password123");

        User user = new User();
        user.setUsername("validUser");

        when(userService.getUserByUsername(signInRequest.getUsername())).thenReturn(user);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        JwtAuthenticationResponse response = authService.signIn(signInRequest);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
    }

    @Test
    void signIn_InvalidCredentials_ShouldThrowUserBadCredentialsException() {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setUsername("invalidUser");
        signInRequest.setPassword("wrongPassword");

        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThrows(UserBadCredentialsException.class, () -> authService.signIn(signInRequest));
        verify(jwtService, never()).generateToken(any(User.class));
    }
}
