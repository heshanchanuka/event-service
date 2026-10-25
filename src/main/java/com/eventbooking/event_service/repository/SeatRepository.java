package com.eventbooking.event_service.repository;




import com.eventbooking.event_service.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    // Event එකකට අදාළ ඔක්කොම Seats ටික හොයාගන්න custom method එකක්
    List<Seat> findByEventId(Long eventId);
}