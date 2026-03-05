package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class ExpenseServiceImpl {

    private final ExpenseRepository expenseRepo;
    private final StaffRepository staffRepo;

    public ExpenseResponse create(ExpenseRequest req) {
        Expense e = Expense.builder()
            .category(req.getCategory()).description(req.getDescription())
            .amount(req.getAmount()).date(req.getDate())
            .paidBy(req.getPaidBy()).hasReceipt(req.getHasReceipt() != null ? req.getHasReceipt() : false)
            .notes(req.getNotes()).status("pending")
            .build();
        return toResponse(expenseRepo.save(e));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> list(String category, LocalDate from, LocalDate to) {
        LocalDate f = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate t = to != null ? to : LocalDate.now();
        List<Expense> list = category != null
            ? expenseRepo.findByCategoryAndDateBetween(category, f, t)
            : expenseRepo.findByDateBetweenOrderByDateDesc(f, t);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ExpenseResponse update(Long id, ExpenseRequest req) {
        Expense e = findOrThrow(id);
        if ("approved".equals(e.getStatus())) throw new BusinessException("Cannot edit an approved expense");
        e.setCategory(req.getCategory()); e.setDescription(req.getDescription());
        e.setAmount(req.getAmount()); e.setDate(req.getDate());
        e.setPaidBy(req.getPaidBy()); e.setNotes(req.getNotes());
        if (req.getHasReceipt() != null) e.setHasReceipt(req.getHasReceipt());
        return toResponse(expenseRepo.save(e));
    }

    public ExpenseResponse approve(Long id, Long approverStaffId) {
        Expense e = findOrThrow(id);
        Staff approver = staffRepo.findById(approverStaffId)
            .orElseThrow(() -> new ResourceNotFoundException("Staff", approverStaffId));
        e.setStatus("approved");
        e.setApprovedBy(approver);
        return toResponse(expenseRepo.save(e));
    }

    public ExpenseResponse reject(Long id) {
        Expense e = findOrThrow(id);
        e.setStatus("rejected");
        return toResponse(expenseRepo.save(e));
    }

    public void delete(Long id) {
        Expense e = findOrThrow(id);
        if ("approved".equals(e.getStatus())) throw new BusinessException("Cannot delete an approved expense");
        expenseRepo.delete(e);
    }

    private Expense findOrThrow(Long id) {
        return expenseRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Expense", id));
    }

    ExpenseResponse toResponse(Expense e) {
        return ExpenseResponse.builder()
            .id(e.getId()).category(e.getCategory()).description(e.getDescription())
            .amount(e.getAmount()).date(e.getDate()).paidBy(e.getPaidBy())
            .hasReceipt(e.getHasReceipt()).status(e.getStatus()).notes(e.getNotes())
            .build();
    }
}
