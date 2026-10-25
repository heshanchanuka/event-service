package com.eventbooking.event_service.service;

import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.Seat;
import com.eventbooking.event_service.dto.SeatResponse;
import com.eventbooking.event_service.exception.ResourceNotFoundException;
import com.eventbooking.event_service.repository.EventRepository;
import com.eventbooking.event_service.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;

    // 1. ඔක්කොම Events ටික ගන්න
    @Transactional(readOnly = true)
    public List<Event> getAllEvents() {
        log.info("Fetching all events from database");
        return eventRepository.findAll();
    }

    // 2. ID එකෙන් එක Event එකක් ගන්න
    @Transactional(readOnly = true)
    public Event getEventById(Long eventId) {
        log.info("Fetching event with ID: {}", eventId);
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));
    }

    // 3. ID එකෙන් එක Seat එකක් ගන්න (Booking Service එකට අනාගතයේදී මේක ඕන වෙනවා)
    @Transactional(readOnly = true)
    public Seat getSeatById(Long seatId) {
        log.info("Fetching seat with ID: {}", seatId);
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seatId));
    }

    // EventService.java ඇතුළේ තියෙන getAllSeats method එක මෙහෙම වෙනස් වෙන්න ඕනේ:

    public List<SeatResponse> getAllSeats() {
        log.info("Fetching all seats from database");

        // 1. Database එකෙන් Seats ඔක්කොම ගන්නවා (මේක List එකක්)
        List<Seat> seats = seatRepository.findAll();

        // 2. ඒ List එකේ තියෙන එකින් එක අරගෙන SeatResponse DTO එකට මාරු කරලා අලුත් List එකක් හදනවා
        return seats.stream()
                .map(seat -> new SeatResponse(
                        seat.getId(),
                        seat.getSeatNumber(),
                        seat.getPrice(),
                        seat.getStatus().name() // Enum එකක් නම් String කරන්න
                ))
                .collect(Collectors.toList());
    }


}
