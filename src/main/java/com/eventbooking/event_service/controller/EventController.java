package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.EventStatus;
import com.eventbooking.event_service.dto.request.EventCreateRequest;
import com.eventbooking.event_service.dto.request.EventUpdateRequest;
import com.eventbooking.event_service.dto.response.EventResponse;
import com.eventbooking.event_service.dto.response.EventSummaryResponse;
import com.eventbooking.event_service.dto.response.PageResponse;
import com.eventbooking.event_service.mapper.EventMapper;
import com.eventbooking.event_service.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody EventCreateRequest request) {
        Event createdEvent = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventMapper.toResponse(createdEvent));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventSummaryResponse>> searchEvents(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID organizerId,
            @RequestParam(required = false) EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTo,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "startDateTime,asc") String sort) {

        int validatedPage = Math.max(0, page);
        int validatedSize = (size <= 0) ? 10 : Math.min(size, 100);

        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(validatedPage, validatedSize, sortObj);

        Page<Event> eventPage = eventService.searchEvents(
                city, categoryId, organizerId, status, startDateFrom, startDateTo, q, pageable);

        PageResponse<EventSummaryResponse> response = PageResponse.from(
                eventPage, eventMapper::toSummaryResponse);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable UUID eventId) {
        Event event = eventService.getEventById(eventId);
        return ResponseEntity.ok(eventMapper.toResponse(event));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEventFull(
            @PathVariable UUID eventId,
            @Valid @RequestBody EventUpdateRequest request) {
        Event updatedEvent = eventService.updateEventFull(eventId, request);
        return ResponseEntity.ok(eventMapper.toResponse(updatedEvent));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEventPartial(
            @PathVariable UUID eventId,
            @RequestBody EventUpdateRequest request) {
        Event updatedEvent = eventService.updateEventPartial(eventId, request);
        return ResponseEntity.ok(eventMapper.toResponse(updatedEvent));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable UUID eventId) {
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{eventId}/publish")
    public ResponseEntity<EventResponse> publishEvent(@PathVariable UUID eventId) {
        Event publishedEvent = eventService.publishEvent(eventId);
        return ResponseEntity.ok(eventMapper.toResponse(publishedEvent));
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(@PathVariable UUID eventId) {
        Event cancelledEvent = eventService.cancelEvent(eventId);
        return ResponseEntity.ok(eventMapper.toResponse(cancelledEvent));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "startDateTime");
        }
        String[] parts = sort.split(",");
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(direction, property);
    }
}
