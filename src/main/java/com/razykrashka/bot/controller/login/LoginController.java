package com.razykrashka.bot.controller.login;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class LoginController {
    @GetMapping("/login")
    public String loginForm() {
        return "fancy-login.html";
    }

    @GetMapping("/logout")
    public String loginError() {
        return "redirect:/login";
    }
}