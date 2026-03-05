package com.growthhub.salon.controller;
import com.growthhub.salon.dto.*;
import com.growthhub.salon.service.impl.ClientServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/clients") @RequiredArgsConstructor @Tag(name = "Clients")
public class ClientController {
    private final ClientServiceImpl clientService;
    @PostMapping
    public ResponseEntity<ApiResponse<ClientResponse>> create(@Valid @RequestBody ClientRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok("Client created", clientService.create(req)));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ClientResponse>>> list(
        @RequestParam(required=false) String search, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="20") int size) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.list(search, page, size)));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(clientService.getById(id)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ClientResponse>> update(@PathVariable Long id, @Valid @RequestBody ClientRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Client updated", clientService.update(id, req)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        clientService.delete(id); return ResponseEntity.ok(ApiResponse.ok("Client deactivated"));
    }
}
