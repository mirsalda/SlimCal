package com.gr5B.SlimCal.food;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FoodResponse {

    private Long id;
    private String name;
    private Integer cals;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private String formattedDate;  // Add this field for the formatted date

    public FoodResponse(Long id, String name, Integer cals, BigDecimal price, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.cals = cals;
        this.price = price;
        this.createdAt = createdAt;
        this.formattedDate = createdAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));  // Format the date
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCals() {
        return cals;
    }

    public void setCals(Integer cals) {
        this.cals = cals;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    public String getTime() {
        if (this.createdAt == null) return null;
        DateTimeFormatter fmttr = DateTimeFormatter.ofPattern("HH:mm");
        return createdAt.format(fmttr);
    }
}
