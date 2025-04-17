package com.ecom.service.impl;

import com.ecom.dtos.MonthlyReportDTO;
import com.ecom.dtos.ReportResponseDTO;
import com.ecom.repository.ImportRepository;
import com.ecom.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrdersRepository ordersRepository;
    private final ImportRepository importRepository;

    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public ReportResponseDTO getReport(int year) {
        Map<Integer, Double> incomeMap = new HashMap<>();
        Map<Integer, Double> expenseMap = new HashMap<>();

        // Query từ DB
        List<Object[]> incomeData = ordersRepository.getMonthlyIncome(year);
        List<Object[]> expenseData = importRepository.getMonthlyExpense(year);

        // Gộp dữ liệu vào map
        for (Object[] row : incomeData) {
            Integer month = (Integer) row[0];
            Double amount = (Double) row[1];
            incomeMap.put(month, amount);
        }

        for (Object[] row : expenseData) {
            Integer month = (Integer) row[0];
            Double amount = (Double) row[1];
            expenseMap.put(month, amount);
        }

        List<MonthlyReportDTO> monthlyData = new ArrayList<>();
        double totalIncome = 0;
        double totalExpense = 0;

        for (int i = 1; i <= 12; i++) {
            double income = incomeMap.getOrDefault(i, 0.0);
            double expense = expenseMap.getOrDefault(i, 0.0);
            monthlyData.add(new MonthlyReportDTO(MONTHS[i - 1], income, expense));
            totalIncome += income;
            totalExpense += expense;
        }

        double profit = totalIncome - totalExpense;
        double profitRate = (totalIncome > 0) ? (profit / totalIncome) * 100 : 0;

        return ReportResponseDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .profit(profit)
                .profitRate(Math.round(profitRate * 10) / 10.0) // làm tròn 1 chữ số thập phân
                .monthlyData(monthlyData)
                .build();
    }
}