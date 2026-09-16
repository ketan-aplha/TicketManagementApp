package com.Library.Management.dto;

import com.Library.Management.entity.TicketPriority;
import lombok.*;

@Data
public class TicketRequest {
    private String title;
    private String description;
    private TicketPriority priority;
    private Long creatorId;
    private Long categoryId;
}