package com.eventbooking.event_service.mapper;

import com.eventbooking.event_service.domain.*;
import com.eventbooking.event_service.dto.request.EventCreateRequest;
import com.eventbooking.event_service.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EventMapper {

    private final VenueMapper venueMapper;
    private final CategoryMapper categoryMapper;
    private final EventImageMapper eventImageMapper;

    public Event toEntity(EventCreateRequest request, Venue venue, Organizer organizer, Set<Category> categories) {
        if (request == null) {
            return null;
        }

        Event event = Event.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .startDateTime(request.getStartDateTime())
                .endDateTime(request.getEndDateTime())
                .status(EventStatus.DRAFT)
                .bannerImageUrl(request.getBannerImageUrl() != null ? request.getBannerImageUrl().trim() : null)
                .venue(venue)
                .organizer(organizer)
                .build();

        if (categories != null && !categories.isEmpty()) {
            categories.forEach(event::addCategory);
        }

        return event;
    }

    public EventResponse toResponse(Event event) {
        if (event == null) {
            return null;
        }

        List<CategoryResponse> categoryResponses = event.getCategories() != null
                ? event.getCategories().stream()
                .map(categoryMapper::toResponse)
                .collect(Collectors.toList())
                : List.of();

        List<EventImageResponse> imageResponses = event.getImages() != null
                ? event.getImages().stream()
                .sorted(Comparator.comparing(EventImage::getDisplayOrder, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(eventImageMapper::toResponse)
                .collect(Collectors.toList())
                : List.of();

        OrganizerResponse organizerResponse = toOrganizerResponse(event.getOrganizer());

        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startDateTime(event.getStartDateTime())
                .endDateTime(event.getEndDateTime())
                .status(event.getStatus())
                .bannerImageUrl(event.getBannerImageUrl())
                .venue(venueMapper.toResponse(event.getVenue()))
                .organizer(organizerResponse)
                .categories(categoryResponses)
                .images(imageResponses)
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public EventSummaryResponse toSummaryResponse(Event event) {
        if (event == null) {
            return null;
        }

        String primaryImageUrl = resolvePrimaryImageUrl(event);
        String venueName = event.getVenue() != null ? event.getVenue().getName() : null;

        return EventSummaryResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .startDateTime(event.getStartDateTime())
                .venueName(venueName)
                .status(event.getStatus())
                .primaryImageUrl(primaryImageUrl)
                .build();
    }

    public OrganizerResponse toOrganizerResponse(Organizer organizer) {
        if (organizer == null) {
            return null;
        }
        return OrganizerResponse.builder()
                .id(organizer.getId())
                .displayName(organizer.getDisplayName())
                .contactEmail(organizer.getContactEmail())
                .build();
    }

    private String resolvePrimaryImageUrl(Event event) {
        if (event.getImages() != null && !event.getImages().isEmpty()) {
            // Find explicit primary image
            for (EventImage image : event.getImages()) {
                if (Boolean.TRUE.equals(image.getIsPrimary())) {
                    return image.getImageUrl();
                }
            }
        }
        if (event.getBannerImageUrl() != null && !event.getBannerImageUrl().isBlank()) {
            return event.getBannerImageUrl();
        }
        if (event.getImages() != null && !event.getImages().isEmpty()) {
            return event.getImages().get(0).getImageUrl();
        }
        return null;
    }
}
