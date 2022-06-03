package com.bida.employer.manager.domain;

import com.bida.employer.manager.domain.enums.CheckInOutEnum;
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
@Table(name = "check_in_out")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class CheckInOut {

    @Id
    @Column(name = "cio_id")
    @Type(type = "pg-uuid")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @CreationTimestamp
    @Column(name = "cio_check_time")
    private LocalDateTime checkTime;

    @Column(name = "cio_note")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "u_check_in_out")
    @Type(type = "pgsql_enum")
    private CheckInOutEnum checkInOut;

    @Column(name = "cio_shift_id")
    private UUID shiftId;
}
