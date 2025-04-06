package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AdminController {

    @GetMapping("/Admin/Profile")
    public String adminProfile(Model model) {

        /* this is only for one table, for multiple table with different data, modify this with Map, im not gonna do it now */

        List<String> headers = List.of("ID", "Name", "Email", "Phone");
        List<List<Object>> rows = new ArrayList<>();

        // Generate 100 rows to force vertical scrollbar (For Testing)
        for (int i = 1; i <= 100; i++) {
            rows.add(List.of(i, "User " + i, "user" + i + "@example.com", "123-456-78" + (i % 10)));
        }

        model.addAttribute("headers", headers);
        model.addAttribute("rows", rows);
        
        return "admin/adminProfile";
    }
}

