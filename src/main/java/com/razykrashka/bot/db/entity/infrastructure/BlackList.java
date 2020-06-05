package com.razykrashka.bot.db.entity.infrastructure;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    Integer userId;
    String description;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date date;
}