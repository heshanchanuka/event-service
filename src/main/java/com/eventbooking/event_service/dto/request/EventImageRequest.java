package com.eventbooking.event_service.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventImageRequest {

    @NotBlank(message = "Image URL is required")
    @URL(message = "Invalid image URL format")
    private String imageUrl;

    @Builder.Default
    private Boolean isPrimary = false;

    @Min(value = 0, message = "Display order must be >= 0")
    @Builder.Default
    private Integer displayOrder = 0;
}
