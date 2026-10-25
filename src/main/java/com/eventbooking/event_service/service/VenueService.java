package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.Venue;
import com.eventbooking.event_service.dto.request.VenueRequest;
import com.eventbooking.event_service.exception.ResourceConflictException;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.mapper.VenueMapper;
import com.eventbooking.event_service.repository.EventRepository;
import com.eventbooking.event_service.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;
    private final EventRepository eventRepository;
    private final VenueMapper venueMapper;

    @Transactional
    public Venue createVenue(VenueRequest request) {
        log.info("Creating new venue: {}", request.getName());
        Venue venue = venueMapper.toEntity(request);
        return venueRepository.save(venue);
    }

    @Transactional(readOnly = true)
    public List<Venue> getAllVenues() {
        return venueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Venue getVenueById(UUID id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with ID: " + id));
    }

    @Transactional
    public Venue updateVenue(UUID id, VenueRequest request) {
        log.info("Updating venue with ID: {}", id);
        Venue venue = getVenueById(id);
        venueMapper.updateEntity(venue, request);
        return venueRepository.save(venue);
    }

    @Transactional
    public void deleteVenue(UUID id) {
        log.info("Deleting venue with ID: {}", id);
        Venue venue = getVenueById(id);
        if (eventRepository.existsByVenueId(id)) {
            throw new ResourceConflictException("Cannot delete venue hosting active events. ID: " + id);
        }
        venueRepository.delete(venue);
    }
}
