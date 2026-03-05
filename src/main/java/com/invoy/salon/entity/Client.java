package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "clients")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Client extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String phone;

    private String email;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    private String gender;   // MALE / FEMALE / OTHER
    private String address;
    private String avatar;

    @Column(name = "total_visits", columnDefinition = "INT DEFAULT 0")
    private Integer totalVisits = 0;

    @Column(name = "total_spend", precision = 14, scale = 2, columnDefinition = "NUMERIC DEFAULT 0")
    private BigDecimal totalSpend = BigDecimal.ZERO;

    @Column(name = "last_visit")
    private LocalDate lastVisit;

    @Column(name = "membership_type", length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'Basic'")
    private String membershipType = "Basic";  // Basic, Silver, Gold, Platinum

    @Column(name = "join_date")
    private LocalDate joinDate;

    private String notes;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @ElementCollection
    @CollectionTable(name = "client_tags", joinColumns = @JoinColumn(name = "client_id"))
    @Column(name = "tag")
    private List<String> tags;
}
