package com.mhealth.admin.repository;

import com.mhealth.admin.model.HealthTipOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HealthTipOrdersRepository extends JpaRepository<HealthTipOrders, Integer> {
    @Query("Select u from HealthTipOrders u where u.patientId.userId = ?1 and u.healthTipPackage.packageId = ?2")
    List<HealthTipOrders> findByPatientIdAndHathTipPackageId(Integer userId, Integer packageId);

    @Query(value = "SELECT \n" +
            "    a.month_year,\n" +
            "    SUM(a.consultation_total_amount) AS consultation_total_amount,\n" +
            "    SUM(b.healthtip_total_amount) AS healthtip_total_amount\n" +
            "FROM (\n" +
            "    SELECT \n" +
            "        DATE_FORMAT(create_at, '%Y-%m') AS month_year,\n" +
            "        SUM(doctor_amount) AS consultation_total_amount\n" +
            "    FROM mh_orders\n" +
            "    WHERE status NOT IN ('Cancelled', 'Failed')\n" +
            "    GROUP BY DATE_FORMAT(create_at, '%Y-%m')\n" +
            ") AS a\n" +
            "LEFT JOIN (\n" +
            "    SELECT \n" +
            "        DATE_FORMAT(create_at, '%Y-%m') AS month_year,\n" +
            "        SUM(amount) AS healthtip_total_amount\n" +
            "    FROM mh_healthtip_orders\n" +
            "    GROUP BY DATE_FORMAT(create_at, '%Y-%m')\n" +
            ") AS b\n" +
            "ON a.month_year = b.month_year\n" +
            "GROUP BY a.month_year\n" +
            "ORDER BY a.month_year desc;",
            nativeQuery = true)
    List<Object[]> findByIncomeAndConsultation();

}