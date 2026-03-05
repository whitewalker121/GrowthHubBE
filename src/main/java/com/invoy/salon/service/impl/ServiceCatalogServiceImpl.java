package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class ServiceCatalogServiceImpl {

    private final SalonServiceRepository serviceRepo;
    private final ServiceCategoryRepository categoryRepo;

    public ServiceResponse createService(ServiceRequest req) {
        ServiceCategory cat = categoryRepo.findById(req.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", req.getCategoryId()));

        SalonService s = SalonService.builder()
            .category(cat).name(req.getName()).description(req.getDescription())
            .duration(req.getDuration()).price(req.getPrice()).mrp(req.getMrp())
            .gstRate(req.getGstRate()).status(req.getStatus() != null ? req.getStatus() : "active")
            .popular(req.getPopular() != null ? req.getPopular() : false)
            .build();
        return toResponse(serviceRepo.save(s));
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> listServices(String status) {
        List<SalonService> list = status != null
            ? serviceRepo.findByStatus(status)
            : serviceRepo.findAll();
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceResponse getService(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ServiceResponse updateService(Long id, ServiceRequest req) {
        SalonService s = findOrThrow(id);
        ServiceCategory cat = categoryRepo.findById(req.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("ServiceCategory", req.getCategoryId()));
        s.setCategory(cat); s.setName(req.getName()); s.setDescription(req.getDescription());
        s.setDuration(req.getDuration()); s.setPrice(req.getPrice()); s.setMrp(req.getMrp());
        s.setGstRate(req.getGstRate());
        if (req.getStatus() != null) s.setStatus(req.getStatus());
        if (req.getPopular() != null) s.setPopular(req.getPopular());
        return toResponse(serviceRepo.save(s));
    }

    public void deleteService(Long id) {
        SalonService s = findOrThrow(id);
        s.setStatus("inactive");
        serviceRepo.save(s);
    }

    public List<ServiceCategory> listCategories() {
        return categoryRepo.findByIsActiveTrue();
    }

    public ServiceCategory createCategory(String name, String icon, String color) {
        return categoryRepo.save(ServiceCategory.builder()
            .name(name).icon(icon).color(color).isActive(true).build());
    }

    private SalonService findOrThrow(Long id) {
        return serviceRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Service", id));
    }

    public ServiceResponse toResponse(SalonService s) {
        return ServiceResponse.builder()
            .id(s.getId()).categoryId(s.getCategory().getId())
            .categoryName(s.getCategory().getName())
            .name(s.getName()).description(s.getDescription()).duration(s.getDuration())
            .price(s.getPrice()).mrp(s.getMrp()).gstRate(s.getGstRate())
            .status(s.getStatus()).popular(s.getPopular())
            .build();
    }
}
