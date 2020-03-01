package com.razykrashka.bot.db.entity.razykrashka;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@Data
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
