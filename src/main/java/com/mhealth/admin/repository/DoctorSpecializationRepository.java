package com.mhealth.admin.repository;

import com.mhealth.admin.model.DoctorSpecialization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DoctorSpecializationRepository extends JpaRepository<DoctorSpecialization, Integer> {
    @Query("Select u from DoctorSpecialization u where u.userId.userId = ?1")
    List<DoctorSpecialization> findByUserId(Integer val);

    @Query("Select u from DoctorSpecialization u where u.specializationId.id =?1")
    List<Integer> getDoctorIdFromSpecializationId(Integer specializationId);
}