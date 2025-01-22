package com.gr5B.SlimCal.admin;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class UserWithHighSpending {
    private Long id;
    private String name;
    private String email;
    private BigDecimal totalSpent;

    public UserWithHighSpending(Long id, String name, String email, BigDecimal totalSpent) {
        this.id = id;
        this.name = name;
        this.email=email;
        this.totalSpent = totalSpent;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(BigDecimal totalSpent) {
        this.totalSpent = totalSpent;
    }
}
