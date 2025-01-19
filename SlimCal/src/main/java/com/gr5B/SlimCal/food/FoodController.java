package com.gr5B.SlimCal.food;

import com.gr5B.SlimCal.food.DiaryResponse;
import com.gr5B.SlimCal.user.CustomUserDetails;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class FoodController {

    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        String role = ((CustomUserDetails) userDetails).getRole().name();
        LocalDate today = LocalDate.now();

        List<FoodResponse> foodEntryResponses = foodService.getUserFoodEntriesForDay(userId, today.atStartOfDay()).stream()
                .map(entry -> new FoodResponse(
                        entry.getId(),
                        entry.getName(),
                        entry.getCals(),
                        entry.getPrice(),
                        entry.getCreatedAt()))
                .collect(Collectors.toList());

        Integer dailyCalories = foodService.getDailyCals(userId, today.atStartOfDay());
        BigDecimal monthlySpending = foodService.getMonthlySpending(userId, today.getYear(), today.getMonthValue());
        LocalDate startOfWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        BigDecimal weeklySpending = foodService.getWeeklySpending(userId, startOfWeek);

        List<Map<String, Object>> last7DaysCalories = foodService.getLast7DaysCalories(userId);

        model.addAttribute("foodEntries", foodEntryResponses);
        model.addAttribute("dailyCalories", dailyCalories != null ? dailyCalories : 0);
        model.addAttribute("monthlySpending", monthlySpending != null ? monthlySpending : BigDecimal.ZERO);
        model.addAttribute("weeklySpending", weeklySpending != null ? weeklySpending : BigDecimal.ZERO);
        model.addAttribute("userRole", role);
        model.addAttribute("last7DaysCalories", last7DaysCalories);

        return "dashboard";
    }

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }


    @GetMapping("/insert")
    public String showInsertPage(Model model) {
        model.addAttribute("foodRequest", new FoodRequest());
        return "insert";
    }


    @PostMapping("/insert")
    public String createFoodEntry(
            @Valid @ModelAttribute FoodRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        foodService.createFoodEntry(request, userId);
        return "redirect:/dashboard";
    }
    @PostMapping("/delete/{foodId}")
    public String deleteFoodEntry(
            @PathVariable Long foodId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        foodService.deleteFoodEntry(foodId, userId);
        return "redirect:/dashboard";
    }

    @GetMapping("/diary/daily")
    public String getDailyEntries(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        List<FoodResponse> foodEntryResponses = foodService.getUserFoodEntriesForDay(userId, date.atStartOfDay()).stream()
                .map(entry -> new FoodResponse(
                        entry.getId(),
                        entry.getName(),
                        entry.getCals(),
                        entry.getPrice(),
                        entry.getCreatedAt()))
                .collect(Collectors.toList());
        model.addAttribute("entries", foodEntryResponses);
        return "dailyDiary";
    }


    @GetMapping("/diary/cals/daily")
    public String getDailyCals(
            @RequestParam LocalDate date,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        Integer cals = foodService.getDailyCals(userId, date.atStartOfDay());
        model.addAttribute("cals", cals != null ? cals : 0);
        return "dailyCals";
    }


    @GetMapping("/diary/spending/monthly")
    public String getMonthlySpending(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        BigDecimal spending = foodService.getMonthlySpending(userId, year, month);
        model.addAttribute("spending", spending);
        return "monthlySpending";
    }

    @GetMapping("/diary")
    public String getDiary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Long userId = ((CustomUserDetails) userDetails).getId();
        List<Food> entries = Collections.emptyList();
        String error = null;

        try {
            if (fromDate == null || toDate == null) {

                toDate = LocalDate.now();
                fromDate = toDate.minusDays(30);
            }
            entries = foodService.getUserFoodEntriesBetweenDates(userId, fromDate, toDate);
        } catch (Exception e) {
            error = "An error occurred while fetching diary data.";
        }

        Map<String, List<Food>> groupedEntries = entries.stream()
                .collect(Collectors.groupingBy(entry -> entry.getCreatedAt().toLocalDate().toString()));

        model.addAttribute("groupedEntries", groupedEntries);
        model.addAttribute("error", error);

        return "diary";
    }









}
