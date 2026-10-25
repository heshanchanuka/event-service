package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.Venue;
import com.eventbooking.event_service.dto.request.VenueRequest;
import com.eventbooking.event_service.dto.response.VenueResponse;
import com.eventbooking.event_service.exception.GlobalExceptionHandler;
import com.eventbooking.event_service.exception.ResourceConflictException;
import com.eventbooking.event_service.mapper.VenueMapper;
import com.eventbooking.event_service.service.VenueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VenueControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VenueService venueService;

    @Mock
    private VenueMapper venueMapper;

    @InjectMocks
    private VenueController venueController;

    private UUID venueId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(venueController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        venueId = UUID.randomUUID();
    }

    @Test
    void shouldCreateVenueSuccessfully() throws Exception {
        String requestJson = """
                {
                    "name": "Lotus Theatre",
                    "address": "100 Main St",
                    "city": "Colombo",
                    "country": "Sri Lanka",
                    "capacity": 1500,
                    "latitude": 6.9271,
                    "longitude": 79.8612
                }
                """;

        Venue venue = Venue.builder().id(venueId).name("Lotus Theatre").build();
        VenueResponse response = VenueResponse.builder().id(venueId).name("Lotus Theatre").build();

        when(venueService.createVenue(any(VenueRequest.class))).thenReturn(venue);
        when(venueMapper.toResponse(venue)).thenReturn(response);

        mockMvc.perform(post("/api/v1/venues")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(venueId.toString()))
                .andExpect(jsonPath("$.name").value("Lotus Theatre"));
    }

    @Test
    void shouldReturnAllVenues() throws Exception {
        Venue venue = Venue.builder().id(venueId).name("Lotus Theatre").build();
        VenueResponse response = VenueResponse.builder().id(venueId).name("Lotus Theatre").build();

        when(venueService.getAllVenues()).thenReturn(List.of(venue));
        when(venueMapper.toResponse(venue)).thenReturn(response);

        mockMvc.perform(get("/api/v1/venues"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Lotus Theatre"));
    }

    @Test
    void shouldDeleteVenueSuccessfully() throws Exception {
        doNothing().when(venueService).deleteVenue(venueId);

        mockMvc.perform(delete("/api/v1/venues/{id}", venueId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnConflictWhenDeletingVenueWithActiveEvents() throws Exception {
        doThrow(new ResourceConflictException("Cannot delete venue hosting active events"))
                .when(venueService).deleteVenue(venueId);

        mockMvc.perform(delete("/api/v1/venues/{id}", venueId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}
