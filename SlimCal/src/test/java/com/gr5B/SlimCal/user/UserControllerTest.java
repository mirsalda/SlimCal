package com.gr5B.SlimCal.user;

import com.gr5B.SlimCal.user.User;
import com.gr5B.SlimCal.user.UserController;
import com.gr5B.SlimCal.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test the homepage method
    @Test
    void testHomepage() {
        String result = userController.homepage();
        assertEquals("homepage", result, "Homepage should return the 'homepage' view");
    }

    // Test the showLoginPage method
    @Test
    void testShowLoginPageWithError() {
        when(model.addAttribute("error", "Invalid email or password")).thenReturn(model);

        String result = userController.showLoginPage("error", null, model);
        assertEquals("login", result);
        verify(model).addAttribute("error", "Invalid email or password");
    }

    @Test
    void testShowLoginPageWithLogout() {
        when(model.addAttribute("message", "You have been logged out successfully")).thenReturn(model);

        String result = userController.showLoginPage(null, "logout", model);
        assertEquals("login", result);
        verify(model).addAttribute("message", "You have been logged out successfully");
    }

    @Test
    void testShowLoginPageWithoutParams() {
        String result = userController.showLoginPage(null, null, model);
        assertEquals("login", result);
    }

    // Test the registerPage method
    @Test
    void testRegisterPage() {
        User user = new User();
        when(model.addAttribute("user", user)).thenReturn(model);

        String result = userController.registerPage(model);
        assertEquals("register", result);
        verify(model).addAttribute("user", user);
    }

    // Test the registerUser method
    @Test
    void testRegisterUserSuccess() {
        User user = new User();
        user.setEmail("test@example.com"); // Set valid fields

        doNothing().when(userService).registerUser(any(User.class));

        String result = userController.registerUser(user, model);
        assertEquals("redirect:/login?registered=true", result);
        verify(userService).registerUser(user);
    }

    @Test
    void testRegisterUserFailure() {
        User user = new User();
        // Missing required fields or invalid data
        doThrow(new IllegalArgumentException("Invalid user data")).when(userService).registerUser(any(User.class));

        String result = userController.registerUser(user, model);
        assertEquals("register", result);
        verify(model).addAttribute("error", "Invalid user data");
    }
}
