package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.*;
import com.growthhub.salon.entity.*;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor @Transactional
public class ClientServiceImpl {

    private final ClientRepository clientRepo;
    private final ClientLoyaltyAccountRepository loyaltyAccountRepo;

    public ClientResponse create(ClientRequest req) {
        if (clientRepo.existsByPhone(req.getPhone()))
            throw new DuplicateResourceException("Phone number already registered: " + req.getPhone());
        if (req.getEmail() != null && clientRepo.existsByEmail(req.getEmail()))
            throw new DuplicateResourceException("Email already registered: " + req.getEmail());

        Client client = Client.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .email(req.getEmail())
            .dateOfBirth(req.getDateOfBirth())
            .gender(req.getGender())
            .address(req.getAddress())
            .notes(req.getNotes())
            .tags(req.getTags())
            .avatar(initials(req.getName()))
            .joinDate(LocalDate.now())
            .isActive(true)
            .build();
        client = clientRepo.save(client);

        // Auto-create loyalty account
        loyaltyAccountRepo.save(ClientLoyaltyAccount.builder()
            .client(client)
            .pointsBalance(0).totalPointsEarned(0).totalPointsRedeemed(0)
            .tier("Basic").joinDate(LocalDate.now())
            .build());

        return toResponse(client);
    }

    @Transactional(readOnly = true)
    public PageResponse<ClientResponse> list(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));
        Page<Client> p;
        if (search != null && !search.isBlank()) {
            p = clientRepo.search(search,pageable);
            /*int start = (int) pageable.getOffset();
            int end   = Math.min(start + pageable.getPageSize(), found.size());
            List<Client> pageContent = start >= found.size() ? List.of() : found.subList(start, end);
            p = new PageImpl<>(pageContent, pageable, found.size());*/
        } else {
            p = clientRepo.findAll(pageable);
        }
        return PageResponse.<ClientResponse>builder()
            .content(p.getContent().stream().map(this::toResponse).collect(Collectors.toList()))
            .page(p.getNumber()).size(p.getSize())
            .totalElements(p.getTotalElements()).totalPages(p.getTotalPages()).last(p.isLast())
            .build();
    }

    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ClientResponse update(Long id, ClientRequest req) {
        Client client = findOrThrow(id);
        if (!client.getPhone().equals(req.getPhone()) && clientRepo.existsByPhone(req.getPhone()))
            throw new DuplicateResourceException("Phone already registered: " + req.getPhone());

        client.setName(req.getName());
        client.setPhone(req.getPhone());
        client.setEmail(req.getEmail());
        client.setDateOfBirth(req.getDateOfBirth());
        client.setGender(req.getGender());
        client.setAddress(req.getAddress());
        client.setNotes(req.getNotes());
        client.setTags(req.getTags());
        return toResponse(clientRepo.save(client));
    }

    public void delete(Long id) {
        Client client = findOrThrow(id);
        client.setIsActive(false);
        clientRepo.save(client);
    }

    private Client findOrThrow(Long id) {
        return clientRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private String initials(String name) {
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) if (!p.isEmpty()) sb.append(Character.toUpperCase(p.charAt(0)));
        return sb.length() > 2 ? sb.substring(0, 2) : sb.toString();
    }

    public ClientResponse toResponse(Client c) {
        return ClientResponse.builder()
            .id(c.getId()).name(c.getName()).phone(c.getPhone()).email(c.getEmail())
            .dateOfBirth(c.getDateOfBirth()).gender(c.getGender()).address(c.getAddress())
            .avatar(c.getAvatar()).totalVisits(c.getTotalVisits()).totalSpend(c.getTotalSpend())
            .lastVisit(c.getLastVisit()).membershipType(c.getMembershipType())
            .joinDate(c.getJoinDate()).notes(c.getNotes()).isActive(c.getIsActive())
            .tags(c.getTags()).createdAt(c.getCreatedAt())
            .build();
    }
}
