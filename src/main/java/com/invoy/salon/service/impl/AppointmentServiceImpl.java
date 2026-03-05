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
public class AppointmentServiceImpl {

    private final AppointmentRepository apptRepo;
    private final ClientRepository clientRepo;
    private final SalonServiceRepository serviceRepo;
    private final StaffRepository staffRepo;

    public AppointmentResponse create(AppointmentRequest req) {
        Client client = clientRepo.findById(req.getClientId())
            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
        SalonService svc = serviceRepo.findById(req.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service", req.getServiceId()));
        Staff staff = staffRepo.findById(req.getStaffId())
            .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()));

        Appointment appt = Appointment.builder()
            .client(client).service(svc).staff(staff)
            .date(req.getDate()).time(req.getTime())
            .duration(req.getDuration() != null ? req.getDuration() : svc.getDuration())
            .amount(svc.getPrice())
            .status(req.getStatus() != null ? req.getStatus() : "upcoming")
            .notes(req.getNotes())
            .source(req.getSource() != null ? req.getSource() : "walkin")
            .build();
        return toResponse(apptRepo.save(appt));
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByDate(LocalDate date) {
        return apptRepo.findByDateBetweenOrderByDateAscTimeAsc(date, date)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByDateRange(LocalDate from, LocalDate to) {
        return apptRepo.findByDateBetweenOrderByDateAscTimeAsc(from, to)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByClient(Long clientId) {
        return apptRepo.findByClientIdOrderByDateDesc(clientId)
            .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public AppointmentResponse update(Long id, AppointmentRequest req) {
        Appointment appt = findOrThrow(id);
        if (req.getClientId() != null)
            appt.setClient(clientRepo.findById(req.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId())));
        if (req.getServiceId() != null) {
            SalonService svc = serviceRepo.findById(req.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", req.getServiceId()));
            appt.setService(svc);
            appt.setAmount(svc.getPrice());
        }
        if (req.getStaffId() != null)
            appt.setStaff(staffRepo.findById(req.getStaffId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId())));
        if (req.getDate() != null) appt.setDate(req.getDate());
        if (req.getTime() != null) appt.setTime(req.getTime());
        if (req.getDuration() != null) appt.setDuration(req.getDuration());
        if (req.getStatus() != null) appt.setStatus(req.getStatus());
        if (req.getNotes() != null) appt.setNotes(req.getNotes());
        return toResponse(apptRepo.save(appt));
    }

    public AppointmentResponse updateStatus(Long id, String status) {
        Appointment appt = findOrThrow(id);
        appt.setStatus(status);
        return toResponse(apptRepo.save(appt));
    }

    public void cancel(Long id) {
        Appointment appt = findOrThrow(id);
        appt.setStatus("cancelled");
        apptRepo.save(appt);
    }

    private Appointment findOrThrow(Long id) {
        return apptRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
    }

    public AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
            .id(a.getId())
            .clientId(a.getClient().getId()).clientName(a.getClient().getName())
            .clientPhone(a.getClient().getPhone())
            .serviceId(a.getService().getId()).serviceName(a.getService().getName())
            .staffId(a.getStaff().getId()).staffName(a.getStaff().getName())
            .date(a.getDate()).time(a.getTime()).duration(a.getDuration())
            .amount(a.getAmount()).status(a.getStatus())
            .notes(a.getNotes()).source(a.getSource())
            .build();
    }
}
