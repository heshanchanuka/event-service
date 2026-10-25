package com.eventbooking.event_service.dto;





import com.eventbooking.event_service.domain.Event;

import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        Long id,
        String name,
        String venue,
        LocalDateTime eventTime,
        List<SeatResponse> seats
) {
    // Event Entity එකක් දුන්නම ඒක DTO එකක් කරන Method එක
    public static EventResponse from(Event event) {
        List<SeatResponse> seatResponses = event.getSeats()
                .stream()
                .map(SeatResponse::from)
                .toList();

        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getVenue(),
                event.getEventTime(),
                seatResponses
        );
    }
}