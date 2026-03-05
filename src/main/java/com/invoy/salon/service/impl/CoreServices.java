//package com.growthhub.salon.service.impl;
//
//import com.growthhub.salon.dto.StaffResponse;
//import com.growthhub.salon.dto.request.*;
//import com.growthhub.salon.dto.response.*;
//import com.growthhub.salon.entity.*;
//import com.growthhub.salon.enums.*;
//import com.growthhub.salon.exception.*;
//import com.growthhub.salon.repository.*;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.*;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalTime;
//import java.util.*;
//
//// ═══════════════════════════════════════════════════════════
//// STAFF SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//class StaffService {
//
//    private final StaffRepository staffRepository;
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    public List<StaffResponse> listAll(StaffStatus status) {
//        List<Staff> list = (status != null)
//            ? staffRepository.findByStatus(status)
//            : staffRepository.findAll();
//        return list.stream().map(this::toResponse).toList();
//    }
//
//    public StaffResponse getById(UUID id) {
//        return toResponse(findOrThrow(id));
//    }
//
//    @Transactional
//    public StaffResponse create(CreateStaffRequest req) {
//        Staff staff = Staff.builder()
//            .fullName(req.getFullName()).phone(req.getPhone()).email(req.getEmail())
//            .gender(req.getGender() != null ? req.getGender() : GenderType.PREFER_NOT_TO_SAY)
//            .dateOfBirth(req.getDateOfBirth()).address(req.getAddress())
//            .role(req.getRole())
//            .joinDate(req.getJoinDate() != null ? req.getJoinDate() : java.time.LocalDate.now())
//            .baseSalary(req.getBaseSalary())
//            .commissionRate(req.getCommissionRate() != null ? req.getCommissionRate() : java.math.BigDecimal.ZERO)
//            .targetRevenue(req.getTargetRevenue() != null ? req.getTargetRevenue() : java.math.BigDecimal.ZERO)
//            .workingDays(req.getWorkingDays())
//            .shiftStart(req.getShiftStart() != null ? req.getShiftStart() : LocalTime.of(9, 0))
//            .shiftEnd(req.getShiftEnd() != null ? req.getShiftEnd() : LocalTime.of(19, 0))
//            .avatarInitials(initials(req.getFullName()))
//            .build();
//
//        staff = staffRepository.save(staff);
//
//        // Optionally create a portal User account
//        if (req.getPortalEmail() != null && req.getPortalPassword() != null) {
//            if (userRepository.existsByEmail(req.getPortalEmail())) {
//                throw new DuplicateResourceException("Portal account already exists: " + req.getPortalEmail());
//            }
//            User user = User.builder()
//                .email(req.getPortalEmail())
//                .passwordHash(passwordEncoder.encode(req.getPortalPassword()))
//                .role(UserRole.STYLIST)
//                .fullName(req.getFullName())
//                .phone(req.getPhone())
//                .build();
//            user = userRepository.save(user);
//            staff.setUser(user);
//            staff = staffRepository.save(staff);
//        }
//
//        // Add specializations
//        if (req.getSpecializations() != null) {
//            Staff finalStaff = staff;
//            req.getSpecializations().forEach(s ->
//                finalStaff.getSpecializations().add(
//                    StaffSpecialization.builder().staff(finalStaff).specialization(s).build()));
//            staffRepository.save(finalStaff);
//        }
//
//        return toResponse(staff);
//    }
//
//    @Transactional
//    public StaffResponse update(UUID id, UpdateStaffRequest req) {
//        Staff staff = findOrThrow(id);
//
//        if (req.getFullName()       != null) { staff.setFullName(req.getFullName()); staff.setAvatarInitials(initials(req.getFullName())); }
//        if (req.getPhone()          != null) staff.setPhone(req.getPhone());
//        if (req.getEmail()          != null) staff.setEmail(req.getEmail());
//        if (req.getGender()         != null) staff.setGender(req.getGender());
//        if (req.getDateOfBirth()    != null) staff.setDateOfBirth(req.getDateOfBirth());
//        if (req.getAddress()        != null) staff.setAddress(req.getAddress());
//        if (req.getRole()           != null) staff.setRole(req.getRole());
//        if (req.getBaseSalary()     != null) staff.setBaseSalary(req.getBaseSalary());
//        if (req.getCommissionRate() != null) staff.setCommissionRate(req.getCommissionRate());
//        if (req.getTargetRevenue()  != null) staff.setTargetRevenue(req.getTargetRevenue());
//        if (req.getWorkingDays()    != null) staff.setWorkingDays(req.getWorkingDays());
//        if (req.getShiftStart()     != null) staff.setShiftStart(req.getShiftStart());
//        if (req.getShiftEnd()       != null) staff.setShiftEnd(req.getShiftEnd());
//        if (req.getStatus()         != null) staff.setStatus(req.getStatus());
//
//        if (req.getSpecializations() != null) {
//            staff.getSpecializations().clear();
//            req.getSpecializations().forEach(s ->
//                staff.getSpecializations().add(
//                    StaffSpecialization.builder().staff(staff).specialization(s).build()));
//        }
//
//        return toResponse(staffRepository.save(staff));
//    }
//
//    @Transactional
//    public void deactivate(UUID id) {
//        Staff staff = findOrThrow(id);
//        staff.setStatus(String.valueOf(StaffStatus.INACTIVE));
//        staffRepository.save(staff);
//    }
//
//    private Staff findOrThrow(UUID id) {
//        return staffRepository.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Staff member", id));
//    }
//
//    private String initials(String name) {
//        if (name == null || name.isBlank()) return "?";
//        String[] p = name.trim().split("\\s+");
//        return p.length >= 2
//            ? ("" + p[0].charAt(0) + p[1].charAt(0)).toUpperCase()
//            : name.substring(0, Math.min(2, name.length())).toUpperCase();
//    }
//
//    StaffSummary toSummary(Staff s) {
//        return StaffSummary.builder().id(s.getId()).fullName(s.getFullName())
//            .role(s.getRole()).avatarInitials(s.getAvatarInitials()).status(s.getStatus()).build();
//    }
//
//    StaffResponse toResponse(Staff s) {
//        List<String> specs = s.getSpecializations() == null ? List.of()
//            : s.getSpecializations().stream().map(StaffSpecialization::getSpecialization).toList();
//
//        return StaffResponse.builder()
//            .id(s.getId()).fullName(s.getFullName()).phone(s.getPhone()).email(s.getEmail())
//            .gender(s.getGender()).dateOfBirth(s.getDateOfBirth()).address(s.getAddress())
//            .role(s.getRole()).joinDate(s.getJoinDate()).avatarInitials(s.getAvatarInitials())
//            .status(s.getStatus()).baseSalary(s.getBaseSalary()).commissionRate(s.getCommissionRate())
//            .targetRevenue(s.getTargetRevenue()).specializations(specs)
//            .workingDays(s.getWorkingDays()).shiftStart(s.getShiftStart()).shiftEnd(s.getShiftEnd())
//            .rating(s.getRating()).totalClients(s.getTotalClients()).createdAt(s.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// SERVICE CATALOG SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//class ServiceCatalogService {
//
//    private final ServiceCategoryRepository categoryRepo;
//    private final ServiceRepository serviceRepo;
//
//    // Categories
//    public List<ServiceCategoryResponse> listCategories() {
//        return categoryRepo.findByIsActiveTrueOrderBySortOrderAsc().stream()
//            .map(c -> ServiceCategoryResponse.builder()
//                .id(c.getId()).name(c.getName()).icon(c.getIcon()).color(c.getColor())
//                .sortOrder(c.getSortOrder()).isActive(c.getIsActive())
//                .serviceCount(c.getServices().stream().filter(s -> s.getStatus() == ServiceStatus.ACTIVE).count())
//                .build())
//            .toList();
//    }
//
//    @Transactional
//    public ServiceCategoryResponse createCategory(ServiceCategoryRequest req) {
//        ServiceCategory cat = ServiceCategory.builder()
//            .name(req.getName()).icon(req.getIcon()).color(req.getColor())
//            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
//            .isActive(req.getIsActive() != null ? req.getIsActive() : true)
//            .build();
//        cat = categoryRepo.save(cat);
//        return ServiceCategoryResponse.builder()
//            .id(cat.getId()).name(cat.getName()).icon(cat.getIcon()).color(cat.getColor())
//            .sortOrder(cat.getSortOrder()).isActive(cat.getIsActive()).serviceCount(0).build();
//    }
//
//    @Transactional
//    public ServiceCategoryResponse updateCategory(UUID id, ServiceCategoryRequest req) {
//        ServiceCategory cat = categoryRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", id));
//        if (req.getName()      != null) cat.setName(req.getName());
//        if (req.getIcon()      != null) cat.setIcon(req.getIcon());
//        if (req.getColor()     != null) cat.setColor(req.getColor());
//        if (req.getSortOrder() != null) cat.setSortOrder(req.getSortOrder());
//        if (req.getIsActive()  != null) cat.setIsActive(req.getIsActive());
//        cat = categoryRepo.save(cat);
//        return ServiceCategoryResponse.builder()
//            .id(cat.getId()).name(cat.getName()).icon(cat.getIcon()).color(cat.getColor())
//            .sortOrder(cat.getSortOrder()).isActive(cat.getIsActive()).build();
//    }
//
//    // Services
//    public PagedResponse<ServiceResponse> listServices(UUID categoryId, ServiceStatus status, String search, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder", "name"));
//        Page<Service> result;
//
//        if (search != null && !search.isBlank()) {
//            result = serviceRepo.search(search, pageable);
//        } else if (categoryId != null) {
//            result = (Page<Service>) (Page<?>) Page.empty(); // placeholder; real impl below
//            var list = categoryId != null && status != null
//                ? serviceRepo.findByCategoryIdAndStatus(categoryId, status)
//                : serviceRepo.findAll();
//            result = new PageImpl<>(list, pageable, list.size());
//        } else {
//            result = status != null
//                ? serviceRepo.findByStatus(status, pageable)
//                : serviceRepo.findAll(pageable);
//        }
//
//        return PagedResponse.<ServiceResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public ServiceResponse getService(UUID id) {
//        return toResponse(findServiceOrThrow(id));
//    }
//
//    @Transactional
//    public ServiceResponse createService(CreateServiceRequest req) {
//        ServiceCategory cat = categoryRepo.findById(req.getCategoryId())
//            .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", req.getCategoryId()));
//
//        Service svc = Service.builder()
//            .category(cat).name(req.getName()).description(req.getDescription())
//            .durationMins(req.getDurationMins()).price(req.getPrice()).mrp(req.getMrp())
//            .gstPercent(req.getGstPercent())
//            .status(req.getStatus() != null ? req.getStatus() : ServiceStatus.ACTIVE)
//            .isPopular(req.getIsPopular() != null ? req.getIsPopular() : false)
//            .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
//            .build();
//
//        return toResponse(serviceRepo.save(svc));
//    }
//
//    @Transactional
//    public ServiceResponse updateService(UUID id, UpdateServiceRequest req) {
//        Service svc = findServiceOrThrow(id);
//
//        if (req.getCategoryId()   != null) {
//            ServiceCategory cat = categoryRepo.findById(req.getCategoryId())
//                .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", req.getCategoryId()));
//            svc.setCategory(cat);
//        }
//        if (req.getName()         != null) svc.setName(req.getName());
//        if (req.getDescription()  != null) svc.setDescription(req.getDescription());
//        if (req.getDurationMins() != null) svc.setDurationMins(req.getDurationMins());
//        if (req.getPrice()        != null) svc.setPrice(req.getPrice());
//        if (req.getMrp()          != null) svc.setMrp(req.getMrp());
//        if (req.getGstPercent()   != null) svc.setGstPercent(req.getGstPercent());
//        if (req.getStatus()       != null) svc.setStatus(req.getStatus());
//        if (req.getIsPopular()    != null) svc.setIsPopular(req.getIsPopular());
//        if (req.getSortOrder()    != null) svc.setSortOrder(req.getSortOrder());
//
//        return toResponse(serviceRepo.save(svc));
//    }
//
//    @Transactional
//    public void deleteService(UUID id) {
//        Service svc = findServiceOrThrow(id);
//        svc.setStatus(ServiceStatus.ARCHIVED);
//        serviceRepo.save(svc);
//    }
//
//    private Service findServiceOrThrow(UUID id) {
//        return serviceRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Service", id));
//    }
//
//    ServiceSummary toSummary(Service s) {
//        return ServiceSummary.builder().id(s.getId()).name(s.getName())
//            .durationMins(s.getDurationMins()).price(s.getPrice()).build();
//    }
//
//    ServiceResponse toResponse(Service s) {
//        return ServiceResponse.builder()
//            .id(s.getId())
//            .categoryId(s.getCategory() != null ? s.getCategory().getId() : null)
//            .categoryName(s.getCategory() != null ? s.getCategory().getName() : null)
//            .name(s.getName()).description(s.getDescription()).durationMins(s.getDurationMins())
//            .price(s.getPrice()).mrp(s.getMrp()).gstPercent(s.getGstPercent())
//            .status(s.getStatus()).isPopular(s.getIsPopular()).sortOrder(s.getSortOrder())
//            .createdAt(s.getCreatedAt())
//            .build();
//    }
//}
//
//// ═══════════════════════════════════════════════════════════
//// APPOINTMENT SERVICE
//// ═══════════════════════════════════════════════════════════
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//class AppointmentService {
//
//    private final AppointmentRepository appointmentRepo;
//    private final ClientRepository clientRepo;
//    private final StaffRepository staffRepo;
//    private final ServiceRepository serviceRepo;
//    private final StaffService staffService;
//    private final ServiceCatalogService svcService;
//
//    public PagedResponse<AppointmentResponse> list(
//            java.time.LocalDate date, UUID clientId, UUID staffId,
//            AppointmentStatus status, int page, int size) {
//
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Appointment> result;
//
//        if (date != null) {
//            result = appointmentRepo.findByAppointmentDateOrderByStartTimeAsc(date, pageable);
//        } else if (clientId != null) {
//            result = appointmentRepo.findByClientId(clientId, pageable);
//        } else {
//            result = appointmentRepo.findAll(pageable);
//        }
//
//        return PagedResponse.<AppointmentResponse>builder()
//            .content(result.getContent().stream().map(this::toResponse).toList())
//            .page(result.getNumber()).size(result.getSize())
//            .totalElements(result.getTotalElements())
//            .totalPages(result.getTotalPages()).last(result.isLast())
//            .build();
//    }
//
//    public AppointmentResponse getById(UUID id) {
//        return toResponse(findOrThrow(id));
//    }
//
//    @Transactional
//    public AppointmentResponse create(CreateAppointmentRequest req) {
//        Client client = clientRepo.findById(req.getClientId())
//            .orElseThrow(() -> new ResourceNotFoundException("Client", req.getClientId()));
//        Staff staff = staffRepo.findById(req.getStaffId())
//            .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()));
//        Service service = serviceRepo.findById(req.getServiceId())
//            .orElseThrow(() -> new ResourceNotFoundException("Service", req.getServiceId()));
//
//        LocalTime endTime = req.getStartTime().plusMinutes(service.getDurationMins());
//
//        // Check schedule conflict
//        if (appointmentRepo.isStaffBusy(staff.getId(), req.getAppointmentDate(), req.getStartTime(), endTime)) {
//            throw new ScheduleConflictException(staff.getFullName(),
//                req.getAppointmentDate().toString(), req.getStartTime().toString());
//        }
//
//        Appointment appt = Appointment.builder()
//            .client(client).staff(staff).service(service)
//            .appointmentDate(req.getAppointmentDate())
//            .startTime(req.getStartTime()).endTime(endTime)
//            .durationMins(service.getDurationMins())
//            .amount(service.getPrice())
//            .status(AppointmentStatus.CONFIRMED)
//            .source(req.getSource() != null ? req.getSource() : BookingSource.WALK_IN)
//            .notes(req.getNotes())
//            .build();
//
//        return toResponse(appointmentRepo.save(appt));
//    }
//
//    @Transactional
//    public AppointmentResponse update(UUID id, UpdateAppointmentRequest req) {
//        Appointment appt = findOrThrow(id);
//
//        if (req.getStatus() == AppointmentStatus.CANCELLED) {
//            appt.setStatus(AppointmentStatus.CANCELLED);
//            appt.setCancelledAt(java.time.Instant.now());
//            appt.setCancelReason(req.getCancelReason());
//            return toResponse(appointmentRepo.save(appt));
//        }
//
//        if (req.getStaffId() != null) {
//            Staff staff = staffRepo.findById(req.getStaffId())
//                .orElseThrow(() -> new ResourceNotFoundException("Staff", req.getStaffId()));
//            appt.setStaff(staff);
//        }
//        if (req.getAppointmentDate() != null) appt.setAppointmentDate(req.getAppointmentDate());
//        if (req.getStartTime()       != null) {
//            appt.setStartTime(req.getStartTime());
//            appt.setEndTime(req.getStartTime().plusMinutes(appt.getDurationMins()));
//        }
//        if (req.getStatus() != null) appt.setStatus(req.getStatus());
//        if (req.getNotes()  != null) appt.setNotes(req.getNotes());
//
//        return toResponse(appointmentRepo.save(appt));
//    }
//
//    @Transactional
//    public AppointmentResponse cancel(UUID id, String reason) {
//        Appointment appt = findOrThrow(id);
//        if (appt.getStatus() == AppointmentStatus.COMPLETED) {
//            throw new BusinessException("Cannot cancel a completed appointment");
//        }
//        appt.setStatus(AppointmentStatus.CANCELLED);
//        appt.setCancelledAt(java.time.Instant.now());
//        appt.setCancelReason(reason);
//        return toResponse(appointmentRepo.save(appt));
//    }
//
//    private Appointment findOrThrow(UUID id) {
//        return appointmentRepo.findById(id)
//            .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
//    }
//
//    AppointmentResponse toResponse(Appointment a) {
//        return AppointmentResponse.builder()
//            .id(a.getId())
//            .client(ClientSummary.builder()
//                .id(a.getClient().getId()).name(a.getClient().getName())
//                .phone(a.getClient().getPhone()).avatarInitials(a.getClient().getAvatarInitials())
//                .membershipType(a.getClient().getMembershipType())
//                .loyaltyPoints(a.getClient().getLoyaltyPoints())
//                .walletBalance(a.getClient().getWalletBalance())
//                .lastVisitAt(a.getClient().getLastVisitAt())
//                .build())
//            .staff(staffService.toSummary(a.getStaff()))
//            .service(svcService.toSummary(a.getService()))
//            .appointmentDate(a.getAppointmentDate()).startTime(a.getStartTime()).endTime(a.getEndTime())
//            .durationMins(a.getDurationMins()).amount(a.getAmount())
//            .status(a.getStatus()).source(a.getSource()).notes(a.getNotes())
//            .reminderSent(a.getReminderSent()).cancelReason(a.getCancelReason())
//            .createdAt(a.getCreatedAt())
//            .build();
//    }
//}
