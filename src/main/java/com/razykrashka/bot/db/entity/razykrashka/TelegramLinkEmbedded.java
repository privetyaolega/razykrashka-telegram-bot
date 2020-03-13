package com.razykrashka.bot.db.entity.razykrashka;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramLinkEmbedded {
    String link;
    String textLink;

    @Override
    public String toString() {
        return String.format("<a href='%s'>%s</a>", link, textLink);
    }
}
