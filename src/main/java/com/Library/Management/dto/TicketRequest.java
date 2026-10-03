package com.Library.Management.dto;

import com.Library.Management.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Map;

@Data
public class TicketRequest {
    @NotBlank(message = "Title is mandatory")
    private String title;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull private TicketPriority priority;
    @NotNull private Long creatorId;
    @NotNull private Long categoryId;

    // Optional metadata map
    private Map<String, Object> metadata;
}