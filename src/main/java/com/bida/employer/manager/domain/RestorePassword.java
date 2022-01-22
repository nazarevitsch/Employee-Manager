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
@Table(name = "restore_password")
public class RestorePassword {

    @Id
    @Column(name = "rp_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "rp_token")
    private String token;

    @Column(name = "rp_user_id")
    private UUID userId;

    @CreationTimestamp
    @Column(name = "rp_expiration_date")
    private LocalDateTime expirationDate;
}
