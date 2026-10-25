package com.eventbooking.event_service.repository;




import com.eventbooking.event_service.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    // දැනට Spring Data JPA වලින් දෙන default methods (save, findById, findAll) ඇති.
}