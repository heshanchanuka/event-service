package com.eventbooking.event_service.controller;

import com.eventbooking.event_service.domain.EventImage;
import com.eventbooking.event_service.dto.request.EventImageRequest;
import com.eventbooking.event_service.dto.response.EventImageResponse;
import com.eventbooking.event_service.exception.GlobalExceptionHandler;
import com.eventbooking.event_service.mapper.EventImageMapper;
import com.eventbooking.event_service.service.EventImageService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventImageControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EventImageService eventImageService;

    @Mock
    private EventImageMapper eventImageMapper;

    @InjectMocks
    private EventImageController eventImageController;

    private UUID eventId;
    private UUID imageId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventImageController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        eventId = UUID.randomUUID();
        imageId = UUID.randomUUID();
    }

    @Test
    void shouldGetImagesForEvent() throws Exception {
        EventImage image = EventImage.builder()
                .id(imageId)
                .imageUrl("https://example.com/photo.jpg")
                .isPrimary(true)
                .displayOrder(1)
                .build();
        EventImageResponse response = EventImageResponse.builder()
                .id(imageId)
                .imageUrl("https://example.com/photo.jpg")
                .isPrimary(true)
                .displayOrder(1)
                .build();

        when(eventImageService.getImagesForEvent(eventId)).thenReturn(List.of(image));
        when(eventImageMapper.toResponse(image)).thenReturn(response);

        mockMvc.perform(get("/api/v1/events/{eventId}/images", eventId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].imageUrl").value("https://example.com/photo.jpg"));
    }

    @Test
    void shouldAddImageToEvent() throws Exception {
        String requestJson = """
                {
                    "imageUrl": "https://example.com/new.jpg",
                    "isPrimary": true,
                    "displayOrder": 2
                }
                """;

        EventImage image = EventImage.builder().id(imageId).imageUrl("https://example.com/new.jpg").build();
        EventImageResponse response = EventImageResponse.builder().id(imageId).imageUrl("https://example.com/new.jpg").build();

        when(eventImageService.addImageToEvent(eq(eventId), any(EventImageRequest.class))).thenReturn(image);
        when(eventImageMapper.toResponse(image)).thenReturn(response);

        mockMvc.perform(post("/api/v1/events/{eventId}/images", eventId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(imageId.toString()));
    }

    @Test
    void shouldDeleteImageFromEvent() throws Exception {
        doNothing().when(eventImageService).deleteImageFromEvent(eventId, imageId);

        mockMvc.perform(delete("/api/v1/events/{eventId}/images/{imageId}", eventId, imageId))
                .andExpect(status().isNoContent());
    }
}
