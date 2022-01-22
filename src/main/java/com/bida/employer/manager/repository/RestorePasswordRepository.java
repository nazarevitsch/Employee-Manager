package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.RestorePassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface RestorePasswordRepository extends JpaRepository<RestorePassword, UUID> {

    @Modifying
    @Transactional
    void deleteByUserId(UUID userId);

    RestorePassword findByUserId(UUID userId);
}
