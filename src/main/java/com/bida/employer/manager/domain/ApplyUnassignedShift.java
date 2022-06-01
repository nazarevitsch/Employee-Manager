package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.PostgreSQLEnumType;
import lombok.Data;
import org.hibernate.annotations.*;

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
        typeClass = PostgreSQLEnumType.class)
@SQLDelete(sql = "update users set u_is_deleted=true WHERE u_id=?")
public class ApplyUnassignedShift {

    @Id
    @Column(name = "aus_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @CreationTimestamp
    @Column(name = "aus_apply_date")
    private LocalDateTime applyDate;

    @Column(name = "aus_user_id")
    private UUID userId;

    @Column(name = "aus_shift_id")
    private UUID shiftId;
}
