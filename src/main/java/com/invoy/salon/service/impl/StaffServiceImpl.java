package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.Staff;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class StaffServiceImpl {

    private final StaffRepository staffRepo;

    public StaffResponse create(StaffRequest req) {
        if (staffRepo.existsByPhone(req.getPhone()))
            throw new DuplicateResourceException("Phone already registered: " + req.getPhone());

        Staff staff = Staff.builder()
            .name(req.getName()).role(req.getRole()).phone(req.getPhone())
            .email(req.getEmail()).joinDate(req.getJoinDate() != null ? req.getJoinDate() : LocalDate.now())
            .gender(req.getGender()).salary(req.getSalary()).commissionRate(req.getCommissionRate())
            .targetRevenue(req.getTargetRevenue()).specializations(req.getSpecializations())
            .workingDays(req.getWorkingDays()).workStartTime(req.getWorkStartTime())
            .workEndTime(req.getWorkEndTime()).status(req.getStatus() != null ? req.getStatus() : "active")
            .avatar(initials(req.getName()))
            .build();
        return toResponse(staffRepo.save(staff));
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> list(String status) {
        List<Staff> list = status != null ? staffRepo.findByStatus(status) : staffRepo.findAll();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StaffResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public StaffResponse update(Long id, StaffRequest req) {
        Staff s = findOrThrow(id);
        if (!s.getPhone().equals(req.getPhone()) && staffRepo.existsByPhone(req.getPhone()))
            throw new DuplicateResourceException("Phone already registered");

        s.setName(req.getName()); s.setRole(req.getRole()); s.setPhone(req.getPhone());
        s.setEmail(req.getEmail()); s.setGender(req.getGender()); s.setSalary(req.getSalary());
        s.setCommissionRate(req.getCommissionRate()); s.setTargetRevenue(req.getTargetRevenue());
        s.setSpecializations(req.getSpecializations()); s.setWorkingDays(req.getWorkingDays());
        s.setWorkStartTime(req.getWorkStartTime()); s.setWorkEndTime(req.getWorkEndTime());
        if (req.getStatus() != null) s.setStatus(req.getStatus());
        return toResponse(staffRepo.save(s));
    }

    public void updateStatus(Long id, String status) {
        Staff s = findOrThrow(id);
        s.setStatus(status);
        staffRepo.save(s);
    }

    private Staff findOrThrow(Long id) {
        return staffRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staff", id));
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
        return sb.length() > 2 ? sb.substring(0, 2) : sb.toString();
    }

    public StaffResponse toResponse(Staff s) {
        return StaffResponse.builder()
            .id(s.getId()).name(s.getName()).role(s.getRole()).phone(s.getPhone()).email(s.getEmail())
            .joinDate(s.getJoinDate()).avatar(s.getAvatar()).gender(s.getGender())
            .salary(s.getSalary()).commissionRate(s.getCommissionRate()).targetRevenue(s.getTargetRevenue())
            .specializations(s.getSpecializations()).workingDays(s.getWorkingDays())
            .workStartTime(s.getWorkStartTime()).workEndTime(s.getWorkEndTime())
            .status(s.getStatus()).rating(s.getRating()).totalClients(s.getTotalClients())
            .leavesCount(s.getLeavesCount()).createdAt(s.getCreatedAt())
            .build();
    }
}
