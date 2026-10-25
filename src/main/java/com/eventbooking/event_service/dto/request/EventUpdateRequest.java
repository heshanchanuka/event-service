package com.eventbooking.event_service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventUpdateRequest {

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;
    @JsonIgnore
    private boolean titlePresent;

    @Size(max = 4000, message = "Description cannot exceed 4000 characters")
    private String description;
    @JsonIgnore
    private boolean descriptionPresent;

    private LocalDateTime startDateTime;
    @JsonIgnore
    private boolean startDateTimePresent;

    private LocalDateTime endDateTime;
    @JsonIgnore
    private boolean endDateTimePresent;

    private String bannerImageUrl;
    @JsonIgnore
    private boolean bannerImageUrlPresent;

    private UUID venueId;
    @JsonIgnore
    private boolean venueIdPresent;

    private List<UUID> categoryIds;
    @JsonIgnore
    private boolean categoryIdsPresent;

    public void setTitle(String title) {
        this.title = title;
        this.titlePresent = true;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionPresent = true;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
        this.startDateTimePresent = true;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
        this.endDateTimePresent = true;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
        this.bannerImageUrlPresent = true;
    }

    public void setVenueId(UUID venueId) {
        this.venueId = venueId;
        this.venueIdPresent = true;
    }

    public void setCategoryIds(List<UUID> categoryIds) {
        this.categoryIds = categoryIds;
        this.categoryIdsPresent = true;
    }

    /**
     * Validates that all required fields for a full PUT update are supplied and valid.
     */
    public void validateForFullUpdate() {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required for full update");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date time is required for full update");
        }
        if (venueId == null) {
            throw new IllegalArgumentException("Venue ID is required for full update");
        }
    }
}
