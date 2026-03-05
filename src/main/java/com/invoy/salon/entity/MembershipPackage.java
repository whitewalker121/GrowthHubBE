package com.growthhub.salon.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "membership_packages")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class MembershipPackage extends Auditable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer validity;  // days

    @Column(name = "included_service_count")
    private Integer includedServiceCount;

    @Column(name = "bonus_wallet", precision = 12, scale = 2)
    private BigDecimal bonusWallet = BigDecimal.ZERO;

    @Column(name = "discount_pct", precision = 5, scale = 2)
    private BigDecimal discountPct = BigDecimal.ZERO;

    @Column(length = 20, columnDefinition = "VARCHAR(20) DEFAULT 'active'")
    private String status = "active";

    private String color;

    @ElementCollection
    @CollectionTable(name = "membership_package_services", joinColumns = @JoinColumn(name = "package_id"))
    @Column(name = "service_name")
    private List<String> services;
}
