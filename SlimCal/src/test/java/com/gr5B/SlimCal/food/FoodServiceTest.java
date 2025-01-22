package com.gr5B.SlimCal.food;

import com.gr5B.SlimCal.food.Food;
import com.gr5B.SlimCal.food.FoodRepository;
import com.gr5B.SlimCal.food.FoodRequest;
import com.gr5B.SlimCal.food.FoodService;
import com.gr5B.SlimCal.user.User;
import com.gr5B.SlimCal.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private FoodService foodService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
    }

    @Test
    void testCreateFoodEntry() {
        FoodRequest request = new FoodRequest();
        request.setName("Pizza");
        request.setCals(400);
        request.setPrice(BigDecimal.valueOf(5.99));
        when(userService.getUserById(1L)).thenReturn(user);
        foodService.createFoodEntry(request, 1L);

        verify(foodRepository).save(any(Food.class));
    }

    @Test
    void testGetUserFoodEntriesForDay() {
        LocalDateTime startOfDay = LocalDateTime.of(2025, 1, 20, 0, 0);
        List<Food> foodList = List.of(new Food());
        when(foodRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(1L, startOfDay, startOfDay.plusDays(1)))
                .thenReturn(foodList);

        List<Food> result = foodService.getUserFoodEntriesForDay(1L, startOfDay);
        assertEquals(1, result.size());
    }

    @Test
    void testGetDailyCals() {
        LocalDateTime startOfDay = LocalDateTime.of(2025, 1, 20, 0, 0);
        when(foodRepository.calculateTotalCalsForUserBetweenDates(1L, startOfDay, startOfDay.plusDays(1)))
                .thenReturn(500);

        Integer result = foodService.getDailyCals(1L, startOfDay);
        assertEquals(500, result);
    }

    @Test
    void testGetMonthlySpending() {
        int year = 2025;
        int month = 1;
        when(foodRepository.calculateMonthlySpending(1L, year, month))
                .thenReturn(BigDecimal.valueOf(200.00));

        BigDecimal result = foodService.getMonthlySpending(1L, year, month);
        assertEquals(BigDecimal.valueOf(200.00), result);
    }

    @Test
    void testGetWeeklySpending() {
        LocalDate startOfWeek = LocalDate.of(2025, 1, 19);
        when(foodRepository.calculateWeeklyExpenditure(1L, startOfWeek.atStartOfDay(), startOfWeek.plusDays(6).atTime(23, 59, 59)))
                .thenReturn(BigDecimal.valueOf(50.00));

        BigDecimal result = foodService.getWeeklySpending(1L, startOfWeek);
        assertEquals(BigDecimal.valueOf(50.00), result);
    }

    @Test
    void testDeleteFoodEntry() {
        Food food = new Food();
        food.setId(1L);
        when(foodRepository.findByIdAndUserId(1L, 1L)).thenReturn(food);
        foodService.deleteFoodEntry(1L, 1L);

        verify(foodRepository).delete(food);
    }

    @Test
    void testDeleteFoodEntryNotFound() {
        when(foodRepository.findByIdAndUserId(1L, 1L)).thenReturn(null);
        foodService.deleteFoodEntry(1L, 1L);

        verify(foodRepository, never()).delete(any(Food.class));
    }

    @Test
    void testGetUserFoodEntriesBetweenDates() {
        LocalDate fromDate = LocalDate.of(2025, 1, 1);
        LocalDate toDate = LocalDate.of(2025, 1, 5);
        List<Food> foodList = List.of(new Food());
        when(foodRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(1L, fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay()))
                .thenReturn(foodList);

        List<Food> result = foodService.getUserFoodEntriesBetweenDates(1L, fromDate, toDate);
        assertEquals(1, result.size());
    }


}
