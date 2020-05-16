package com.razykrashka.bot.controller.rest.response;

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