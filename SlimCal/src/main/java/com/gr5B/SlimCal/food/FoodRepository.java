package com.gr5B.SlimCal.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {


    List<Food> findByUserIdOrderByCreatedAtDesc(Long userId);


    List<Food> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long userId, LocalDateTime start, LocalDateTime end);

    Food findByIdAndUserId(Long foodId, Long userId);


    @Query("SELECT COALESCE(SUM(f.cals), 0) FROM Food f WHERE f.user.id = ?1 AND f.createdAt BETWEEN ?2 AND ?3")
    Integer calculateTotalCalsForUserBetweenDates(Long userId, LocalDateTime start, LocalDateTime end);


    @Query("SELECT SUM(f.price) FROM Food f " +
            "WHERE f.user.id = :userId " +
            "AND YEAR(f.createdAt) = :year " +
            "AND MONTH(f.createdAt) = :month")
    BigDecimal calculateMonthlySpending(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);


    @Query("SELECT SUM(f.price) FROM Food f " +
            "WHERE f.user.id = :userId " +
            "AND f.createdAt BETWEEN :startOfWeek AND :endOfWeek")
    BigDecimal calculateWeeklyExpenditure(
            @Param("userId") Long userId,
            @Param("startOfWeek") LocalDateTime startOfWeek,
            @Param("endOfWeek") LocalDateTime endOfWeek);



    @Query("SELECT COUNT(f) FROM Food f WHERE f.createdAt BETWEEN :start AND :end")
    int countByDateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COALESCE(AVG(f.cals), 0) FROM Food f WHERE f.user.id = :userId AND f.createdAt >= :lastWeek")
    Double getAverageCaloriesPerUserLastWeek(@Param("userId") Long userId, @Param("lastWeek") LocalDateTime lastWeek);
}