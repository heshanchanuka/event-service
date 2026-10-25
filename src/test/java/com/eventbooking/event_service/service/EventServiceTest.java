package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.*;
import com.eventbooking.event_service.dto.request.EventUpdateRequest;
import com.eventbooking.event_service.mapper.EventMapper;
import com.eventbooking.event_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;
    @Mock
    private VenueRepository venueRepository;
    @Mock
    private OrganizerRepository organizerRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private UUID eventId;
    private Event event;

    @BeforeEach
    void setUp() {
        eventId = UUID.randomUUID();
        event = Event.builder()
                .id(eventId)
                .title("Tech Conference")
                .description("Annual tech summit")
                .status(EventStatus.DRAFT)
                .startDateTime(LocalDateTime.now().plusDays(20))
                .build();
    }

    @Test
    void shouldPublishDraftEventSuccessfully() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event published = eventService.publishEvent(eventId);

        assertNotNull(published);
        assertEquals(EventStatus.PUBLISHED, published.getStatus());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldRejectPublishingAlreadyPublishedEvent() {
        event.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> eventService.publishEvent(eventId));

        assertEquals("Event is already published", ex.getMessage());
        verify(eventRepository, never()).save(any());
    }

    @Test
    void shouldRejectPublishingCancelledEvent() {
        event.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> eventService.publishEvent(eventId));

        assertEquals("Cannot publish a cancelled event", ex.getMessage());
    }

    @Test
    void shouldCancelEventSuccessfully() {
        event.setStatus(EventStatus.PUBLISHED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event cancelled = eventService.cancelEvent(eventId);

        assertEquals(EventStatus.CANCELLED, cancelled.getStatus());
    }

    @Test
    void shouldRejectCancellingAlreadyCancelledEvent() {
        event.setStatus(EventStatus.CANCELLED);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> eventService.cancelEvent(eventId));

        assertEquals("Event is already cancelled", ex.getMessage());
    }

    @Test
    void shouldDistinguishOmittedVsExplicitNullInPatch() {
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        // In PATCH: description is present and set to null (clear value)
        EventUpdateRequest request = new EventUpdateRequest();
        request.setDescription(null); // descriptionPresent becomes true, description = null

        eventService.updateEventPartial(eventId, request);

        assertNull(event.getDescription());
        // Title was omitted, so title should remain untouched
        assertEquals("Tech Conference", event.getTitle());
    }
}
