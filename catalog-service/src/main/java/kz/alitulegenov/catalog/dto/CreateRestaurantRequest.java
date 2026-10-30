package kz.alitulegenov.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kz.alitulegenov.catalog.domain.CuisineType;

public record CreateRestaurantRequest(
        @NotBlank(message = "Restaurant name is required")
        String name,

        String description,

        @NotNull(message = "Cuisine type is required")
        CuisineType cuisineType,

        @NotBlank(message = "Address is required")
        String address,

        @NotBlank(message = "Phone number is required")
        String phone
) {
}
