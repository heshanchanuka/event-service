package com.eventbooking.event_service.mapper;

import com.eventbooking.event_service.domain.*;
import com.eventbooking.event_service.dto.request.EventCreateRequest;
import com.eventbooking.event_service.dto.response.EventResponse;
import com.eventbooking.event_service.dto.response.EventSummaryResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventMapperTest {

    private EventMapper eventMapper;

    @BeforeEach
    void setUp() {
        VenueMapper venueMapper = new VenueMapper();
        CategoryMapper categoryMapper = new CategoryMapper();
        EventImageMapper eventImageMapper = new EventImageMapper();
        eventMapper = new EventMapper(venueMapper, categoryMapper, eventImageMapper);
    }

    @Test
    void shouldMapEventCreateRequestToEntityWithDraftStatus() {
        Venue venue = Venue.builder().id(UUID.randomUUID()).name("Test Venue").build();
        Organizer organizer = Organizer.builder().id(UUID.randomUUID()).displayName("Test Org").build();
        Category category = Category.builder().id(UUID.randomUUID()).name("Music").build();

        EventCreateRequest request = EventCreateRequest.builder()
                .title("Rock Concert")
                .description("Live music concert")
                .venueId(venue.getId())
                .organizerId(organizer.getId())
                .startDateTime(LocalDateTime.now().plusDays(10))
                .endDateTime(LocalDateTime.now().plusDays(10).plusHours(4))
                .build();

        Event event = eventMapper.toEntity(request, venue, organizer, Set.of(category));

        assertNotNull(event);
        assertEquals("Rock Concert", event.getTitle());
        assertEquals("Live music concert", event.getDescription());
        assertEquals(EventStatus.DRAFT, event.getStatus());
        assertEquals(venue, event.getVenue());
        assertEquals(organizer, event.getOrganizer());
        assertEquals(1, event.getCategories().size());
    }

    @Test
    void shouldMapEventToSummaryResponseWithPrimaryImage() {
        Venue venue = Venue.builder().id(UUID.randomUUID()).name("Colombo Stadium").build();
        Event event = Event.builder()
                .id(UUID.randomUUID())
                .title("Cricket Match")
                .startDateTime(LocalDateTime.of(2026, 12, 1, 15, 0))
                .status(EventStatus.PUBLISHED)
                .venue(venue)
                .bannerImageUrl("http://example.com/banner.jpg")
                .build();

        EventSummaryResponse summary = eventMapper.toSummaryResponse(event);

        assertNotNull(summary);
        assertEquals(event.getId(), summary.getId());
        assertEquals("Cricket Match", summary.getTitle());
        assertEquals("Colombo Stadium", summary.getVenueName());
        assertEquals(EventStatus.PUBLISHED, summary.getStatus());
        assertEquals("http://example.com/banner.jpg", summary.getPrimaryImageUrl());
    }

    @Test
    void shouldMapEventToResponse() {
        Venue venue = Venue.builder()
                .id(UUID.randomUUID())
                .name("Grand Hall")
                .address("123 Street")
                .city("Kandy")
                .country("Sri Lanka")
                .capacity(500)
                .build();

        Organizer organizer = Organizer.builder()
                .id(UUID.randomUUID())
                .displayName("Organizer 1")
                .contactEmail("org@test.com")
                .build();

        Event event = Event.builder()
                .id(UUID.randomUUID())
                .title("Art Exhibition")
                .description("Modern art")
                .startDateTime(LocalDateTime.now().plusDays(5))
                .status(EventStatus.PUBLISHED)
                .venue(venue)
                .organizer(organizer)
                .build();

        EventResponse response = eventMapper.toResponse(event);

        assertNotNull(response);
        assertEquals("Art Exhibition", response.getTitle());
        assertEquals("Grand Hall", response.getVenue().getName());
        assertEquals("Organizer 1", response.getOrganizer().getDisplayName());
    }
}
