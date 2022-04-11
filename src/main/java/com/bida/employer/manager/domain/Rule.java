package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.*;
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
@Table(name = "rule")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class Rule {

    @Id
    @Column(name = "r_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_swap_shifts_rule")
    @Type(type = "pgsql_enum")
    private SwapShiftRule swapShiftRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_substitute_me_rule")
    @Type(type = "pgsql_enum")
    private SubstituteMeRule substituteMeRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_check_in_rule")
    @Type(type = "pgsql_enum")
    private CheckInRule checkInRule;

    @Enumerated(EnumType.STRING)
    @Column(name = "r_not_assigned_shift_rule")
    @Type(type = "pgsql_enum")
    private NotAssignedShiftRule notAssignedShiftRule;

    @Column(name = "r_max_employee_shift_application")
    private int maxEmployeeShiftApplication;

    @Column(name = "r_last_update_date")
    @CreationTimestamp
    private LocalDateTime lastUpdateDate;

    @Column(name = "r_organization_id")
    private UUID organizationId;
}
