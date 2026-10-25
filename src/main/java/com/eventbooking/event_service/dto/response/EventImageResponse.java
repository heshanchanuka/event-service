package com.eventbooking.event_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventImageResponse {

    private UUID id;
    private String imageUrl;
    private Boolean isPrimary;
    private Integer displayOrder;
}
