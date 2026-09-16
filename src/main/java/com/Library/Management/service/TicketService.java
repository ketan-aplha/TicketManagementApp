package com.Library.Management.service;

import com.Library.Management.dto.TicketRequest;
import com.Library.Management.dto.TicketResponse;
import com.Library.Management.entity.*;
import com.Library.Management.repository.CategoryRepository;
import com.Library.Management.repository.TicketRepository;
import com.Library.Management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public TicketResponse createTicket(TicketRequest request, MultipartFile file) {
        User user = userRepository.findById(request.getCreatorId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Handle optional file upload
        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = fileStorageService.store(file);
        }

        Ticket ticket = Ticket.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(TicketStatus.OPEN)
                .creator(user)
                .category(category)
                .attachmentPath(filePath) // Set the path
                .build();
        return mapToResponse(ticketRepository.save(ticket));

    }


    public Page<TicketResponse> getAllTickets(TicketStatus status, Pageable pageable, UserRole requesterRole, Long requesterId) {

        // VISIBILITY LOGIC:
        // If the user is NOT an ADMIN, they can only see tickets they created.
        // If the user IS an ADMIN, they can see everything.
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
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return mapToResponse(ticket);
    }

    @Transactional
    public TicketResponse updateStatus(Long id, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // Task 1.3: Lifecycle Logic
        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new RuntimeException("Cannot update status of a closed ticket");
        }

        ticket.setStatus(newStatus);
        return mapToResponse(ticketRepository.save(ticket));
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