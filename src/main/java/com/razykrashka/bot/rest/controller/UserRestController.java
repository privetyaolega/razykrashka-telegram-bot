package com.razykrashka.bot.rest.controller;

import com.google.common.collect.Lists;
import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import com.razykrashka.bot.db.repo.TelegramUserRepository;
import com.razykrashka.bot.rest.controller.exception.UserNotFoundException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/api")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRestController {

    final TelegramUserRepository telegramUserRepository;

    public UserRestController(TelegramUserRepository telegramUserRepository) {
        this.telegramUserRepository = telegramUserRepository;
    }

    @GetMapping("/users")
    public ArrayList<TelegramUser> getUsers() {
        return Lists.newArrayList(telegramUserRepository.findAll());
    }

    @GetMapping("/user/{userTelegramId}")
    public TelegramUser getUser(@PathVariable int userTelegramId) {
        return telegramUserRepository.findById(userTelegramId)
                .orElseThrow(() -> new UserNotFoundException("No user with telegramId " + userTelegramId + " were found."));
    }
}