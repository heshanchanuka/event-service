package com.eventbooking.event_service.mapper;

import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.EventImage;
import com.eventbooking.event_service.dto.request.EventImageRequest;
import com.eventbooking.event_service.dto.response.EventImageResponse;
import org.springframework.stereotype.Component;

@Component
public class EventImageMapper {

    public EventImage toEntity(EventImageRequest request, Event event) {
        if (request == null) {
            return null;
        }
        return EventImage.builder()
                .event(event)
                .imageUrl(request.getImageUrl().trim())
                .isPrimary(Boolean.TRUE.equals(request.getIsPrimary()))
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
    }

    public EventImageResponse toResponse(EventImage image) {
        if (image == null) {
            return null;
        }
        return EventImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
}
