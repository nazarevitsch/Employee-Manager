package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.PasswordRecovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface PasswordRecoveryRepository extends JpaRepository<PasswordRecovery, UUID> {

    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);

    PasswordRecovery findByUserId(UUID userId);
}
