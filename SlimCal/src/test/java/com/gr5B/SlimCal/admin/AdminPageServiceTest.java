package com.gr5B.SlimCal.admin;

import com.gr5B.SlimCal.food.Food;
import com.gr5B.SlimCal.food.FoodRepository;
import com.gr5B.SlimCal.user.User;
import com.gr5B.SlimCal.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminPageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodRepository foodRepository;

    private AdminPageService adminPageService;

    private User testUser;

    private Food testFoodEntry;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        adminPageService = new AdminPageService(userRepository, foodRepository);
        testUser = new User(1L, "Test User", "testuser@example.com", "password123", User.Role.USER);


        testFoodEntry = new Food();
        testFoodEntry.setId(1L);
        testFoodEntry.setUser(testUser);
        testFoodEntry.setName("Pizza");
        testFoodEntry.setCals(500);
        testFoodEntry.setPrice(BigDecimal.valueOf(10.99));
        testFoodEntry.setCreatedAt(LocalDateTime.now());
    }




    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<User> result = adminPageService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.getId(), result.get(0).getId());
    }

    @Test
    void testGetUserEntries() {
        Long userId = 1L;
        when(foodRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(testFoodEntry));

        List<Food> result = adminPageService.getUserEntries(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFoodEntry.getName(), result.get(0).getName());
    }

    @Test
    void testDeleteEntry() {
        Long entryId = 1L;
        doNothing().when(foodRepository).deleteById(entryId);

        adminPageService.deleteEntry(entryId);

        verify(foodRepository, times(1)).deleteById(entryId);
    }

    @Test
    void testFindById() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        User result = adminPageService.findById(userId);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
    }

    @Test
    void testFindByIdNotFound() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        User result = adminPageService.findById(userId);

        assertNull(result);
    }


    @Test
    void testGetAverageCaloriesForUserLastWeekNoData() {
        Long userId = 1L;
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);
        when(foodRepository.getAverageCaloriesPerUserLastWeek(userId, lastWeek)).thenReturn(null);

        double result = adminPageService.getAverageCaloriesForUserLastWeek(userId);

        assertEquals(0.0, result);
    }

    @Test
    void testGetFoodEntry() {
        Long entryId = 1L;
        when(foodRepository.findById(entryId)).thenReturn(Optional.of(testFoodEntry));

        Food result = adminPageService.getFoodEntry(entryId);

        assertNotNull(result);
        assertEquals(testFoodEntry.getId(), result.getId());
    }

    @Test
    void testGetFoodEntryNotFound() {
        Long entryId = 1L;
        when(foodRepository.findById(entryId)).thenReturn(Optional.empty());

        Food result = adminPageService.getFoodEntry(entryId);

        assertNull(result);
    }




    @Test
    void testSaveUserEntry() {
        when(foodRepository.save(any(Food.class))).thenReturn(testFoodEntry);

        adminPageService.saveUserEntry(testFoodEntry);

        verify(foodRepository, times(1)).save(testFoodEntry);
    }
}
