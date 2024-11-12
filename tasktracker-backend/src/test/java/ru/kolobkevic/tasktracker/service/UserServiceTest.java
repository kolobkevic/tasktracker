package ru.kolobkevic.tasktracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kolobkevic.tasktracker.model.User;
import ru.kolobkevic.tasktracker.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private final String testUsername = "testUser";
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setUsername(testUsername);
        testUser.setPassword("password");
    }

    @Test
    void loadUserByUsername_ReturnsUserDetails() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername(testUsername);

        assertNotNull(userDetails);
        assertEquals(testUsername, userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    void loadUserByUsername_ThrowsException() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(testUsername));
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    void getUserByUsername_ReturnsUser() {
        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        User user = userService.getUserByUsername(testUsername);

        assertNotNull(user);
        assertEquals(testUsername, user.getUsername());
        verify(userRepository, times(1)).findByUsername(testUsername);
    }

    @Test
    void createUser_ReturnsUser() {
        when(userRepository.save(testUser)).thenReturn(testUser);

        User savedUser = userService.createUser(testUser);

        assertNotNull(savedUser);
        assertEquals(testUsername, savedUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }
}
