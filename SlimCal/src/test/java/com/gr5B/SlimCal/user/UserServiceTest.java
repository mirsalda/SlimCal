package com.gr5B.SlimCal.user;

import org.junit.jupiter.api.BeforeEach;  // Correct JUnit 5 import
import org.junit.jupiter.api.Test;      // Correct JUnit 5 import
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;  // Correct import

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;  // Correct JUnit 5 assertion import
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;  // Correct PasswordEncoder declaration

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Correct method for JUnit 5
    }


    @Test
    public void registerUser_ShouldEncodePasswordAndSaveUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("plainPassword");

        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode("plainPassword")).thenReturn(encodedPassword);

        // Act
        userService.registerUser(user);

        // Assert
        assertEquals(encodedPassword, user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void findByEmail_ShouldReturnUserIfFound() {
        // Arrange
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(user);

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void findByEmail_ShouldReturnNullIfNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(null);

        // Act
        User result = userService.findByEmail(email);

        // Assert
        assertNull(result);
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void getUserById_ShouldReturnUserIfFound() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getUserById_ShouldThrowExceptionIfNotFound() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
        assertEquals("User not found with id: " + userId, exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }
}
