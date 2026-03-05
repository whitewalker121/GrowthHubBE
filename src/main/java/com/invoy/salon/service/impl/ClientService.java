package com.growthhub.salon.service.impl;

import com.growthhub.salon.dto.ClientResponse;
import com.growthhub.salon.dto.request.*;
import com.growthhub.salon.dto.response.*;
import com.growthhub.salon.entity.Client;
import com.growthhub.salon.entity.LoyaltyTransaction;
import com.growthhub.salon.enums.ClientTier;
import com.growthhub.salon.exception.*;
import com.growthhub.salon.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClientService {

    private final ClientRepository clientRepository;
    private final LoyaltyTransactionRepository loyaltyTxRepo;

    // ── LIST / SEARCH ──────────────────────────────────────

    public PagedResponse<ClientResponse> list(String search, int page, int size, String sortBy, String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Client> result = (search != null && !search.isBlank())
            ? clientRepository.search(search,pageable)
            : clientRepository.findAll(pageable);

        return toPagedResponse(result);
    }

    // ── GET BY ID ──────────────────────────────────────────

    public ClientResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public ClientResponse getByPhone(String phone) {
        Client client = clientRepository.findByPhone(phone)
            .orElseThrow(() -> new ResourceNotFoundException("Client not found with phone: " + phone));
        return toResponse(client);
    }

    // ── CREATE ─────────────────────────────────────────────

    @Transactional
    public ClientResponse create(CreateClientRequest req) {
        if (clientRepository.existsByPhone(req.getPhone())) {
            throw new DuplicateResourceException("Client already exists with phone: " + req.getPhone());
        }

        Client client = Client.builder()
            .name(req.getName())
            .phone(req.getPhone())
            .email(req.getEmail())
            .dateOfBirth(req.getDateOfBirth())
            .gender(String.valueOf(req.getGender()))
            .address(req.getAddress())
            .notes(req.getNotes())
            .tags(req.getTags() != null ? List.of(req.getTags().toArray(String[]::new)) : null)
            .avatar(initials(req.getName()))
           // .referralCode(generateReferralCode())
            .build();

        // Handle referral
        if (req.getReferredByPhone() != null && !req.getReferredByPhone().isBlank()) {
            clientRepository.findByPhone(req.getReferredByPhone());
                //.ifPresent(client::setReferredBy);
        }

        return toResponse(clientRepository.save(client));
    }

    // ── UPDATE ─────────────────────────────────────────────

    @Transactional
    public ClientResponse update(Long id, UpdateClientRequest req) {
        Client client = findOrThrow(id);

        if (req.getName()        != null) { client.setName(req.getName()); client.setAvatar(initials(req.getName())); }
        if (req.getEmail()       != null) client.setEmail(req.getEmail());
        if (req.getDateOfBirth() != null) client.setDateOfBirth(req.getDateOfBirth());
        if (req.getGender()      != null) client.setGender(String.valueOf(req.getGender()));
        if (req.getAddress()     != null) client.setAddress(req.getAddress());
        if (req.getNotes()       != null) client.setNotes(req.getNotes());
        if (req.getTags()        != null) client.setTags(List.of(req.getTags().toArray(String[]::new)));
        if (req.getIsActive()    != null) client.setIsActive(req.getIsActive());

        return toResponse(clientRepository.save(client));
    }

    // ── DELETE (soft) ──────────────────────────────────────

    @Transactional
    public void delete(Long id) {
        Client client = findOrThrow(id);
        client.setIsActive(false);
        clientRepository.save(client);
    }

    // ── LOYALTY OVERVIEW ──────────────────────────────────

    public ClientLoyaltyResponse getLoyaltySummary(Long id) {
        Client client = findOrThrow(id);
        List<LoyaltyTransactionResponse> txns = loyaltyTxRepo.findByClientId(id)
            .stream().limit(10)
            .map(t -> LoyaltyTransactionResponse.builder()
                .id(t.getId())
                .transactionType(t.getTransactionType())
                .points(t.getPoints())
               // .balanceAfter(t.getBalanceAfter())
               // .notes(t.getNotes())
              //  .createdAt(t.getCreatedAt())
                .build())
            .toList();

        return ClientLoyaltyResponse.builder()
            .clientId(client.getId())
            .clientName(client.getName())
            .tier(ClientTier.valueOf(client.getMembershipType()))
        //    .loyaltyPoints(client.getLoyaltyPoints())
          //  .redeemableValue(java.math.BigDecimal.valueOf(client.getLoyaltyPoints()).multiply(java.math.BigDecimal.valueOf(0.5)))
            .totalSpend(client.getTotalSpend())
            .recentTransactions(txns)
            .build();
    }

    // ── HELPERS ────────────────────────────────────────────

    private Client findOrThrow(Long id) {
        return clientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    private String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] parts = name.trim().split("\\s+");
        return parts.length >= 2
            ? ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase()
            : name.substring(0, Math.min(2, name.length())).toUpperCase();
    }

    private String generateReferralCode() {
        return "REF-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private ClientResponse toResponse(Client c) {
        return ClientResponse.builder()
            .id(c.getId()).name(c.getName()).phone(c.getPhone()).email(c.getEmail())
            .dateOfBirth(c.getDateOfBirth()).gender(c.getGender()).address(c.getAddress())
            .avatar(c.getAvatar()).membershipType(c.getMembershipType())
           // .loyaltyPoints(c.getLoyaltyPoints()).walletBalance(c.getWalletBalance())
            .totalVisits(c.getTotalVisits()).totalSpend(c.getTotalSpend())
            //.lastVisitAt(c.getLastVisitAt()).notes(c.getNotes()).tags(c.getTags())
            //.referralCode(c.getReferralCode()).isActive(c.getIsActive())
            .joinDate(c.getJoinDate()).createdAt(c.getCreatedAt())
            .build();
    }

    private PagedResponse<ClientResponse> toPagedResponse(Page<Client> page) {
        return PagedResponse.<ClientResponse>builder()
            .content(page.getContent().stream().map(this::toResponse).toList())
            .page(page.getNumber()).size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages()).last(page.isLast())
            .build();
    }
}
