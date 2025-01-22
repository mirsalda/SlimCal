package com.gr5B.SlimCal.admin;

import lombok.Data;

import java.util.List;

@Data

public class AdminData {

    private int entriesThisWeek;
    private int entriesLastWeek;
   
    private List<UserWithHighSpending> usersWithHighSpending;

    public AdminData(int entriesThisWeek,
                     int entriesLastWeek,
                     List<UserWithHighSpending> usersWithHighSpending) {

        this.entriesThisWeek = entriesThisWeek;
        this.entriesLastWeek = entriesLastWeek;
       
        this.usersWithHighSpending = usersWithHighSpending;
    }

    public int getEntriesThisWeek() {
        return entriesThisWeek;
    }

    public void setEntriesThisWeek(int entriesThisWeek) {
        this.entriesThisWeek = entriesThisWeek;
    }

    public int getEntriesLastWeek() {
        return entriesLastWeek;
    }

    public void setEntriesLastWeek(int entriesLastWeek) {
        this.entriesLastWeek = entriesLastWeek;
    }


    public List<UserWithHighSpending> getUsersWithHighSpending() {
        return usersWithHighSpending;
    }

    public void setUsersWithHighSpending(List<UserWithHighSpending> usersWithHighSpending) {
        this.usersWithHighSpending = usersWithHighSpending;
    }
}
