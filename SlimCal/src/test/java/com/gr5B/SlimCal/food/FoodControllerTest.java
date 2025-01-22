package com.gr5B.SlimCal.food;

import com.gr5B.SlimCal.food.Food;
import com.gr5B.SlimCal.food.FoodController;
import com.gr5B.SlimCal.food.FoodRequest;
import com.gr5B.SlimCal.food.FoodResponse;
import com.gr5B.SlimCal.food.FoodService;
import com.gr5B.SlimCal.user.CustomUserDetails;
import com.gr5B.SlimCal.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.support.BindingAwareModelMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FoodControllerTest {

    @Mock
    private FoodService foodService;

    @InjectMocks
    private FoodController foodController;

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUserDetails = new CustomUserDetails(
                1L,
                "user@example.com",
                "password",
                User.Role.USER,
                Collections.emptyList()
        );
    }

    @Test
    void testShowDashboard() {
        Model model = new BindingAwareModelMap();
        List<FoodResponse> mockResponses = List.of(
                new FoodResponse(1L, "Food1", 300, BigDecimal.valueOf(10.00), LocalDateTime.now()),
                new FoodResponse(2L, "Food2", 500, BigDecimal.valueOf(15.00), LocalDateTime.now())
        );

        when(foodService.getUserFoodEntriesForDay(eq(1L), any())).thenReturn(Collections.emptyList());
        when(foodService.getDailyCals(eq(1L), any())).thenReturn(800);
        when(foodService.getMonthlySpending(eq(1L), anyInt(), anyInt())).thenReturn(new BigDecimal("50.00"));
        when(foodService.getWeeklySpending(eq(1L), any())).thenReturn(new BigDecimal("30.00"));
        when(foodService.getLast7DaysCalories(eq(1L))).thenReturn(List.of(Map.of("date", "2025-01-18", "cals", 800)));

        String viewName = foodController.showDashboard(mockUserDetails, model);

        assertEquals("dashboard", viewName);
        assertTrue(model.containsAttribute("foodEntries"));
        assertTrue(model.containsAttribute("dailyCalories"));
        assertTrue(model.containsAttribute("monthlySpending"));
        assertTrue(model.containsAttribute("weeklySpending"));
        assertTrue(model.containsAttribute("last7DaysCalories"));
        assertTrue(model.containsAttribute("userRole"));
    }

    @Test
    void testShowInsertPage() {
        Model model = new BindingAwareModelMap();
        String viewName = foodController.showInsertPage(model);

        assertEquals("insert", viewName);
        assertTrue(model.containsAttribute("foodRequest"));
    }

    @Test
    void testCreateFoodEntry() {
        FoodRequest request = new FoodRequest();
        request.setName("Food1");
        request.setCals(300);
        request.setPrice(new BigDecimal("10.00"));

        String viewName = foodController.createFoodEntry(request, mockUserDetails);

        assertEquals("redirect:/dashboard", viewName);
        verify(foodService, times(1)).createFoodEntry(eq(request), eq(1L));
    }


    @Test
    void testDeleteFoodEntry() {
        String viewName = foodController.deleteFoodEntry(1L, mockUserDetails);

        assertEquals("redirect:/dashboard", viewName);
        verify(foodService, times(1)).deleteFoodEntry(eq(1L), eq(1L));
    }



    @Test
    void testGetDailyCals() {
        Model model = new BindingAwareModelMap();

        when(foodService.getDailyCals(eq(1L), any())).thenReturn(800);

        String viewName = foodController.getDailyCals(LocalDate.now(), mockUserDetails, model);

        assertEquals("dailyCals", viewName);
        assertTrue(model.containsAttribute("cals"));
    }

    @Test
    void testGetMonthlySpending() {
        Model model = new BindingAwareModelMap();

        when(foodService.getMonthlySpending(eq(1L), anyInt(), anyInt())).thenReturn(new BigDecimal("100.00"));

        String viewName = foodController.getMonthlySpending(2025, 1, mockUserDetails, model);

        assertEquals("monthlySpending", viewName);
        assertTrue(model.containsAttribute("spending"));
    }

    @Test
    void testGetDiaryWithDates() {
        Model model = new BindingAwareModelMap();
        List<Food> mockEntries = List.of(
                createMockFood(1L, "Food1", 300, new BigDecimal("10.00"), LocalDateTime.now()),
                createMockFood(2L, "Food2", 500, new BigDecimal("15.00"), LocalDateTime.now().minusDays(1))
        );

        when(foodService.getUserFoodEntriesBetweenDates(eq(1L), any(), any())).thenReturn(mockEntries);

        String viewName = foodController.getDiary(LocalDate.now().minusDays(7), LocalDate.now(), mockUserDetails, model);

        assertEquals("diary", viewName);
        assertTrue(model.containsAttribute("groupedEntries"));
    }

    @Test
    void testGetDiaryWithoutDates() {
        Model model = new BindingAwareModelMap();

        when(foodService.getUserFoodEntriesBetweenDates(eq(1L), any(), any())).thenThrow(RuntimeException.class);

        String viewName = foodController.getDiary(null, null, mockUserDetails, model);

        assertEquals("diary", viewName);
        assertTrue(model.containsAttribute("error"));
    }

    private Food createMockFood(Long id, String name, int cals, BigDecimal price, LocalDateTime createdAt) {
        Food food = new Food();
        food.setId(id);
        food.setName(name);
        food.setCals(cals);
        food.setPrice(price);
        food.setCreatedAt(createdAt);
        return food;
    }
}
