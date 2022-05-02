package com.bida.employer.manager.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "shift")
public class Shift {

    @Id
    @Column(name = "s_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "s_title")
    private String title;

    @Column(name = "s_description")
    private String description;

    @Column(name = "s_shift_start")
    private LocalDateTime shiftStart;

    @Column(name = "s_shift_finish")
    private LocalDateTime shiftFinish;

    @Column(name = "s_last_modification_date")
    private LocalDateTime lastModificationDate;

    @Column(name = "s_last_modification_user")
    private UUID lastModificationUser;

    @Column(name = "s_user_id")
    private UUID userId;
}
