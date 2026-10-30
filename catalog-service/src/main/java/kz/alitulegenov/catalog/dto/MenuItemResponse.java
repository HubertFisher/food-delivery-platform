package kz.alitulegenov.catalog.dto;

import kz.alitulegenov.catalog.domain.MenuItem;

import java.math.BigDecimal;
import java.util.List;

public record MenuItemResponse(
        String id,
        String restaurantId,
        String name,
        String description,
        String category,
        BigDecimal price,
        boolean available,
        List<String> tags
) {
    public static MenuItemResponse from(MenuItem item) {
        return new MenuItemResponse(
                item.getId(),
                item.getRestaurantId(),
                item.getName(),
                item.getDescription(),
                item.getCategory(),
                item.getPrice(),
                item.isAvailable(),
                item.getTags()
        );
    }
}
