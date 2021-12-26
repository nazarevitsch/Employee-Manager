package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.PostgreSQLEnumType;
import com.bida.employer.manager.domain.enums.SubstituteMeRule;
import com.bida.employer.manager.domain.enums.SwapShiftRule;
import lombok.Data;
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

    @Column(name = "r_last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "r_last_update_user_id")
    private UUID lastUpdateId;

    @Column(name = "r_organization_id")
    private UUID organizationId;
}
