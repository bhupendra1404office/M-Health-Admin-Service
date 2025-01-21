package com.mhealth.admin.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDto {

    // Summary Metrics Section
    private Metrics metrics;

    // Financial Summary Section
    private FinancialSummary financialSummary;

    // Charts Section
    private Charts charts;

    // Inner DTOs for better organization
    @Data
    public static class Metrics {
        private long totalDoctors;
        private long totalPatients;
        private long totalNurses;
        private long totalHospitals;
        private long totalFeedback;
        private long totalDoctorConsultations;
        private long totalCancelledConsultations;
        private long activeDoctors;
        private long totalNurseBookings;
        private long totalSupportTickets;
        private long availableDoctors;
        private long todayDoctorConsultations;
        private long todayCancelledConsultations;
    }

    @Data
    public static class FinancialSummary {
        private double hospitalAmountEarned;
        private double transferredAmount;
        private double balance;
    }

    @Data
    public static class Charts {
        private List<PaymentHistory> paymentHistory; // Area Chart
        private List<TopDoctor> topMostBookedDoctors; // Bar Chart
        private List<TopPatient> topMostBookedPatients; // Pie Chart
        private List<TopHospital> topMostBookedHospitals; // Pie Chart
        private List<DoctorReview> doctorReviews; // Bar Chart
    }

    // Nested DTOs for specific chart data
    @Data
    public static class PaymentHistory {
        private String date;
        private double consultationIncome;
        private double healthTipIncome;
    }

    @Data
    public static class TopDoctor {
        private String doctorName;
        private long numberOfBookings;
    }

    @Data
    public static class TopPatient {
        private String patientName;
        private long numberOfBookings;
    }

    @Data
    public static class TopHospital {
        private String hospitalName;
        private long numberOfBookings;
    }

    @Data
    public static class DoctorReview {
        private String doctorName;
        private Double rating;
    }
}

