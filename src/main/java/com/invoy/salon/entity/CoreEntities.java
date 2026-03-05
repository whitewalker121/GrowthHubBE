//package com.growthhub.salon.entity;
//
//import com.growthhub.salon.enums.*;
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.*;
//import java.util.*;
//
//// ─────────────────────────────────────────────────────────────
//// USER (auth)
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "users")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class User extends BaseEntity {
//
//    @Column(nullable = false, unique = true, length = 150)
//    private String email;
//
//    @Column(name = "password_hash", nullable = false, length = 255)
//    private String passwordHash;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private UserRole role = UserRole.RECEPTIONIST;
//
//    @Column(name = "full_name", nullable = false, length = 150)
//    private String fullName;
//
//    @Column(length = 20)
//    private String phone;
//
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive = true;
//
//    @Column(name = "last_login_at")
//    private Instant lastLoginAt;
//
//    @Column(name = "refresh_token", columnDefinition = "TEXT")
//    private String refreshToken;
//}
//
//// ─────────────────────────────────────────────────────────────
//// STAFF SPECIALIZATION
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "staff_specializations")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//@IdClass(StaffSpecialization.SpecializationId.class)
//class StaffSpecialization {
//
//    @Id
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "staff_id")
//    private Staff staff;
//
//    @Id
//    @Column(name = "specialization", length = 100)
//    private String specialization;
//
//    @lombok.Data
//    @lombok.NoArgsConstructor
//    @lombok.AllArgsConstructor
//    public static class SpecializationId implements java.io.Serializable {
//        private UUID staff;
//        private String specialization;
//    }
//}
//
//// ─────────────────────────────────────────────────────────────
//// SERVICE CATEGORY
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "service_categories")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class ServiceCategory extends BaseEntity {
//
//    @Column(nullable = false, unique = true, length = 100)
//    private String name;
//
//    @Column(length = 10)
//    private String icon;
//
//    @Column(length = 7)
//    private String color;
//
//    @Column(name = "sort_order", nullable = false)
//    private Integer sortOrder = 0;
//
//    @Column(name = "is_active", nullable = false)
//    private Boolean isActive = true;
//
//    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
//    private List<Service> services = new ArrayList<>();
//}
//
//// ─────────────────────────────────────────────────────────────
//// SERVICE
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "services")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class Service extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id", nullable = false)
//    private ServiceCategory category;
//
//    @Column(nullable = false, length = 150)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(name = "duration_mins", nullable = false)
//    private Integer durationMins = 30;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal price;
//
//    @Column(precision = 10, scale = 2)
//    private BigDecimal mrp;
//
//    @Column(name = "gst_percent", nullable = false, precision = 5, scale = 2)
//    private BigDecimal gstPercent = BigDecimal.valueOf(18);
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private ServiceStatus status = ServiceStatus.ACTIVE;
//
//    @Column(name = "is_popular", nullable = false)
//    private Boolean isPopular = false;
//
//    @Column(name = "sort_order", nullable = false)
//    private Integer sortOrder = 0;
//}
//
//// ─────────────────────────────────────────────────────────────
//// APPOINTMENT
//// ─────────────────────────────────────────────────────────────
//@Entity @Table(name = "appointments")
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//class Appointment extends BaseEntity {
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "client_id", nullable = false)
//    private Client client;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "staff_id", nullable = false)
//    private Staff staff;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "service_id", nullable = false)
//    private Service service;
//
//    @Column(name = "appointment_date", nullable = false)
//    private LocalDate appointmentDate;
//
//    @Column(name = "start_time", nullable = false)
//    private LocalTime startTime;
//
//    @Column(name = "end_time", nullable = false)
//    private LocalTime endTime;
//
//    @Column(name = "duration_mins", nullable = false)
//    private Integer durationMins;
//
//    @Column(nullable = false, precision = 10, scale = 2)
//    private BigDecimal amount;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private AppointmentStatus status = AppointmentStatus.PENDING;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private BookingSource source = BookingSource.WALK_IN;
//
//    @Column(columnDefinition = "TEXT")
//    private String notes;
//
//    @Column(name = "reminder_sent", nullable = false)
//    private Boolean reminderSent = false;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "invoice_id")
//    private Invoice invoice;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "created_by")
//    private User createdBy;
//
//    @Column(name = "cancelled_at")
//    private Instant cancelledAt;
//
//    @Column(name = "cancel_reason")
//    private String cancelReason;
//}
