package kz.alitulegenov.catalog.dto;

import kz.alitulegenov.catalog.domain.CuisineType;
import kz.alitulegenov.catalog.domain.Restaurant;

import java.time.Instant;

public record RestaurantResponse(
        String id,
        String name,
        String description,
        CuisineType cuisineType,
        String address,
        String phone,
        boolean open,
        double rating,
        Instant createdAt
) {
    public static RestaurantResponse from(Restaurant restaurant) {
        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getCuisineType(),
                restaurant.getAddress(),
                restaurant.getPhone(),
                restaurant.isOpen(),
                restaurant.getRating(),
                restaurant.getCreatedAt()
        );
    }
}
