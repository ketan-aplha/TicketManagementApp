package com.Library.Management.service;

import com.Library.Management.dto.TicketRequest;
import com.Library.Management.dto.TicketResponse;
import com.Library.Management.entity.*;
import com.Library.Management.exception.InvalidStatusTransitionException;
import com.Library.Management.exception.ResourceNotFoundException;
import com.Library.Management.repository.CategoryRepository;
import com.Library.Management.repository.TicketRepository;
import com.Library.Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {
    private static final Logger log = LoggerFactory.getLogger(TicketService.class);
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public TicketResponse createTicket(TicketRequest request, MultipartFile file) {
        log.info("Creating ticket '{}' for creator {} in category {}",
                request.getTitle(), request.getCreatorId(), request.getCategoryId());
        User user = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> {
                    log.warn("User not found with id {}", request.getCreatorId());
                    return new ResourceNotFoundException("User not found with id: " + request.getCreatorId());
                });

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Category not found with id {}", request.getCategoryId());
                    return new ResourceNotFoundException("Category not found with id: " + request.getCategoryId());
                });

        String filePath = (file != null && !file.isEmpty()) ? fileStorageService.store(file) : null;

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .creator(user)
                .category(category)
                .attachmentPath(filePath)
                .build();
        TicketResponse response = mapToResponse(ticketRepository.save(ticket));
        log.info("Ticket with details {} created", response);
        return response;
    }


    public Page<TicketResponse> getAllTickets(TicketStatus status, Pageable pageable, UserRole requesterRole, Long requesterId) {

        // VISIBILITY LOGIC:
        // If the user is NOT an ADMIN, they can only see tickets they created.
        // If the user IS an ADMIN, they can see everything.
        log.info("Listing tickets for requester {} with role {} and status {}", requesterId, requesterRole, status);
        if (requesterRole != UserRole.ADMIN) {
            // This is a simplified version. In a real app, you'd add a custom
            // method in TicketRepository: findByCreatorIdAndStatus(...)
            List<Ticket> all = (status == null)
                    ? ticketRepository.findAll(pageable).getContent()
                    : ticketRepository.findByStatus(status, pageable).getContent();

            return new PageImpl<>(all.stream()
                    .filter(t -> t.getCreator().getId().equals(requesterId))
                    .map(this::mapToResponse)
                    .collect(Collectors.toList()))
                    .map(t -> t); // This is a simplified pagination for the example
        }

        // Admin sees all
        Page<Ticket> tickets = (status == null)
                ? ticketRepository.findAll(pageable)
                : ticketRepository.findByStatus(status, pageable);

        return tickets.map(this::mapToResponse);
    }

    public TicketResponse getTicketById(Long id) {
        log.info("Fetching ticket {}", id);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with id {}", id);
                    return new RuntimeException("Ticket not found");
                });
        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(Long id, TicketStatus newStatus) {
        log.info("Updating ticket {} status to {}", id, newStatus);
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ticket not found with id {}", id);
                    return new ResourceNotFoundException("Ticket not found with id: " + id);
                });

        // BUSINESS LOGIC: Lifecycle State Machine
        // Rule 1: Closed tickets cannot be changed.
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            log.warn("Cannot update ticket {} because it is already CLOSED", id);
            throw new InvalidStatusTransitionException("Cannot update a ticket that is already CLOSED.");
        }

        // Rule 2: A ticket cannot be RESOLVED unless it was IN_PROGRESS.
        if (newStatus == TicketStatus.RESOLVED && ticket.getStatus() != TicketStatus.IN_PROGRESS) {
            log.warn("Cannot resolve ticket {} from status {}", id, ticket.getStatus());
            throw new InvalidStatusTransitionException("Ticket must be 'IN_PROGRESS' before it can be 'RESOLVED'.");
        }

        ticket.setStatus(newStatus);
        TicketResponse response = mapToResponse(ticketRepository.save(ticket));
        log.info("Ticket {} status updated to {}", id, newStatus);
        return response;
    }

    // Helper method to convert Entity -> DTO
    private TicketResponse mapToResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .status(ticket.getStatus().name())
                .priority(ticket.getPriority().name())
                .categoryName(ticket.getCategory().getName())
                .creatorUsername(ticket.getCreator().getUsername())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}