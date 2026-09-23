package com.Library.Management.dto;

import com.Library.Management.entity.TicketPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
public class TicketRequest {
    @NotBlank(message = "Title is mandatory")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    private String title;

    @NotBlank(message = "Description is mandatory")
    @Size(min = 10, message = "Description must be at least 10 characters long")
    private String description;

    @NotNull(message = "Priority is mandatory")
    private TicketPriority priority;

    @NotNull(message = "Creator ID is mandatory")
    private Long creatorId;

    @NotNull(message = "Category ID is mandatory")
    private Long categoryId;
}