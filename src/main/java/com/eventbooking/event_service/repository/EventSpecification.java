package com.eventbooking.event_service.repository;

import com.eventbooking.event_service.domain.Category;
import com.eventbooking.event_service.domain.Event;
import com.eventbooking.event_service.domain.EventStatus;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EventSpecification {

    public static Specification<Event> filterEvents(
            String city,
            UUID categoryId,
            UUID organizerId,
            EventStatus status,
            LocalDateTime startDateFrom,
            LocalDateTime startDateTo,
            String q) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (city != null && !city.isBlank()) {
                predicates.add(cb.equal(
                        cb.lower(root.get("venue").get("city")),
                        city.trim().toLowerCase()
                ));
            }

            if (categoryId != null) {
                Join<Event, Category> categoryJoin = root.join("categories");
                predicates.add(cb.equal(categoryJoin.get("id"), categoryId));
                if (query != null) {
                    query.distinct(true);
                }
            }

            if (organizerId != null) {
                predicates.add(cb.equal(root.get("organizer").get("id"), organizerId));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (startDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startDateTime"), startDateFrom));
            }

            if (startDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDateTime"), startDateTo));
            }

            if (q != null && !q.isBlank()) {
                String searchPattern = "%" + q.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("title")), searchPattern));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
