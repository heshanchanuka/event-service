package com.eventbooking.event_service.dto.response;

import com.eventbooking.event_service.domain.EventStatus;
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
public class EventResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private EventStatus status;
    private String bannerImageUrl;
    private VenueResponse venue;
    private OrganizerResponse organizer;

    @Builder.Default
    private List<CategoryResponse> categories = new ArrayList<>();

    @Builder.Default
    private List<EventImageResponse> images = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
