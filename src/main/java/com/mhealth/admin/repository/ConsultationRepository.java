package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.ConsultationType;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Integer>, JpaSpecificationExecutor<Consultation> {
    //TODO fix this query (this is based on date we need time)
    @Query("SELECT c FROM Consultation c WHERE c.patientId.userId = ?1 AND c.requestType IN ('Book', 'Cancel', 'Pending', 'Failed') AND c.consultationDate > CURRENT_DATE  ORDER BY c.caseId DESC")
    List<Consultation> findUpcomingConsultationsForPatient(Integer userId);

    @Query("SELECT c FROM Consultation c WHERE c.doctorId.userId = ?1 AND c.requestType IN ('Book', 'Cancel') AND TO_TIMESTAMP(CONCAT(c.consultationDate, ' ', c.slotId.slotTime)) > CURRENT_TIMESTAMP ORDER BY c.caseId DESC")
    List<Consultation> findUpcomingConsultationsForDoctor(Integer doctorId);

    @Query(value = "SELECT COUNT(*) FROM mh_consultation c WHERE c.doctor_id = :userId", nativeQuery = true)
    int findTotalCases(Integer userId);

    List<Consultation> findByDoctorIdAndConsultationDate(Users doctor, LocalDate requiredDate);

    @Query("Select u from Consultation u where u.slotId.slotId = ?1 and u.consultationDate=?2 and u.doctorId.userId = ?3 and u.requestType in ?4")
    List<Consultation> findBySlotDateAndDoctorAndRequestType(Integer slotId, LocalDate date, Integer doctorId, List<RequestType> requestTypes);

    @Query("Select u from Consultation u where u.slotId.slotId = ?1 and u.consultationDate=?2 and u.patientId.userId = ?3 and u.requestType in ?4")
    List<Consultation> findBySlotDateAndPatientAndRequestType(Integer slotId, LocalDate date, Integer patientId, List<RequestType> requestTypes);

    @Query("Select u from Consultation u where u.slotId.slotId = ?2 and u.consultationDate=?4 and u.doctorId.userId = ?1 and u.requestType = ?3")
    List<Consultation> findByDoctorIdAndSlotIdAndRequestTypeAndDate(Integer doctorId, Integer slotId, RequestType requestType, LocalDate date);

    @Query("Select u from Consultation u where u.doctorId.userId = ?1 and  u.slotId.slotId = ?2 and u.consultationDate=?3 and u.requestType = ?4")
    List<Consultation> findByDoctorIdAndSlotIdAndRequestTypeAndDate(Integer doctorId, Integer slotId, LocalDate date, RequestType requestType);

    @Query("Select u from Consultation u where u.patientId.userId = ?1 and u.reportSuggested like ?2 and u.requestType =?3 and CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?4% order by u.caseId DESC")
    Page<Consultation> findByPatientReportSuggestedAndRequestTypeAndName(Integer userId, String number, RequestType requestType,String name,Pageable pageable);

    @Query("Select u from Consultation u where u.patientId.userId = ?1 and u.reportSuggested like ?2 and u.requestType =?3 and CONCAT(u.doctorId.firstName,' ', u.doctorId.lastName) like %?4% and u.consultationDate = ?5 order by u.caseId DESC")
    Page<Consultation> findByPatientReportSuggestedAndRequestTypeAndNameAndDate(Integer userId, String number, RequestType requestType, String name, LocalDate date, Pageable pageable);

    @Query("Select u from Consultation u where u.patientId.userId = ?1 and u.requestType in ?2 AND u.slotId.slotStartTime < ?3 and u.slotId.slotStartTime > ?4 and u.consultationDate = ?5 order by u.caseId DESC")
    Optional<Consultation> findUpcomingConsultationForPatient(Integer userId, List<RequestType> requestType, LocalTime start, LocalTime end, LocalDate localDate);

    @Query("Select u from Consultation u where u.patientId.userId = ?1 order by u.caseId DESC")
    Page<Consultation> findByPatientIdOrderByCaseId(Integer userId,Pageable pageable);

    @Query("Select u from Consultation u where u.doctorId.userId = ?1 order by u.caseId DESC")
    Page<Consultation> findByDoctorIdOrderByCaseId(Integer userId, Pageable pageable);

    @Query("Select u from Consultation u where u.patientId.userId = ?1 and u.consultationDate = ?2 order by u.caseId DESC")
    List<Consultation> findByPatientIdAndDateOrderByCaseId(Integer userId, LocalDate date);

    @Query("Select u from Consultation u where u.doctorId.userId = ?1 and u.consultationDate = ?2 order by u.caseId DESC")
    List<Consultation> findByDoctorIdAndDateOrderByCaseId(Integer userId, LocalDate date);

    @Query("Select count(u.caseId) from Consultation u where u.requestType in ?1 and u.slotId.slotId in ?2 and u.doctorId.userId = ?3 and u.consultationDate = ?4")
    Long countByRequestTypeAndSlotIdAndDoctorIdAndConsultationDate(List<RequestType> list, List<Integer> allocatedSlots, Integer doctorId, LocalDate consultationDate);

    @Query("Select u from Consultation u where u.requestType = ?1 and u.createdAt = DATE(?2) and u.patientId.userId = ?3 and" +
            " u.doctorId.userId = ?4 and u.consultationType = ?5 and u.consultationDate = ?6 order by u.consultationDate DESC, u.createdAt DESC")
    List<Consultation> findByRequestTypeAndCreatedAtAndPatientIdAndDoctorIdAndConstaitionTypeAndConstationDate(
            RequestType requestType,LocalDate newOrderDate, Integer patient, Integer doctor
            , ConsultationType consultationType,
            LocalDate consultationDate);

    @Query("Select count(u.caseId) from Consultation u where u.patientId.userId = ?1 and u.doctorId.userId = ?2 and " +
            "u.requestType = ?4 and u.consultationDate = ?3")
    Long countByPatientIdAndDoctorIdAndConsultationDateAndConsultationTypeAndRequestType(
            Users patientId, Integer userId,
            LocalDate consultationDate, RequestType requestType);

    @Query("SELECT COUNT(u.caseId) FROM Consultation u WHERE u.patientId.userId = ?1 AND u.doctorId.userId = ?2 AND " +
            "DATE(u.createdAt) = ?3 AND u.consultationType = ?4 AND u.consultType = ?5 AND " +
            "CONCAT(u.consultationDate, ' ', SUBSTRING(u.slotId.slotTime, -5), ':00') >= ?6")
    Long countByPatientIdAndDoctorIdCreatedAtAndConstaitionTypeConsultTypeAndConstationDate(
            Integer patient, Integer doctor,LocalDate createdAt
            , ConsultationType consultationType, String consultType,
            LocalDateTime consultationDate);

    @Query("Select count(u.caseId) from Consultation u where u.slotId.slotId = ?1 and u.doctorId.userId = ?2 and " +
            " u.consultationDate = ?3")
    Long countBySlotIdAndDoctorIdConsultationDate(Integer slotId, Integer doctorId, LocalDate date);

    @Query("select count(u) from ConsultationRating u")
    Long countConsultationRatings();

    @Query("Select COUNT(u) from Consultation u where u.requestType = ?1")
    Long getTotalConsultations(RequestType requestType);

    @Query("Select COUNT(u) from Consultation u where u.requestType = ?1 and DATE(u.consultationDate) = DATE(?2)")
    Long getTotalConsultations(RequestType requestType,LocalDate date);

    @Query(value = "SELECT u.first_name, u.last_name, COUNT(*) AS total_bookings FROM mh_consultation c JOIN mh_users u ON c.doctor_id = u.user_id WHERE c.request_type = 'Book' GROUP BY c.doctor_id ORDER BY total_bookings DESC LIMIT 5",
            nativeQuery = true)
    List<Object[]> findByTopMostBookDoctor();

    @Query(value = "SELECT u.first_name, u.last_name, COUNT(*) AS total_bookings FROM mh_consultation c JOIN mh_users u \n" +
            "ON c.patient_id = u.user_id GROUP BY c.patient_id ORDER BY total_bookings DESC LIMIT 5;",
            nativeQuery = true)
    List<Object[]> findByTopMostBookPatient();

    @Query(value = "SELECT u.first_name, u.last_name, AVG(r.rating) AS average_rating FROM mh_consultation_rating r JOIN mh_users u ON r.doctor_id = u.user_id WHERE r.status = 'Approve' GROUP BY r.doctor_id ORDER BY average_rating DESC LIMIT 5",
            nativeQuery = true)
    List<Object[]> findByTopMostDoctorReview();

    @Query(value = "SELECT (select p.clinic_name from mh_users p where p.user_id = u.hospital_id and u.hospital_id) as clinic , COUNT(c.case_id) AS total_bookings \n" +
            "FROM mh_consultation c \n" +
            "JOIN mh_users u ON c.doctor_id = u.user_id \n" +
            "WHERE c.request_type = 'Book'\n" +
            "GROUP BY u.hospital_id\n" +
            "ORDER BY total_bookings DESC \n" +
            "LIMIT 5",
            nativeQuery = true)
    List<Object[]> findByTopMostHospital();

}