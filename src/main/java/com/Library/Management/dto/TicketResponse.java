package com.Library.Management.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Data @Builder
public class TicketResponse {
    private Long id;
    private String title;
    private String status;
    private String priority;
    private String categoryName;
    private String creatorUsername;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
}
