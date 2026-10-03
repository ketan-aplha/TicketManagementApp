package com.Library.Management.controller;


import com.Library.Management.dto.TicketRequest;
import com.Library.Management.dto.TicketResponse;
import com.Library.Management.entity.TicketStatus;
import com.Library.Management.entity.UserRole;
import com.Library.Management.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    private static final Logger log = LoggerFactory.getLogger(TicketController.class);
    private final TicketService ticketService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TicketResponse> create(
            @RequestPart("ticket") @Valid TicketRequest request, // Added @Valid
            @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Received request to create ticket '{}' for creator {}", request.getTitle(), request.getCreatorId());
        TicketResponse response = ticketService.createTicket(request, file);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAll(
            @RequestParam(required = false) TicketStatus status,
            @PageableDefault(size = 10) Pageable pageable,
            @RequestHeader("X-User-Role") UserRole role, // Simulating security
            @RequestHeader("X-User-Id") Long userId) {
        log.info("Received request to list tickets for user {} with role {} and status {}", userId, role, status);
        return ResponseEntity.ok(ticketService.getAllTickets(status, pageable, role, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        log.info("Received request to get ticket {}", id);
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TicketResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam TicketStatus status) {
        log.info("Received request to update ticket {} status to {}", id, status);
        TicketResponse response = ticketService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
