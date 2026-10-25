package com.eventbooking.event_service.controller;





import com.eventbooking.event_service.dto.SeatResponse;
import com.eventbooking.event_service.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
@RequiredArgsConstructor
public class SeatController {

    private final EventService eventService;

    // 3. අදාළ ID එක දීලා එක Seat එකක විස්තර ගන්න
    @GetMapping("/{seatId}")
    public ResponseEntity<SeatResponse> getSeatById(@PathVariable Long seatId) {
        SeatResponse seat = SeatResponse.from(eventService.getSeatById(seatId));
        return ResponseEntity.ok(seat);
    }

    @GetMapping
    public ResponseEntity<List<SeatResponse>> getAllSeats() {
        List<SeatResponse> seats = eventService.getAllSeats();
        return ResponseEntity.ok(seats);
    }
}