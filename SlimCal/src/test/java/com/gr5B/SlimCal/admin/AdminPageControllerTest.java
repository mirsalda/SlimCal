package com.gr5B.SlimCal.admin;

import com.gr5B.SlimCal.admin.*;
import com.gr5B.SlimCal.food.Food;
import com.gr5B.SlimCal.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class AdminPageControllerTest {

    @Mock
    private AdminPageService adminPageService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminPageController adminPageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAdminDashboard() {
        // Valid data scenario
        int entriesThisWeek = 10;
        int entriesLastWeek = 5;
        List<UserWithHighSpending> usersWithHighSpending = Arrays.asList(
                new UserWithHighSpending(1L, "User 1", "user1@example.com", new BigDecimal("250.50")),
                new UserWithHighSpending(2L, "User 2", "user2@example.com", new BigDecimal("150.75"))
        );

        AdminData adminData = new AdminData(entriesThisWeek, entriesLastWeek, usersWithHighSpending);
        List<User> users = Arrays.asList(new User(), new User());

        when(adminPageService.getAdminData()).thenReturn(adminData);
        when(adminPageService.getAllUsers()).thenReturn(users);

        String viewName = adminPageController.getAdminDashboard(model);

        assertEquals("admin-dashboard", viewName);
        verify(model).addAttribute("data", adminData);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testGetAdminDashboard_EmptyUsersWithHighSpending() {
        // Test case where the list of high spending users is empty
        int entriesThisWeek = 10;
        int entriesLastWeek = 5;
        List<UserWithHighSpending> usersWithHighSpending = Collections.emptyList();

        AdminData adminData = new AdminData(entriesThisWeek, entriesLastWeek, usersWithHighSpending);
        List<User> users = Arrays.asList(new User(), new User());

        when(adminPageService.getAdminData()).thenReturn(adminData);
        when(adminPageService.getAllUsers()).thenReturn(users);

        String viewName = adminPageController.getAdminDashboard(model);

        assertEquals("admin-dashboard", viewName);
        verify(model).addAttribute("data", adminData);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testGetAdminDashboard_NullUsersWithHighSpending() {
        // Test case where usersWithHighSpending is null
        int entriesThisWeek = 10;
        int entriesLastWeek = 5;
        List<UserWithHighSpending> usersWithHighSpending = null;

        AdminData adminData = new AdminData(entriesThisWeek, entriesLastWeek, usersWithHighSpending);
        List<User> users = Arrays.asList(new User(), new User());

        when(adminPageService.getAdminData()).thenReturn(adminData);
        when(adminPageService.getAllUsers()).thenReturn(users);

        String viewName = adminPageController.getAdminDashboard(model);

        assertEquals("admin-dashboard", viewName);
        verify(model).addAttribute("data", adminData);
        verify(model).addAttribute("users", users);
    }

    @Test
    void testGetUserEntries_UserFound() {
        Long userId = 1L;
        User user = new User();
        List<Food> foodEntries = Arrays.asList(new Food(), new Food());
        double averageCalories = 250.0;

        when(adminPageService.findById(userId)).thenReturn(user);
        when(adminPageService.getUserEntries(userId)).thenReturn(foodEntries);
        when(adminPageService.getAverageCaloriesForUserLastWeek(userId)).thenReturn(averageCalories);

        String viewName = adminPageController.getUserEntries(userId, model);

        assertEquals("user-entries", viewName);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("userEntries", foodEntries);
        verify(model).addAttribute("averageCaloriesPerUser", averageCalories);
    }

    @Test
    void testGetUserEntries_UserNotFound() {
        Long userId = 1L;

        when(adminPageService.findById(userId)).thenReturn(null);

        String viewName = adminPageController.getUserEntries(userId, model);

        assertEquals("user-entries", viewName);
        verify(model).addAttribute("error", "User not found");
    }

    @Test
    void testGetUserEntries_EmptyFoodEntries() {
        Long userId = 1L;
        User user = new User();
        List<Food> foodEntries = Collections.emptyList();
        double averageCalories = 0.0;

        when(adminPageService.findById(userId)).thenReturn(user);
        when(adminPageService.getUserEntries(userId)).thenReturn(foodEntries);
        when(adminPageService.getAverageCaloriesForUserLastWeek(userId)).thenReturn(averageCalories);

        String viewName = adminPageController.getUserEntries(userId, model);

        assertEquals("user-entries", viewName);
        verify(model).addAttribute("user", user);
        verify(model).addAttribute("userEntries", foodEntries);
        verify(model).addAttribute("averageCaloriesPerUser", averageCalories);
    }

    @Test
    void testShowUpdateForm() {
        Long entryId = 1L;
        Food entry = new Food();

        when(adminPageService.getFoodEntry(entryId)).thenReturn(entry);

        String viewName = adminPageController.showUpdateForm(entryId, model);

        assertEquals("update-entry", viewName);
        verify(model).addAttribute("entry", entry);
    }




    @Test
    void testDeleteEntry() {
        Long entryId = 1L;

        String viewName = adminPageController.deleteEntry(entryId);

        assertEquals("redirect:/admin", viewName);
        verify(adminPageService).deleteEntry(entryId);
    }



    @Test
    void testAddUserEntry() {
        Long userId = 1L;
        Food entry = new Food();
        entry.setUser(new User());
        entry.setCreatedAt(java.time.LocalDateTime.now());

        when(adminPageService.findById(userId)).thenReturn(new User());

        String viewName = adminPageController.addUserEntry(userId, entry);

        assertEquals("redirect:/admin/users/1/entries", viewName);
        verify(adminPageService).saveUserEntry(entry);
    }


}
