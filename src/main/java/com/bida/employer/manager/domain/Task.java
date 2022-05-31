package com.bida.employer.manager.domain;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "task")
public class Task {

    @Id
    @Column(name = "t_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "t_title")
    private String title;

    @Column(name = "t_description")
    private String description;

    @Column(name = "t_task_time")
    private LocalDateTime taskTime;

    @Column(name = "t_shift_id")
    private UUID shiftId;

    @ManyToOne
    @JoinColumn(name = "t_shift_id", nullable = false, insertable = false, updatable = false)
    private Shift shift;
}
