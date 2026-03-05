package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "staff")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Staff extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(unique = true)
    private String email;

    @Column(name = "join_date")
    private LocalDate joinDate;

    private String avatar;
    private String gender;

    @Column(precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "commission_rate", precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "target_revenue", precision = 14, scale = 2)
    private BigDecimal targetRevenue;

    @ElementCollection
    @CollectionTable(name = "staff_specializations", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "specialization")
    private List<String> specializations;

    @ElementCollection
    @CollectionTable(name = "staff_working_days", joinColumns = @JoinColumn(name = "staff_id"))
    @Column(name = "day")
    private List<String> workingDays;

    @Column(name = "work_start_time", length = 5)
    private String workStartTime;   // e.g. "09:00"

    @Column(name = "work_end_time", length = 5)
    private String workEndTime;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";  // active, on-leave, inactive

    @Column(columnDefinition = "NUMERIC DEFAULT 0")
    private BigDecimal rating = BigDecimal.ZERO;

    @Column(name = "total_clients", columnDefinition = "INT DEFAULT 0")
    private Integer totalClients = 0;

    @Column(name = "leaves_count", columnDefinition = "INT DEFAULT 0")
    private Integer leavesCount = 0;
}
