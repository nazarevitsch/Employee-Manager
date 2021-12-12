package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.PostgreSQLEnumType;
import com.bida.employer.manager.domain.enums.UserRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class User {

    @Id
    @Column(name = "u_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "u_email")
    private String email;

    @Column(name = "u_password")
    private String password;

    @Column(name = "u_phone_number")
    private String phoneNumber;

    @Column(name = "u_organization_id")
    private UUID organizationId;

    @CreationTimestamp
    @Column(name = "u_creation_date")
    private LocalDateTime creationDate;

    @Column(name = "u_is_active")
    private boolean isActive;

    @Column(name = "u_activation_code")
    private String activationCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "u_user_role")
    @Type(type = "pgsql_enum")
    private UserRole userRole;
}
