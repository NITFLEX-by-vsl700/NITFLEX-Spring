package com.vsl700.nitflex.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Watch page
@Controller
public class WatchController {
    @GetMapping("watch/{id}")
    public String watchPage(@PathVariable String id, Model model){
        model.addAttribute("videoFileId", id);
        return "watch";
    }
}