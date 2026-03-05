package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class AttendanceServiceImpl {

    private final AttendanceRepository attendanceRepo;
    private final StaffRepository staffRepo;

    public AttendanceResponse markAttendance(AttendanceRequest req) {
        Staff staff = staffRepo.findById(req.getStaffId())
            .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()));

        attendanceRepo.findByStaffIdAndDate(staff.getId(), req.getDate()).ifPresent(existing -> {
            throw new DuplicateResourceException("Attendance already marked for " + staff.getName() + " on " + req.getDate());
        });

        BigDecimal hours = BigDecimal.ZERO;
        BigDecimal overtime = BigDecimal.ZERO;
        if (req.getCheckIn() != null && req.getCheckOut() != null) {
            long mins = Duration.between(req.getCheckIn(), req.getCheckOut()).toMinutes();
            hours = BigDecimal.valueOf(mins).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            if (hours.compareTo(BigDecimal.valueOf(9)) > 0)
                overtime = hours.subtract(BigDecimal.valueOf(9));
        }

        Attendance att = Attendance.builder()
            .staff(staff).date(req.getDate())
            .checkIn(req.getCheckIn()).checkOut(req.getCheckOut())
            .hoursWorked(hours).overtimeHours(overtime)
            .status(req.getStatus() != null ? req.getStatus() : "present")
            .notes(req.getNotes())
            .build();
        return toResponse(attendanceRepo.save(att));
    }

    public AttendanceResponse update(Long id, AttendanceRequest req) {
        Attendance att = attendanceRepo.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", id));
        if (req.getCheckIn() != null) att.setCheckIn(req.getCheckIn());
        if (req.getCheckOut() != null) att.setCheckOut(req.getCheckOut());
        if (req.getStatus() != null) att.setStatus(req.getStatus());
        if (req.getNotes() != null) att.setNotes(req.getNotes());
        if (att.getCheckIn() != null && att.getCheckOut() != null) {
            long mins = Duration.between(att.getCheckIn(), att.getCheckOut()).toMinutes();
            BigDecimal hours = BigDecimal.valueOf(mins).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            att.setHoursWorked(hours);
            att.setOvertimeHours(hours.compareTo(BigDecimal.valueOf(9)) > 0
                ? hours.subtract(BigDecimal.valueOf(9)) : BigDecimal.ZERO);
        }
        return toResponse(attendanceRepo.save(att));
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> listByDate(LocalDate date) {
        return attendanceRepo.findByDate(date).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendanceResponse> listByStaff(Long staffId, LocalDate from, LocalDate to) {
        return attendanceRepo.findByStaffIdAndDateBetween(staffId, from, to)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    AttendanceResponse toResponse(Attendance a) {
        return AttendanceResponse.builder()
            .id(a.getId()).staffId(a.getStaff().getId()).staffName(a.getStaff().getName())
            .date(a.getDate()).checkIn(a.getCheckIn()).checkOut(a.getCheckOut())
            .hoursWorked(a.getHoursWorked()).overtimeHours(a.getOvertimeHours())
            .status(a.getStatus()).notes(a.getNotes())
            .build();
    }
}
