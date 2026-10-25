package com.eventbooking.event_service.dto.response;

import com.eventbooking.event_service.domain.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventSummaryResponse {

    private UUID id;
    private String title;
    private LocalDateTime startDateTime;
    private String venueName;
    private EventStatus status;
    private String primaryImageUrl;
}
