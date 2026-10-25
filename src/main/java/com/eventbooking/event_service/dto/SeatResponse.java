package com.eventbooking.event_service.dto;




import com.eventbooking.event_service.domain.Seat;

import java.math.BigDecimal;

public record SeatResponse(
        Long id,
        String seatNumber,
        BigDecimal price,
        String status
) {
    // Entity එකක් දුන්නම ඒකෙන් DTO එකක් හදලා දෙන Factory Method එකක්
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getPrice(),
                seat.getStatus().name()
        );
    }
}
