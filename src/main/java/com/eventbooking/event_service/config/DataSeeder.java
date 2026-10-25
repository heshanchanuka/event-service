package com.eventbooking.event_service.config;







import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.Seat;
import com.eventbooking.event_service.domain.SeatStatus;
import com.eventbooking.event_service.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;

    @Override
    public void run(String... args) throws Exception {
        // Database එකේ events මුකුත් නැත්නම් විතරක් මේක run වෙනවා
        if (eventRepository.count() == 0) {
            log.info("Database is empty. Seeding dummy events and seats...");

            // 1. අලුත් Event එකක් හදනවා
            Event event1 = new Event();
            event1.setName("Tech Symphony 2026");
            event1.setVenue("Nelum Pokuna, Colombo");
            event1.setEventTime(LocalDateTime.now().plusDays(30)); // තව දවස් 30කින් තියෙන්නේ

            // 2. ඒකට අදාළ Seats ටික හදනවා
            Seat seat1 = new Seat();
            seat1.setSeatNumber("A-01");
            seat1.setPrice(new BigDecimal("5000.00"));
            seat1.setStatus(SeatStatus.AVAILABLE);
            seat1.setEvent(event1);

            Seat seat2 = new Seat();
            seat2.setSeatNumber("A-02");
            seat2.setPrice(new BigDecimal("5000.00"));
            seat2.setStatus(SeatStatus.AVAILABLE);
            seat2.setEvent(event1);

            Seat seat3 = new Seat();
            seat3.setSeatNumber("A-03");
            seat3.setPrice(new BigDecimal("5000.00"));
            seat3.setStatus(SeatStatus.AVAILABLE);
            seat3.setEvent(event1);

            // 3. Seats ටික Event එකට ඇතුල් කරනවා
            event1.getSeats().addAll(List.of(seat1, seat2, seat3));

            // 4. Event එක Save කරනවා (CascadeType.ALL නිසා Seats ටිකත් auto save වෙනවා)
            eventRepository.save(event1);

            log.info("Dummy data seeding completed successfully!");
        } else {
            log.info("Events already exist in the database. Skipping seeding.");
        }
    }
}