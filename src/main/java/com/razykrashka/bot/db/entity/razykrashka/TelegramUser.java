package com.razykrashka.bot.db.entity.razykrashka;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.razykrashka.bot.db.entity.razykrashka.meeting.Meeting;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelegramUser {

    @Id
    Integer id;
    String userName;
    String lastName;
    String firstName;
    String phoneNumber;

    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date membershipDate;

    @Column
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST}, mappedBy = "telegramUser")
    @JsonIgnore
    Set<Meeting> createdMeetings = new HashSet<>();

    @Column
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "user_meeting",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meeting_id")
    )
    @JsonIgnore
    Set<Meeting> toGoMeetings = new HashSet<>();
}