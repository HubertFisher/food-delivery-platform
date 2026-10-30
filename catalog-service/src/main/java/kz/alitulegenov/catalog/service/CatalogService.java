package kz.alitulegenov.catalog.service;

import kz.alitulegenov.catalog.config.RedisCacheConfig;
import kz.alitulegenov.catalog.domain.CuisineType;
import kz.alitulegenov.catalog.domain.MenuItem;
import kz.alitulegenov.catalog.domain.Restaurant;
import kz.alitulegenov.catalog.dto.CreateMenuItemRequest;
import kz.alitulegenov.catalog.dto.CreateRestaurantRequest;
import kz.alitulegenov.catalog.dto.MenuItemResponse;
import kz.alitulegenov.catalog.dto.RestaurantResponse;
import kz.alitulegenov.catalog.exception.ResourceNotFoundException;
import kz.alitulegenov.catalog.repository.MenuItemRepository;
import kz.alitulegenov.catalog.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public RestaurantResponse createRestaurant(CreateRestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .description(request.description())
                .cuisineType(request.cuisineType())
                .address(request.address())
                .phone(request.phone())
                .open(true)
                .rating(5.0)
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);
        log.info("Created new restaurant '{}' with id={}", saved.getName(), saved.getId());
        return RestaurantResponse.from(saved);
    }

    public List<RestaurantResponse> getOpenRestaurants(CuisineType cuisineType) {
        List<Restaurant> restaurants = (cuisineType != null)
                ? restaurantRepository.findByCuisineTypeAndOpenTrue(cuisineType)
                : restaurantRepository.findByOpenTrue();

        return restaurants.stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    public RestaurantResponse getRestaurantById(String restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + restaurantId));
        return RestaurantResponse.from(restaurant);
    }

    @CacheEvict(value = RedisCacheConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public MenuItemResponse addMenuItem(String restaurantId, CreateMenuItemRequest request) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        MenuItem menuItem = MenuItem.builder()
                .restaurantId(restaurantId)
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .available(true)
                .tags(request.tags())
                .build();

        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Added menu item '{}' (id={}) to restaurantId={} and evicted menu cache",
                saved.getName(), saved.getId(), restaurantId);
        return MenuItemResponse.from(saved);
    }

    @Cacheable(value = RedisCacheConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public List<MenuItemResponse> getRestaurantMenu(String restaurantId) {
        log.info("Cache miss for restaurantId={} menu - fetching from MongoDB", restaurantId);
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant not found with id: " + restaurantId);
        }

        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(MenuItemResponse::from)
                .toList();
    }

    @CacheEvict(value = RedisCacheConfig.RESTAURANT_MENU_CACHE, key = "#restaurantId")
    public MenuItemResponse updateMenuItemAvailability(String restaurantId, String itemId, boolean available) {
        MenuItem item = menuItemRepository.findById(itemId)
                .filter(m -> m.getRestaurantId().equals(restaurantId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Menu item " + itemId + " not found for restaurant " + restaurantId));

        item.setAvailable(available);
        MenuItem updated = menuItemRepository.save(item);
        log.info("Updated availability of menu item id={} to {} and evicted menu cache", itemId, available);
        return MenuItemResponse.from(updated);
    }
}
