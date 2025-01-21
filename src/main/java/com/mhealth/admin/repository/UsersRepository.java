package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.City;
import com.mhealth.admin.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users,Integer> {

    @Query("Select u from Users u where u.userId = ?1")
    Optional<Users> findById(Integer id);

    @Query("Select u from Users u where u.contactNumber like ?1")
    Optional<Users> findByContactNumber(String contactNumber);

    @Query("Select c from City c where c.id in (Select u.city from Users u where u.type = ?1 group by u.city)")
    List<City> getCitiesByUsertype(UserType userType);

    @Query("Select u from Users u where u.status like ?1 and u.type = ?2 and u.isVerified like ?3")
    List<Users> findByStatusAndTypeAndVerified(String a, UserType userType, String yes);

    @Query("Select u from Users u where u.type = ?1")
    List<Users> findByType(UserType userType);

    List<Users> findByHospitalId(int hospitalId);

    @Query("Select u from Users u where u.status LIKE ?1 and u.type = ?2 order by u.sort asc")
    List<Users> findByStatusAndTypeOrderByAsc(String status,UserType type);

    @Query("SELECT u FROM Users u WHERE u.type = 'Doctor' AND u.status = 'A' AND u.hasDoctorVideo IN ('visit', 'both') AND u.hospitalId > 0")
    List<Users> findActiveDoctorsWithVideoAndHospital();
    
    @Query("SELECT o.doctorId.userId FROM Orders o GROUP BY o.doctorId.userId ORDER BY COUNT(o.doctorId.userId) DESC")
    List<Integer> findTopDoctors();

    @Query("SELECT u FROM Users u WHERE u.userId IN (?1)")
    List<Users> findDoctorsByIds(List<Integer> topDoctorIds);

    @Query("SELECT u FROM Users u WHERE u.type = 'Doctor' AND u.status = 'A' AND u.hospitalId IN ?1 AND u.hasDoctorVideo IN ('visit', 'both') AND u.hospitalId > 0")
    List<Users> findNearbyDoctors(List<Integer> hospitalIds);


    @Query("select u from Users u where u.contactNumber = ?1 and u.type = ?2")
    Users findByContactNumberAndType(String contactNumber, UserType userType);

    @Query("Select count(u) from Users u where u.type = ?1")
    Long countUsersByType(UserType userType);

    @Query("SELECT COUNT(DISTINCT u.doctorId) FROM DoctorAvailability u " +
            "WHERE u.slotId.slotDay = :day AND u.slotId.slotStartTime >= :time")
    Long countAvailableDoctors(@Param("day") String day, @Param("time") LocalTime time);


    @Query("Select count(u) from Users u where u.type = ?1 AND u.status = ?2")
    Long countUsersByTypeAndStatus(UserType userType, StatusAI statusAI);

    long countByEmail(String email);
  
    long countByContactNumberAndType(String contactNumber, UserType type);
  
    long countByEmailAndUserIdNot(String email, Integer userId);
  
    long countByContactNumberAndTypeAndUserIdNot(String contactNumber, UserType type, Integer userId);

    Optional<Users> findByUserIdAndType(Integer userId, UserType type);
  
    Users findByUserIdAndType(Users userId, String userType);

}
