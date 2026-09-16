package com.Library.Management.repository;

import com.Library.Management.entity.Ticket;
import com.Library.Management.entity.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);
}
