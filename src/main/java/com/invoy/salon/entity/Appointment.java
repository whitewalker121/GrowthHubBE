package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private SalonService service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id", nullable = false)
    private Staff staff;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private Integer duration;   // minutes

    @Column(precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'upcoming'")
    private String status = "upcoming";
    // upcoming, confirmed, in-progress, completed, cancelled, no-show

    private String notes;

    @Column(name = "source", length = 30, columnDefinition = "VARCHAR(30) DEFAULT 'walkin'")
    private String source = "walkin";  // walkin, online, phone
}
