package com.growthhub.salon.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ExpenseSummaryResponse {
    private BigDecimal totalApproved;
    private BigDecimal totalPending;
    private List<CategoryBreakdown> byCategory;

    @Data
    @Builder
    public static class CategoryBreakdown {
        private String category;
        private BigDecimal amount;
        private double percentage;
    }
}
