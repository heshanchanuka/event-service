package com.eventbooking.event_service.controller;





import com.eventbooking.event_service.dto.EventResponse;
import com.eventbooking.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    // 1. සිස්ටම් එකේ තියෙන ඔක්කොම Events ටික ගන්න
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEvents()
                .stream()
                .map(EventResponse::from) // Entity එක DTO එකකට හරවනවා
                .toList();
        return ResponseEntity.ok(events);
    }

    // 2. අදාළ ID එක දීලා එක Event එකක් විතරක් ගන්න
    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long eventId) {
        EventResponse event = EventResponse.from(eventService.getEventById(eventId));
        return ResponseEntity.ok(event);
    }
}