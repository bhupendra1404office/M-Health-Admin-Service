package com.mhealth.admin.service;

import com.mhealth.admin.dto.enums.*;
import com.mhealth.admin.dto.response.DashboardResponseDto;
import com.mhealth.admin.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SupportTicketRepository supportTicketRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private DoctorPaymentRepository doctorPaymentRepository;

    @Autowired
    private PartnerNurseRepository partnerNurseRepository;

    @Autowired
    private NurseDemandOrdersRepository nurseDemandOrdersRepository;
    @Autowired
    private HealthTipOrdersRepository healthTipOrdersRepository;


    public ResponseEntity<?> getDashboardDetails(Locale locale) {
        // Fetching all required metrics
        Long totalDoctorCount = usersRepository.countUsersByType(UserType.Doctor);
        Long totalPatientCount = usersRepository.countUsersByType(UserType.Patient);
        Long totalNursePartners = partnerNurseRepository.countNursePartners();
        Long totalClinicCount = usersRepository.countUsersByType(UserType.Clinic);
        Long totalRatingCount = consultationRepository.countConsultationRatings();
        Long totalConsultations = consultationRepository.getTotalConsultations(RequestType.Book);
        Long totalCancelledConsultations = consultationRepository.getTotalConsultations(RequestType.Cancel);
        Long activeDoctors = usersRepository.countUsersByTypeAndStatus(UserType.Doctor, StatusAI.A);
        Long totalNurseBookings = nurseDemandOrdersRepository.countActiveNurseBookings(StatusFullName.Cancelled);
        Long totalSupportTickets = supportTicketRepository.countSupportTickets();

        String today = LocalDate.now().getDayOfWeek().name().toLowerCase();
        LocalTime currentTime = LocalTime.now();

        Long availableDoctors = usersRepository.countAvailableDoctors(today, currentTime);

        Long todayConsultations = consultationRepository.getTotalConsultations(RequestType.Book, LocalDate.now());
        Long todayCancelledConsultations = consultationRepository.getTotalConsultations(RequestType.Cancel, LocalDate.now());
        Double totalConsultationAmount = ordersRepository.getTotalConsultationAmount(List.of(OrderStatus.Cancelled, OrderStatus.Failed));
        Double completedPayments = doctorPaymentRepository.getTotalCompletedPayments();

        // Populating the DTO
        DashboardResponseDto dto = new DashboardResponseDto();

        // Metrics
        DashboardResponseDto.Metrics metrics = new DashboardResponseDto.Metrics();
        metrics.setTotalDoctors(totalDoctorCount);
        metrics.setTotalPatients(totalPatientCount);
        metrics.setTotalNurses(totalNursePartners.intValue());
        metrics.setTotalHospitals(totalClinicCount.intValue());
        metrics.setTotalFeedback(totalRatingCount.intValue());
        metrics.setTotalDoctorConsultations(totalConsultations);
        metrics.setTotalCancelledConsultations(totalCancelledConsultations);
        metrics.setActiveDoctors(activeDoctors.intValue());
        metrics.setTotalNurseBookings(totalNurseBookings.intValue());
        metrics.setTotalSupportTickets(totalSupportTickets.intValue());
        metrics.setAvailableDoctors(availableDoctors.intValue());
        metrics.setTodayDoctorConsultations(todayConsultations);
        metrics.setTodayCancelledConsultations(todayCancelledConsultations);
        dto.setMetrics(metrics);

        // Financial Summary
        DashboardResponseDto.FinancialSummary financialSummary = new DashboardResponseDto.FinancialSummary();
        financialSummary.setHospitalAmountEarned(totalConsultationAmount);
        financialSummary.setTransferredAmount(completedPayments);
        financialSummary.setBalance(totalConsultationAmount - completedPayments);
        dto.setFinancialSummary(financialSummary);

        // Payment history
        List<Object[]> paymentList = healthTipOrdersRepository.findByIncomeAndConsultation();

        List<DashboardResponseDto.PaymentHistory> paymentHistoryList = getPaymentHistories(paymentList);

        DashboardResponseDto.Charts charts = new DashboardResponseDto.Charts();
        charts.setPaymentHistory(paymentHistoryList);

        // top most doctor booked
        List<Object[]> topBookDoctor = consultationRepository.findByTopMostBookDoctor();

        List<DashboardResponseDto.TopDoctor> topDoctors = getTopMost5BookDoctor(topBookDoctor);
        charts.setTopMostBookedDoctors(topDoctors);

        // top most patient booked
        List<Object[]> topBookPatient = consultationRepository.findByTopMostBookPatient();

        List<DashboardResponseDto.TopPatient> topPatient = getTopMost5BookPatient(topBookPatient);
        charts.setTopMostBookedPatients(topPatient);

        // top most doctor review
        List<Object[]> topBook = consultationRepository.findByTopMostDoctorReview();

        List<DashboardResponseDto.DoctorReview> topMostDoctorReview = getTopMost5DoctorReview(topBook);
        charts.setDoctorReviews(topMostDoctorReview);

        // top Most hospital
        List<Object[]> topMostHospital = consultationRepository.findByTopMostHospital();

        List<DashboardResponseDto.TopHospital> topMost5Hospital = getTopMost5Hospital(topMostHospital);
        charts.setTopMostBookedHospitals(topMost5Hospital);

        dto.setCharts(charts);
        // Return ResponseEntity with the populated DTO
        return ResponseEntity.ok(dto);
    }

    private static List<DashboardResponseDto.PaymentHistory> getPaymentHistories(List<Object[]> paymentList) {
        List<DashboardResponseDto.PaymentHistory> paymentHistoryList = new ArrayList<>();

        for (Object[] record : paymentList) {
            String monthYear = record[0] != null ? (String) record[0] : "Unknown"; // Handle null for monthYear
            double consultationIncome = record[1] != null ? ((Number) record[1]).doubleValue() : 0.0; // Safely cast to double
            double healthTipIncome = record[2] != null ? ((Number) record[2]).doubleValue() : 0.0; // Safely cast to double

            // Create a new PaymentHistory object and set its values
            DashboardResponseDto.PaymentHistory paymentHistory = new DashboardResponseDto.PaymentHistory();
            paymentHistory.setDate(monthYear);
            paymentHistory.setConsultationIncome(consultationIncome);
            paymentHistory.setHealthTipIncome(healthTipIncome);

            // Add to the payment history list
            paymentHistoryList.add(paymentHistory);
        }

        return paymentHistoryList;
    }

    private static List<DashboardResponseDto.TopDoctor> getTopMost5BookDoctor(List<Object[]> list) {
        List<DashboardResponseDto.TopDoctor> topDoctorList = new ArrayList<>();

        for (Object[] record : list) {
            String firstName = record[0] != null ? (String) record[0] : null;
            String lastName = record[1] != null ? (String) record[1] : null;
            long count = record[2] != null ? ((Number) record[2]).longValue() : 0;

            // Create a new PaymentHistory object and set its values
            DashboardResponseDto.TopDoctor dto = new DashboardResponseDto.TopDoctor();
            dto.setDoctorName(firstName + " " + lastName);
            dto.setNumberOfBookings(count);

            topDoctorList.add(dto);
        }

        return topDoctorList;
    }

    private static List<DashboardResponseDto.TopPatient> getTopMost5BookPatient(List<Object[]> list) {
        List<DashboardResponseDto.TopPatient> topList = new ArrayList<>();

        for (Object[] record : list) {
            String firstName = record[0] != null ? (String) record[0] : null;
            String lastName = record[1] != null ? (String) record[1] : null;
            long count = record[2] != null ? ((Number) record[2]).longValue() : 0;

            // Create a new PaymentHistory object and set its values
            DashboardResponseDto.TopPatient dto = new DashboardResponseDto.TopPatient();
            dto.setPatientName(firstName + " " + lastName);
            dto.setNumberOfBookings(count);

            topList.add(dto);
        }

        return topList;
    }

    private static List<DashboardResponseDto.DoctorReview> getTopMost5DoctorReview(List<Object[]> list) {
        List<DashboardResponseDto.DoctorReview> topList = new ArrayList<>();

        for (Object[] record : list) {
            String firstName = record[0] != null ? (String) record[0] : null;
            String lastName = record[1] != null ? (String) record[1] : null;
            Double count = record[2] != null ? ((Number) record[2]).doubleValue() : 0;

            // Create a new PaymentHistory object and set its values
            DashboardResponseDto.DoctorReview dto = new DashboardResponseDto.DoctorReview();
            dto.setDoctorName(firstName + " " + lastName);
            dto.setRating(count);

            topList.add(dto);
        }

        return topList;
    }

    private static List<DashboardResponseDto.TopHospital> getTopMost5Hospital(List<Object[]> list) {
        List<DashboardResponseDto.TopHospital> topList = new ArrayList<>();

        for (Object[] record : list) {
            String clinicName = record[0] != null ? (String) record[0] : null;
            long count = record[1] != null ? ((Number) record[1]).longValue() : 0;

            // Create a new PaymentHistory object and set its values
            DashboardResponseDto.TopHospital dto = new DashboardResponseDto.TopHospital();
            dto.setHospitalName(clinicName);
            dto.setNumberOfBookings(count);

            topList.add(dto);
        }

        return topList;
    }





}
