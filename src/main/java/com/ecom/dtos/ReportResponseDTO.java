package com.ecom.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportResponseDTO {
    private Double totalIncome;
    private Double totalExpense;
    private Double profit;
    private Double profitRate; // percent (ex: 23.5)
    private List<MonthlyReportDTO> monthlyData;
}
