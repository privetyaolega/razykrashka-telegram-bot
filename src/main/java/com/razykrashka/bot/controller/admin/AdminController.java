package com.razykrashka.bot.controller.admin;


import com.razykrashka.bot.service.config.property.meeting.MeetingProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    MeetingProperties meetingProperties;

    @GetMapping("/meeting-properties")
    public String meetingProperties(Model model) {
        model.addAttribute("properties", meetingProperties);
        return "meeting-properties.html";
    }

    @GetMapping("/meeting-properties-edit")
    public String meetingPropertiesEdit(Model model) {
        model.addAttribute("properties", meetingProperties);
        return "meeting-properties-edit.html";
    }

    @PostMapping("/save")
    public String saveProperties(@ModelAttribute("prop") MeetingProperties newProps) {
        meetingProperties.setViewPerPage(newProps.getViewPerPage());
        meetingProperties.setSession(newProps.getSession());
        return "redirect:/admin/meeting-properties";
    }
}