package com.bida.employer.manager.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "password_recovery")
public class PasswordRecovery {

    @Id
    @Column(name = "pr_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "pr_token")
    private String token;

    @Column(name = "pr_user_id")
    private UUID userId;

    @Column(name = "pr_expiration_date")
    private LocalDateTime expirationDate;
}
