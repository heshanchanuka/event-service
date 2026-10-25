package com.eventbooking.event_service.mapper;

import com.eventbooking.event_service.domain.Venue;
import com.eventbooking.event_service.dto.request.VenueRequest;
import com.eventbooking.event_service.dto.response.VenueResponse;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public Venue toEntity(VenueRequest request) {
        if (request == null) {
            return null;
        }
        return Venue.builder()
                .name(request.getName().trim())
                .address(request.getAddress().trim())
                .city(request.getCity().trim())
                .country(request.getCountry().trim())
                .capacity(request.getCapacity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }

    public void updateEntity(Venue venue, VenueRequest request) {
        if (venue == null || request == null) {
            return;
        }
        venue.setName(request.getName().trim());
        venue.setAddress(request.getAddress().trim());
        venue.setCity(request.getCity().trim());
        venue.setCountry(request.getCountry().trim());
        venue.setCapacity(request.getCapacity());
        venue.setLatitude(request.getLatitude());
        venue.setLongitude(request.getLongitude());
    }

    public VenueResponse toResponse(Venue venue) {
        if (venue == null) {
            return null;
        }
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .city(venue.getCity())
                .country(venue.getCountry())
                .capacity(venue.getCapacity())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .build();
    }
}
