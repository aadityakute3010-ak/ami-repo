package com.ami.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ami.dto.responses.BillingDashboardResponseDto;
import com.ami.dto.responses.InvoiceStatusSummaryResponseDto;
import com.ami.dto.responses.RevenueTrendResponseDto;
import com.ami.dto.responses.SourceWiseRevenueResponseDto;
import com.ami.entity.User;
import com.ami.enums.InvoiceStatus;
import com.ami.enums.RoleType;
import com.ami.enums.SourceType;
import com.ami.repository.InvoiceRepository;
import com.ami.security.SecurityUtils;
import com.ami.service.BillingDashboardService;
import com.ami.service.InvoiceOverdueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BillingDashboardServiceImpl implements BillingDashboardService {

    private final InvoiceRepository invoiceRepository;

    private final SecurityUtils securityUtils;
    
    private final InvoiceOverdueService invoiceOverdueService;

    @Override
    @Transactional 
    public BillingDashboardResponseDto getDashboard(Integer year) {
    	
    	invoiceOverdueService.updateOverdueInvoices();

        User loggedInUser = securityUtils.getLoggedInUser();

        Long adminId = null;
        Long userId = null;
        
        int selectedYear = year != null ? year : LocalDate.now().getYear();

        if (loggedInUser.getRole() == RoleType.ADMIN) {
            adminId = loggedInUser.getId();
        } else if (loggedInUser.getRole() == RoleType.USER) {
            userId = loggedInUser.getId();
        }

        BigDecimal totalRevenue = invoiceRepository.getTotalRevenue(adminId, userId);
        BigDecimal collectedRevenue = invoiceRepository.getCollectedRevenue(adminId, userId);
        BigDecimal pendingRevenue = invoiceRepository.getPendingRevenue(adminId, userId);
        BigDecimal overdueRevenue = invoiceRepository.getOverdueRevenue(adminId, userId);

        long totalInvoices = invoiceRepository.countInvoices(adminId, userId);
        long paidInvoices = invoiceRepository.countByStatusForDashboard(adminId, userId, InvoiceStatus.PAID);
        long pendingInvoices = invoiceRepository.countByStatusForDashboard(adminId, userId, InvoiceStatus.PENDING);
        long overdueInvoices = invoiceRepository.countByStatusForDashboard(adminId, userId, InvoiceStatus.OVERDUE);
        long failedInvoices = invoiceRepository.countByStatusForDashboard(adminId, userId, InvoiceStatus.FAILED);

        return BillingDashboardResponseDto.builder()
                .totalRevenue(defaultZero(totalRevenue))
                .collectedRevenue(defaultZero(collectedRevenue))
                .pendingRevenue(defaultZero(pendingRevenue))
                .overdueRevenue(defaultZero(overdueRevenue))
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .pendingInvoices(pendingInvoices)
                .overdueInvoices(overdueInvoices)
                .failedInvoices(failedInvoices)
                .revenueTrend(buildRevenueTrend(adminId, userId, selectedYear))
                .statusSummary(buildStatusSummary(adminId, userId))
                .sourceWiseRevenue(buildSourceWiseRevenue(adminId, userId))
                .build();
    }

    private List<RevenueTrendResponseDto> buildRevenueTrend(Long adminId, Long userId, int year) {

        List<Object[]> rows = invoiceRepository.getRevenueTrend(adminId, userId, year);

        Map<Integer, Object[]> monthMap = new java.util.HashMap<>();

        for (Object[] row : rows) {
            Integer month = ((Number) row[0]).intValue();
            monthMap.put(month, row);
        }

        List<RevenueTrendResponseDto> response = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {

            Object[] row = monthMap.get(month);

            BigDecimal revenue = row != null ? toBigDecimal(row[1]) : BigDecimal.ZERO;
            BigDecimal collected = row != null ? toBigDecimal(row[2]) : BigDecimal.ZERO;
            BigDecimal pending = row != null ? toBigDecimal(row[3]) : BigDecimal.ZERO;
            BigDecimal overdue = row != null ? toBigDecimal(row[4]) : BigDecimal.ZERO;

            response.add(RevenueTrendResponseDto.builder()
                    .month(month)
                    .monthName(Month.of(month).name())
                    .revenue(revenue)
                    .collected(collected)
                    .pending(pending)
                    .overdue(overdue)
                    .build());
        }

        return response;
    }

    private List<InvoiceStatusSummaryResponseDto> buildStatusSummary(Long adminId, Long userId) {

        List<Object[]> rows = invoiceRepository.getInvoiceStatusSummary(adminId, userId);

        Map<InvoiceStatus, Long> statusMap = new EnumMap<>(InvoiceStatus.class);

        for (Object[] row : rows) {
            InvoiceStatus status = (InvoiceStatus) row[0];
            Long count = ((Number) row[1]).longValue();
            statusMap.put(status, count);
        }

        List<InvoiceStatusSummaryResponseDto> response = new ArrayList<>();

        for (InvoiceStatus status : InvoiceStatus.values()) {
            response.add(InvoiceStatusSummaryResponseDto.builder()
                    .status(status)
                    .count(statusMap.getOrDefault(status, 0L))
                    .build());
        }

        return response;
    }

    private List<SourceWiseRevenueResponseDto> buildSourceWiseRevenue(Long adminId, Long userId) {

        List<Object[]> rows = invoiceRepository.getSourceWiseRevenue(adminId, userId);

        Map<SourceType, Object[]> sourceMap = new EnumMap<>(SourceType.class);

        for (Object[] row : rows) {
            SourceType source = (SourceType) row[0];
            sourceMap.put(source, row);
        }

        List<SourceWiseRevenueResponseDto> response = new ArrayList<>();

        for (SourceType source : SourceType.values()) {

            if (source.name().equals("ALL")) {
                continue;
            }

            Object[] row = sourceMap.get(source);

            response.add(SourceWiseRevenueResponseDto.builder()
                    .source(source)
                    .invoices(row != null ? ((Number) row[1]).longValue() : 0L)
                    .revenue(row != null ? toBigDecimal(row[2]) : BigDecimal.ZERO)
                    .collected(row != null ? toBigDecimal(row[3]) : BigDecimal.ZERO)
                    .pending(row != null ? toBigDecimal(row[4]) : BigDecimal.ZERO)
                    .overdue(row != null ? toBigDecimal(row[5]) : BigDecimal.ZERO)
                    .build());
        }

        return response;
    }

    private BigDecimal toBigDecimal(Object value) {

        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof BigDecimal decimal) {
            return decimal;
        }

        return BigDecimal.valueOf(((Number) value).doubleValue());
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}