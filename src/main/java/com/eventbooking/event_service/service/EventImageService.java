package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.EventImage;
import com.eventbooking.event_service.dto.request.EventImageRequest;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.mapper.EventImageMapper;
import com.eventbooking.event_service.repository.EventImageRepository;
import com.eventbooking.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventImageService {

    private final EventImageRepository eventImageRepository;
    private final EventRepository eventRepository;
    private final EventImageMapper eventImageMapper;

    @Transactional(readOnly = true)
    public List<EventImage> getImagesForEvent(UUID eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResourceNotFoundException("Event not found with ID: " + eventId);
        }
        return eventImageRepository.findByEventIdOrderByDisplayOrderAsc(eventId);
    }

    @Transactional
    public EventImage addImageToEvent(UUID eventId, EventImageRequest request) {
        log.info("Adding image to event with ID: {}", eventId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

        if (Boolean.TRUE.equals(request.getIsPrimary())) {
            // Reset existing primary image for this event
            List<EventImage> existingImages = eventImageRepository.findByEventIdOrderByDisplayOrderAsc(eventId);
            for (EventImage img : existingImages) {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    img.setIsPrimary(false);
                    eventImageRepository.save(img);
                }
            }
        }

        EventImage image = eventImageMapper.toEntity(request, event);
        return eventImageRepository.save(image);
    }

    @Transactional
    public void deleteImageFromEvent(UUID eventId, UUID imageId) {
        log.info("Deleting image with ID: {} from event ID: {}", imageId, eventId);
        EventImage image = eventImageRepository.findByIdAndEventId(imageId, eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Image with ID " + imageId + " not found for event " + eventId));

        eventImageRepository.delete(image);
    }
}
