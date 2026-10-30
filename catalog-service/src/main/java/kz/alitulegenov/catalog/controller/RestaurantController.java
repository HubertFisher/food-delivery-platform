package kz.alitulegenov.catalog.controller;

import jakarta.validation.Valid;
import kz.alitulegenov.catalog.domain.CuisineType;
import kz.alitulegenov.catalog.dto.CreateMenuItemRequest;
import kz.alitulegenov.catalog.dto.CreateRestaurantRequest;
import kz.alitulegenov.catalog.dto.MenuItemResponse;
import kz.alitulegenov.catalog.dto.RestaurantResponse;
import kz.alitulegenov.catalog.service.CatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final CatalogService catalogService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantResponse createRestaurant(@Valid @RequestBody CreateRestaurantRequest request) {
        return catalogService.createRestaurant(request);
    }

    @GetMapping
    public List<RestaurantResponse> getOpenRestaurants(
            @RequestParam(required = false) CuisineType cuisineType
    ) {
        return catalogService.getOpenRestaurants(cuisineType);
    }

    @GetMapping("/{restaurantId}")
    public RestaurantResponse getRestaurantById(@PathVariable String restaurantId) {
        return catalogService.getRestaurantById(restaurantId);
    }

    @PostMapping("/{restaurantId}/menu-items")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse addMenuItem(
            @PathVariable String restaurantId,
            @Valid @RequestBody CreateMenuItemRequest request
    ) {
        return catalogService.addMenuItem(restaurantId, request);
    }

    @GetMapping("/{restaurantId}/menu-items")
    public List<MenuItemResponse> getRestaurantMenu(@PathVariable String restaurantId) {
        return catalogService.getRestaurantMenu(restaurantId);
    }

    @PatchMapping("/{restaurantId}/menu-items/{itemId}/availability")
    public MenuItemResponse updateMenuItemAvailability(
            @PathVariable String restaurantId,
            @PathVariable String itemId,
            @RequestParam boolean available
    ) {
        return catalogService.updateMenuItemAvailability(restaurantId, itemId, available);
    }
}
