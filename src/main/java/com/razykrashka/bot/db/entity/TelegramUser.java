package com.razykrashka.bot.db.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramUser {

    @Id
    @GeneratedValue
    Integer id;
    String userName;
    String lastName;
    String firstName;
    String phoneNumber;

    @Column
    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "owner")
    List<Meeting> createdMeetings;

    @Column
    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meeting_id")
    )
    List<Meeting> toGoMeetings;
}