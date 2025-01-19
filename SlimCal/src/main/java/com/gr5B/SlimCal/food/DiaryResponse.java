package com.gr5B.SlimCal.food;

import java.util.List;

public class DiaryResponse {
    private List<FoodResponse> entries;
    private int totalCals;

    // Constructor to accept List<FoodResponse>
    public DiaryResponse(List<FoodResponse> entries, int totalCals) {
        this.entries = entries;
        this.totalCals = totalCals;
    }

    // Getters and Setters
    public List<FoodResponse> getEntries() {
        return entries;
    }

    public void setEntries(List<FoodResponse> entries) {
        this.entries = entries;
    }

    public int getTotalCals() {
        return totalCals;
    }

    public void setTotalCals(int totalCals) {
        this.totalCals = totalCals;
    }
}
