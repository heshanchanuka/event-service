package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.EventImage;
import com.eventbooking.event_service.dto.request.EventImageRequest;
import com.eventbooking.event_service.dto.response.EventImageResponse;
import com.eventbooking.event_service.mapper.EventImageMapper;
import com.eventbooking.event_service.service.EventImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events/{eventId}/images")
@RequiredArgsConstructor
public class EventImageController {

    private final EventImageService eventImageService;
    private final EventImageMapper eventImageMapper;

    @GetMapping
    public ResponseEntity<List<EventImageResponse>> getImagesForEvent(@PathVariable UUID eventId) {
        List<EventImageResponse> images = eventImageService.getImagesForEvent(eventId).stream()
                .map(eventImageMapper::toResponse)
                .toList();
        return ResponseEntity.ok(images);
    }

    @PostMapping
    public ResponseEntity<EventImageResponse> addImageToEvent(
            @PathVariable UUID eventId,
            @Valid @RequestBody EventImageRequest request) {
        EventImage image = eventImageService.addImageToEvent(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventImageMapper.toResponse(image));
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImageFromEvent(
            @PathVariable UUID eventId,
            @PathVariable UUID imageId) {
        eventImageService.deleteImageFromEvent(eventId, imageId);
        return ResponseEntity.noContent().build();
    }
}
