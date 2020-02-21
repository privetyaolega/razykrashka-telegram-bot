package com.razykrashka.bot.db.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "location")
@Getter
@Setter
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    @Id
    @GeneratedValue
    Integer id;
    Float latitude;
    Float longitude;
    String address;
    String name;
}
