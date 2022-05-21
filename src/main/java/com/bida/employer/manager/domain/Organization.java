package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.OrganizationSize;
import com.bida.employer.manager.domain.enums.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "organization")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class Organization {

    @Id
    @Column(name = "o_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(name = "o_name")
    private String name;

    @Column(name = "o_active_end_date")
    private LocalDateTime activeEndDate;

    @CreationTimestamp
    @Column(name = "o_creation_date")
    private LocalDateTime creationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "o_organization_size")
    @Type(type = "pgsql_enum")
    private OrganizationSize organizationSize;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "o_id", referencedColumnName = "r_organization_id", insertable = false, updatable = false)
    private Rule rule;
}
