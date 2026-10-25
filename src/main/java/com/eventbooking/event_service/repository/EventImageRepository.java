package com.eventbooking.event_service.repository;

import com.eventbooking.event_service.domain.EventImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventImageRepository extends JpaRepository<EventImage, UUID> {

    List<EventImage> findByEventIdOrderByDisplayOrderAsc(UUID eventId);

    Optional<EventImage> findByIdAndEventId(UUID imageId, UUID eventId);
}
