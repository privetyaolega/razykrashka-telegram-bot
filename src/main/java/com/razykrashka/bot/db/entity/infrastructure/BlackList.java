package com.razykrashka.bot.db.entity.infrastructure;

import com.razykrashka.bot.db.entity.razykrashka.TelegramUser;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlackList {
    @Id
    @GeneratedValue
    Long id;

    @OneToOne
    @JoinColumn(name = "userId")
    TelegramUser user;
    String description;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date date;
}