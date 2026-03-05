package com.growthhub.salon.dto.response;

import com.growthhub.salon.enums.ClientTier;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ClientLoyaltyResponse {
    private Long clientId;
    private String clientName;
    private ClientTier tier;
    private Integer loyaltyPoints;
    private BigDecimal redeemableValue;
    private BigDecimal totalSpend;
    private String activeMembershipName;
    private LocalDate membershipExpiry;
    private List<LoyaltyTransactionResponse> recentTransactions;
}
