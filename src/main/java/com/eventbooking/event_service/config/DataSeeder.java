package com.eventbooking.event_service.config;

import com.eventbooking.event_service.domain.*;
import com.eventbooking.event_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final OrganizerRepository organizerRepository;
    private final CategoryRepository categoryRepository;
    private final EventImageRepository eventImageRepository;

    @Override
    public void run(String... args) throws Exception {
        if (eventRepository.count() == 0) {
            log.info("Database is empty. Seeding dummy events and venues...");

            Venue venue = Venue.builder()
                    .name("Nelum Pokuna Mahinda Rajapaksa Theatre")
                    .address("110 Ananda Coomaraswamy Mawatha")
                    .city("Colombo")
                    .country("Sri Lanka")
                    .capacity(1288)
                    .latitude(6.9111)
                    .longitude(79.8652)
                    .build();
            venue = venueRepository.save(venue);

            Organizer organizer = Organizer.builder()
                    .id(UUID.randomUUID())
                    .displayName("Tech Events Global")
                    .contactEmail("contact@techevents.org")
                    .build();
            organizer = organizerRepository.save(organizer);

            Category category = Category.builder()
                    .name("Technology")
                    .description("Conferences, tech talks, and hackathons")
                    .build();
            category = categoryRepository.save(category);

            Event event = Event.builder()
                    .title("Tech Symphony 2026")
                    .description("The premier technology summit bringing developers and innovators together.")
                    .startDateTime(LocalDateTime.now().plusDays(30))
                    .endDateTime(LocalDateTime.now().plusDays(30).plusHours(6))
                    .status(EventStatus.PUBLISHED)
                    .bannerImageUrl("https://images.unsplash.com/photo-1540575467063-178a50c2df87")
                    .venue(venue)
                    .organizer(organizer)
                    .build();
            event.addCategory(category);
            event = eventRepository.save(event);

            EventImage image = EventImage.builder()
                    .event(event)
                    .imageUrl("https://images.unsplash.com/photo-1540575467063-178a50c2df87")
                    .isPrimary(true)
                    .displayOrder(1)
                    .build();
            eventImageRepository.save(image);

            log.info("Dummy data seeding completed successfully!");
        } else {
            log.info("Events already exist in the database. Skipping seeding.");
        }
    }
}
