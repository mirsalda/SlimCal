package com.gr5B.SlimCal.admin;

import com.upt.SlimCal.food.Food;
import com.upt.SlimCal.food.FoodRepository;
import com.upt.SlimCal.user.User;
import com.upt.SlimCal.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminPageService {

    private final UserRepository userRepository;
    private final FoodRepository foodRepository;

    @Autowired
    public AdminPageService(UserRepository userRepository, FoodRepository foodRepository) {
        this.userRepository = userRepository;
        this.foodRepository = foodRepository;
    }


    public AdminData getAdminData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisWeek = now.minusDays(7);
        LocalDateTime weekBefore = now.minusDays(14);


        int entriesThisWeek = foodRepository.countByDateTimeBetween(thisWeek, now);
        int entriesLastWeek = foodRepository.countByDateTimeBetween(weekBefore, weekBefore);


        List<UserWithHighSpending> usersWithHighSpending = getUsersWithHighSpending();

        return new AdminData(
                entriesThisWeek,
                entriesLastWeek,
                usersWithHighSpending
        );
    }

    public AdminData getAdminData(Long userId) {
       
        AdminData adminData = getAdminData();

        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7);


        double averageCaloriesPerUser = foodRepository.getAverageCaloriesPerUserLastWeek(userId, lastWeek);
        

        return adminData;
    }


    private List<UserWithHighSpending> getUsersWithHighSpending() {
        BigDecimal budgetLimit = new BigDecimal("1000");

        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        List<User> allUsers = getAllUsers();

        return allUsers.stream()
                .map(user -> {
                    BigDecimal monthlySpending = foodRepository.calculateMonthlySpending(user.getId(), currentYear, currentMonth);
                    if (monthlySpending != null && monthlySpending.compareTo(budgetLimit) > 0) {
                        return new UserWithHighSpending(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                monthlySpending
                        );
                    }
                    return null;
                })
                .filter(dto -> dto != null)
                .collect(Collectors.toList());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public List<Food> getUserEntries(Long userId) {
        return foodRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }




    @Transactional
    public void deleteEntry(Long entryId) {
        foodRepository.deleteById(entryId);
    }


    public User findById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElse(null);
    }
    public double getAverageCaloriesForUserLastWeek(Long userId) {
        LocalDateTime lastWeek = LocalDateTime.now().minusDays(7); // Get the date for 7 days ago
        Double averageCalories = foodRepository.getAverageCaloriesPerUserLastWeek(userId, lastWeek);
        return averageCalories != null ? averageCalories : 0; // Ensure it doesn't return null
    }


    public Food getFoodEntry(Long entryId) {
        return foodRepository.findById(entryId).orElse(null);
    }

    public void updateUserEntry(Long entryId, Food updatedEntry) {
        Food existingEntry = foodRepository.findById(entryId).orElse(null);
        if (existingEntry != null) {
            existingEntry.setName(updatedEntry.getName());
            existingEntry.setCals(updatedEntry.getCals());
            existingEntry.setPrice(updatedEntry.getPrice());
            foodRepository.save(existingEntry);
        }
    }

    public void saveUserEntry(Food entry) {
        foodRepository.save(entry);
    }
}
