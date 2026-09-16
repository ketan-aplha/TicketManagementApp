package com.Library.Management.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String status;
    private String priority;
    private String categoryName;
    private String creatorUsername;
    private LocalDateTime createdAt;
}
