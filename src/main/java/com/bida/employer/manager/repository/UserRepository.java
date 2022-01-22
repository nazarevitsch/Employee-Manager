package com.bida.employer.manager.repository;

import com.bida.employer.manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    User findUserByEmail(String email);

    User findUserByPhoneNumber(String phoneNumber);

    List<User> findAllByOrganizationId(UUID organizationId);

    @Query(value = "select count(*) from users where u_organization_id = :organizationId", nativeQuery = true)
    int countEmployersByOrganizationId(@Param("organizationId") UUID organizationId);

    @Query(value = "update users set u_is_active = true, u_password = :newPassword where u_id = :userId", nativeQuery = true)
    void setNewPassword(@Param("userId") UUID userId, @Param("newPassword") String newPassword);
}
