package com.gr5B.SlimCal.food;
import com.gr5B.SlimCal.food.FoodRequest;
import com.gr5B.SlimCal.food.FoodRepository;
import com.gr5B.SlimCal.user.User;
import com.gr5B.SlimCal.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodRepository foodRepository;
    private final UserService userService;

    @Autowired
    public FoodService(FoodRepository foodRepository, UserService userService) {
        this.foodRepository = foodRepository;
        this.userService = userService;
    }

    // Create a new food entry
    public void createFoodEntry(FoodRequest request, Long userId) {
        User user = userService.getUserById(userId);

        Food food = new Food();
        food.setUser(user);
        food.setName(request.getName());
        food.setCals(request.getCals());
        food.setPrice(request.getPrice());


        foodRepository.save(food);
    }


    public List<Food> getUserFoodEntriesForDay(Long userId, LocalDateTime startOfDay) {
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return foodRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, startOfDay, endOfDay);
    }

    // Get daily cals for a specific day
    public Integer getDailyCals(Long userId, LocalDateTime startOfDay) {
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return foodRepository.calculateTotalCalsForUserBetweenDates(userId, startOfDay, endOfDay);
    }


    public BigDecimal getMonthlySpending(Long userId, int year, int month) {
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return foodRepository.calculateMonthlySpending(userId, year, month);
    }

    public BigDecimal getWeeklySpending(Long userId, LocalDate startOfWeek) {
        LocalDateTime start = startOfWeek.atStartOfDay();
        LocalDateTime end = startOfWeek.plusDays(6).atTime(23, 59, 59);
        return foodRepository.calculateWeeklyExpenditure(userId, start, end);
    }
    public void deleteFoodEntry(Long foodId, Long userId) {
        Food food = foodRepository.findByIdAndUserId(foodId, userId);
        if (food != null) {
            foodRepository.delete(food);
        }
    }

    public List<Food> getUserFoodEntriesBetweenDates(Long userId, LocalDate fromDate, LocalDate toDate) {
        LocalDateTime start = fromDate.atStartOfDay();
        LocalDateTime end = toDate.plusDays(1).atStartOfDay();
        return foodRepository.findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, start, end);
    }

    public List<Map<String, Object>> getLast7DaysCalories(Long userId) {
        LocalDate today = LocalDate.now();
        return today.minusDays(6).datesUntil(today.plusDays(1))
                .sorted(Comparator.reverseOrder())
                .map(date -> {
                    Integer dailyCals = getDailyCals(userId, date.atStartOfDay());
                    Map<String, Object> result = Map.of(
                            "date", date.toString(),
                            "calories", dailyCals != null ? dailyCals : 0
                    );
                    return result;
                })
                .collect(Collectors.toList());
    }


}
