package com.mhealth.admin.repository;

import com.mhealth.admin.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Integer> {
    @Query("Select u from Permission u where u.level = ?1")
    List<Permission> findByLevel(int i);

    @Query("Select u from Permission u where u.id in ?1")
    List<Permission> findByIds(List<Integer> ids);
}
