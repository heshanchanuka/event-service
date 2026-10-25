package com.eventbooking.event_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueResponse {

    private UUID id;
    private String name;
    private String address;
    private String city;
    private String country;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
}
