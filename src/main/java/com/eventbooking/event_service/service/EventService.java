package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.*;
import com.eventbooking.event_service.dto.request.EventCreateRequest;
import com.eventbooking.event_service.dto.request.EventUpdateRequest;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.mapper.EventMapper;
import com.eventbooking.event_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final OrganizerRepository organizerRepository;
    private final CategoryRepository categoryRepository;
    private final EventMapper eventMapper;

    @Transactional
    public Event createEvent(EventCreateRequest request) {
        log.info("Creating event with title: {}", request.getTitle());

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + request.getVenueId()));

        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseGet(() -> {
                    log.info("Organizer ID {} not found locally, creating reference record", request.getOrganizerId());
                    Organizer newOrganizer = Organizer.builder()
                            .id(request.getOrganizerId())
                            .displayName("Organizer")
                            .build();
                    return organizerRepository.save(newOrganizer);
                });

        Set<Category> categories = resolveCategories(request.getCategoryIds());

        Event event = eventMapper.toEntity(request, venue, organizer, categories);
        return eventRepository.save(event);
    }

    @Transactional(readOnly = true)
    public Event getEventById(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
    }

    @Transactional(readOnly = true)
    public Page<Event> searchEvents(
            String city,
            UUID categoryId,
            UUID organizerId,
            EventStatus status,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            String q,
            Pageable pageable) {

        Specification<Event> spec = EventSpecification.filterEvents(
                city, categoryId, organizerId, status, startDateFrom, startDateTo, q);

        return eventRepository.findAll(spec, pageable);
    }

    @Transactional
    public Event updateEventFull(UUID eventId, EventUpdateRequest request) {
        log.info("Full update (PUT) for event with ID: {}", eventId);
        request.validateForFullUpdate();

        Event event = getEventById(eventId);

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + request.getVenueId()));

        event.setTitle(request.getTitle().trim());
        event.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        event.setStartDateTime(request.getStartDateTime());
        event.setEndDateTime(request.getEndDateTime());
        event.setBannerImageUrl(request.getBannerImageUrl() != null ? request.getBannerImageUrl().trim() : null);
        event.setVenue(venue);

        if (request.getCategoryIds() != null) {
            Set<Category> categories = resolveCategories(request.getCategoryIds());
            event.getCategories().clear();
            categories.forEach(event::addCategory);
        }

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEventPartial(UUID eventId, EventUpdateRequest request) {
        log.info("Partial update (PATCH) for event with ID: {}", eventId);
        Event event = getEventById(eventId);

        if (request.isTitlePresent()) {
            if (request.getTitle() == null || request.getTitle().isBlank()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }
            event.setTitle(request.getTitle().trim());
        }

        if (request.isDescriptionPresent()) {
            event.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        }

        if (request.isStartDateTimePresent()) {
            if (request.getStartDateTime() == null) {
                throw new IllegalArgumentException("Start date time cannot be null");
            }
            event.setStartDateTime(request.getStartDateTime());
        }

        if (request.isEndDateTimePresent()) {
            event.setEndDateTime(request.getEndDateTime());
        }

        if (request.isBannerImageUrlPresent()) {
            event.setBannerImageUrl(request.getBannerImageUrl() != null ? request.getBannerImageUrl().trim() : null);
        }

        if (request.isVenueIdPresent()) {
            if (request.getVenueId() == null) {
                throw new IllegalArgumentException("Venue ID cannot be null");
            }
            Venue venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + request.getVenueId()));
            event.setVenue(venue);
        }

        if (request.isCategoryIdsPresent()) {
            event.getCategories().clear();
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                Set<Category> categories = resolveCategories(request.getCategoryIds());
                categories.forEach(event::addCategory);
            }
        }

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(UUID eventId) {
        log.info("Deleting event with ID: {}", eventId);
        Event event = getEventById(eventId);
        eventRepository.delete(event);
    }

    @Transactional
    public Event publishEvent(UUID eventId) {
        log.info("Publishing event with ID: {}", eventId);
        Event event = getEventById(eventId);

        if (event.getStatus() == EventStatus.PUBLISHED) {
            throw new IllegalStateException("Event is already published");
        }
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Cannot publish a cancelled event");
        }
        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Cannot publish a completed event");
        }

        event.setStatus(EventStatus.PUBLISHED);
        return eventRepository.save(event);
    }

    @Transactional
    public Event cancelEvent(UUID eventId) {
        log.info("Cancelling event with ID: {}", eventId);
        Event event = getEventById(eventId);

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new IllegalStateException("Event is already cancelled");
        }
        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed event");
        }

        event.setStatus(EventStatus.CANCELLED);
        return eventRepository.save(event);
    }

    private Set<Category> resolveCategories(List<UUID> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        if (categories.size() != categoryIds.size()) {
            throw new ResourceNotFoundException("One or more category IDs were not found");
        }
        return new HashSet<>(categories);
    }
}
