package com.gr5B.SlimCal.admin;

import com.upt.SlimCal.food.Food;
import com.upt.SlimCal.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final AdminPageService adminPageService;

    @Autowired
    public AdminPageController(AdminPageService adminPageService) {
        this.adminPageService = adminPageService;
    }


    @GetMapping
    public String getAdminDashboard(Model model) {
        AdminData adminData = adminPageService.getAdminData();
        List<User> users = adminPageService.getAllUsers();

        model.addAttribute("data", adminData);
        model.addAttribute("users", users);
        return "admin-dashboard";
    }


    @GetMapping("/users/{userId}/entries")
    public String getUserEntries(@PathVariable Long userId, Model model) {
      
        User user = adminPageService.findById(userId);
        if (user != null) {
            model.addAttribute("user", user);
        } else {
            model.addAttribute("error", "User not found");
        }

        List<Food> userEntries = adminPageService.getUserEntries(userId);
        model.addAttribute("userEntries", userEntries);


        double averageCaloriesPerUser = adminPageService.getAverageCaloriesForUserLastWeek(userId);
        model.addAttribute("averageCaloriesPerUser", averageCaloriesPerUser);

        return "user-entries";
    }

    @GetMapping("/entries/{entryId}/update")
    public String showUpdateForm(@PathVariable Long entryId, Model model) {
        Food entry = adminPageService.getFoodEntry(entryId);
        if (entry == null) {
            return "redirect:/admin";
        }
        model.addAttribute("entry", entry);
        return "update-entry";
    }

    @PostMapping("/entries/{entryId}/update")
    public String updateUserEntry(@PathVariable Long entryId, @ModelAttribute Food updatedEntry) {
        Food existingEntry = adminPageService.getFoodEntry(entryId);
        if (existingEntry != null) {

            existingEntry.setName(updatedEntry.getName());
            existingEntry.setCals(updatedEntry.getCals());
            existingEntry.setPrice(updatedEntry.getPrice());

            adminPageService.updateUserEntry(entryId, existingEntry);


            return "redirect:/admin/users/" + existingEntry.getUser().getId() + "/entries";
        }
        return "redirect:/admin";
    }

    @DeleteMapping("/entries/{entryId}")
    public String deleteEntry(@PathVariable Long entryId) {
        adminPageService.deleteEntry(entryId);
        return "redirect:/admin";
    }

    @GetMapping("/users/{userId}/add-entry")
    public String showAddEntryForm(@PathVariable Long userId, Model model) {
        User user = adminPageService.findById(userId);
        if (user == null) {
            return "redirect:/admin";
        }

        Food newEntry = new Food();
        newEntry.setUser(user);
        model.addAttribute("entry", newEntry);
        model.addAttribute("userId", userId);
        return "admin-add-entry";
    }

    @PostMapping("/users/{userId}/add-entry")
    public String addUserEntry(@PathVariable Long userId, @ModelAttribute Food entry) {
        User user = adminPageService.findById(userId);
        if (user != null) {
            entry.setUser(user);
            entry.setCreatedAt(LocalDateTime.now());
            adminPageService.saveUserEntry(entry);
            return "redirect:/admin/users/" + userId + "/entries";
        }
        return "redirect:/admin";
    }



}
