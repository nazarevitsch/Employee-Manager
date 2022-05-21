package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.PostgreSQLEnumType;
import com.bida.employer.manager.domain.enums.UserRole;
import org.hibernate.annotations.*;
import lombok.Data;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
@SQLDelete(sql = "update users set u_is_deleted=true WHERE u_id=?")
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

    @Column(name = "u_last_name")
    private String lastName;

    @Column(name = "u_first_name")
    private String firstName;

    @Column(name = "u_phone_number")
    private String phoneNumber;

    @Column(name = "u_organization_id")
    private UUID organizationId;

    @CreationTimestamp
    @Column(name = "u_creation_date")
    private LocalDateTime creationDate;

    @Column(name = "u_is_active")
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "u_user_role")
    @Type(type = "pgsql_enum")
    private UserRole userRole;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_organization_id", referencedColumnName = "o_id", insertable = false, updatable = false)
    private Organization organization;
}
