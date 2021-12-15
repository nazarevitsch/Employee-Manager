package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findUserByEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);

    @Modifying
    @Transactional
    @Query(value = "update users set u_activation_code = :activationCode, u_is_active = false where u_id = :id", nativeQuery = true)
    void setNewActivationCode(@Param("id") UUID id, @Param("activationCode") String activationCode);

    @Modifying
    @Transactional
    @Query(value = "update users set u_activation_code = null where u_id = :id", nativeQuery = true)
    void setNullActivationCode(@Param("id") UUID id);

    @Transactional
    @Modifying
    @Query(value = "update users set u_activation_code = null, u_is_active = true, u_password = :password where u_id = :id", nativeQuery = true)
    void activate(@Param("id") UUID id, @Param("password") String password);
}
