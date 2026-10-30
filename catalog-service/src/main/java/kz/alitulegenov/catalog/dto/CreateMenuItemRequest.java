package kz.alitulegenov.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record CreateMenuItemRequest(
        @NotBlank(message = "Item name is required")
        String name,

        String description,

        @NotBlank(message = "Category is required")
        String category,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        List<String> tags
) {
}
