package com.eventbooking.event_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCreateRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    private String description;

    @NotNull(message = "Venue ID is required")
    private UUID venueId;

    @NotNull(message = "Organizer ID is required")
    private UUID organizerId;

    @NotNull(message = "Start date time is required")
    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String bannerImageUrl;

    @Builder.Default
    private List<UUID> categoryIds = new ArrayList<>();
}
