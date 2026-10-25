package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.Venue;
import com.eventbooking.event_service.dto.request.VenueRequest;
import com.eventbooking.event_service.dto.response.VenueResponse;
import com.eventbooking.event_service.mapper.VenueMapper;
import com.eventbooking.event_service.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;
    private final VenueMapper venueMapper;

    @PostMapping
    public ResponseEntity<VenueResponse> createVenue(@Valid @RequestBody VenueRequest request) {
        Venue createdVenue = venueService.createVenue(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(venueMapper.toResponse(createdVenue));
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAllVenues() {
        List<VenueResponse> venues = venueService.getAllVenues().stream()
                .map(venueMapper::toResponse)
                .toList();
        return ResponseEntity.ok(venues);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VenueResponse> getVenueById(@PathVariable UUID id) {
        Venue venue = venueService.getVenueById(id);
        return ResponseEntity.ok(venueMapper.toResponse(venue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> updateVenue(
            @PathVariable UUID id,
            @Valid @RequestBody VenueRequest request) {
        Venue updatedVenue = venueService.updateVenue(id, request);
        return ResponseEntity.ok(venueMapper.toResponse(updatedVenue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVenue(@PathVariable UUID id) {
        venueService.deleteVenue(id);
        return ResponseEntity.noContent().build();
    }
}
