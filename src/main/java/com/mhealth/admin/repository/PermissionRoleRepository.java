package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.PermissionRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRoleRepository extends JpaRepository<PermissionRole, Integer> {

    @Query("Select u from PermissionRole u where u.roleType = ?1")
    Optional<PermissionRole> findByUserType(UserType type);
}
