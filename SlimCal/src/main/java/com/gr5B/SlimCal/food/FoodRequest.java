package com.gr5B.SlimCal.food;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class FoodRequest {

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private Integer cals;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 8, fraction = 2)
    private BigDecimal price;

    // Getters and Setters
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
}
