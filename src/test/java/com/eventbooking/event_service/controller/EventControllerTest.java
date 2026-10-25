package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.EventStatus;
import com.eventbooking.event_service.dto.request.EventCreateRequest;
import com.eventbooking.event_service.dto.response.EventResponse;
import com.eventbooking.event_service.exception.GlobalExceptionHandler;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.mapper.EventMapper;
import com.eventbooking.event_service.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventController eventController;

    private UUID eventId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        eventId = UUID.randomUUID();
    }

    @Test
    void shouldCreateEventSuccessfully() throws Exception {
        UUID venueId = UUID.randomUUID();
        UUID organizerId = UUID.randomUUID();

        String requestJson = """
                {
                    "title": "Tech Summit",
                    "description": "Summit details",
                    "venueId": "%s",
                    "organizerId": "%s",
                    "startDateTime": "2026-11-10T10:00:00"
                }
                """.formatted(venueId, organizerId);

        Event event = Event.builder().id(eventId).title("Tech Summit").build();
        EventResponse response = EventResponse.builder().id(eventId).title("Tech Summit").status(EventStatus.DRAFT).build();

        when(eventService.createEvent(any(EventCreateRequest.class))).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.title").value("Tech Summit"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlankOnCreate() throws Exception {
        String requestJson = """
                {
                    "title": "",
                    "venueId": "%s",
                    "organizerId": "%s",
                    "startDateTime": "2026-11-10T10:00:00"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.title").exists());
    }

    @Test
    void shouldGetEventByIdSuccessfully() throws Exception {
        Event event = Event.builder().id(eventId).title("Tech Summit").build();
        EventResponse response = EventResponse.builder().id(eventId).title("Tech Summit").status(EventStatus.DRAFT).build();

        when(eventService.getEventById(eventId)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/{eventId}", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventId.toString()))
                .andExpect(jsonPath("$.title").value("Tech Summit"));
    }

    @Test
    void shouldReturnNotFoundWhenEventDoesNotExist() throws Exception {
        when(eventService.getEventById(eventId)).thenThrow(new ResourceNotFoundException("Event not found with ID: " + eventId));

        mockMvc.perform(get("/api/v1/events/{eventId}", eventId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Event not found with ID: " + eventId));
    }

    @Test
    void shouldPublishEventSuccessfully() throws Exception {
        Event event = Event.builder().id(eventId).title("Tech Summit").status(EventStatus.PUBLISHED).build();
        EventResponse response = EventResponse.builder().id(eventId).title("Tech Summit").status(EventStatus.PUBLISHED).build();

        when(eventService.publishEvent(eventId)).thenReturn(event);
        when(eventMapper.toResponse(event)).thenReturn(response);

        mockMvc.perform(post("/api/v1/events/{eventId}/publish", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void shouldReturnConflictWhenPublishingAlreadyPublishedEvent() throws Exception {
        when(eventService.publishEvent(eventId)).thenThrow(new IllegalStateException("Event is already published"));

        mockMvc.perform(post("/api/v1/events/{eventId}/publish", eventId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Event is already published"));
    }

    @Test
    void shouldDeleteEventSuccessfully() throws Exception {
        doNothing().when(eventService).deleteEvent(eventId);

        mockMvc.perform(delete("/api/v1/events/{eventId}", eventId))
                .andExpect(status().isNoContent());
    }
}
