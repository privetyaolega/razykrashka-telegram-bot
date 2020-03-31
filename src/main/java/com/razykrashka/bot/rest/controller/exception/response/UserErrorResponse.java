package com.razykrashka.bot.rest.controller.exception.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserErrorResponse {
    int status;
    String message;
    LocalDateTime date;
}