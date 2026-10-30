package kz.alitulegenov.catalog.grpc;

import io.grpc.stub.StreamObserver;
import kz.alitulegenov.catalog.domain.MenuItem;
import kz.alitulegenov.catalog.domain.Restaurant;
import kz.alitulegenov.catalog.repository.MenuItemRepository;
import kz.alitulegenov.catalog.repository.RestaurantRepository;
import kz.alitulegenov.common.grpc.catalog.CatalogGrpcServiceGrpc;
import kz.alitulegenov.common.grpc.catalog.OrderItemRequest;
import kz.alitulegenov.common.grpc.catalog.ValidateOrderItemsRequest;
import kz.alitulegenov.common.grpc.catalog.ValidateOrderItemsResponse;
import kz.alitulegenov.common.grpc.catalog.ValidatedMenuItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * gRPC Server implementation for high-speed synchronous order item validation and pricing lookup.
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CatalogGrpcServiceImpl extends CatalogGrpcServiceGrpc.CatalogGrpcServiceImplBase {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public void validateOrderItems(
            ValidateOrderItemsRequest request,
            StreamObserver<ValidateOrderItemsResponse> responseObserver
    ) {
        String restaurantId = request.getRestaurantId();
        log.info("Received gRPC ValidateOrderItems request for restaurantId={} with {} items",
                restaurantId, request.getItemsCount());

        Optional<Restaurant> restaurantOpt = restaurantRepository.findById(restaurantId);
        if (restaurantOpt.isEmpty()) {
            sendFailure(responseObserver, "Restaurant not found: " + restaurantId);
            return;
        }

        Restaurant restaurant = restaurantOpt.get();
        if (!restaurant.isOpen()) {
            sendFailure(responseObserver, "Restaurant '" + restaurant.getName() + "' is currently closed");
            return;
        }

        List<String> requestedItemIds = request.getItemsList().stream()
                .map(OrderItemRequest::getMenuItemId)
                .toList();

        // Batch fetch all requested menu items in a single MongoDB query to avoid N+1 queries
        Map<String, MenuItem> menuItemMap = menuItemRepository
                .findByRestaurantIdAndIdIn(restaurantId, requestedItemIds)
                .stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        List<ValidatedMenuItem> validatedItems = new ArrayList<>();

        for (OrderItemRequest reqItem : request.getItemsList()) {
            MenuItem menuItem = menuItemMap.get(reqItem.getMenuItemId());
            if (menuItem == null) {
                sendFailure(responseObserver, "Menu item not found: " + reqItem.getMenuItemId());
                return;
            }
            if (!menuItem.isAvailable()) {
                sendFailure(responseObserver, "Menu item '" + menuItem.getName() + "' is currently unavailable");
                return;
            }

            validatedItems.add(ValidatedMenuItem.newBuilder()
                    .setMenuItemId(menuItem.getId())
                    .setName(menuItem.getName())
                    .setUnitPrice(menuItem.getPrice().toPlainString())
                    .setQuantity(reqItem.getQuantity())
                    .setAvailable(true)
                    .build());
        }

        ValidateOrderItemsResponse response = ValidateOrderItemsResponse.newBuilder()
                .setValid(true)
                .setRestaurantName(restaurant.getName())
                .addAllValidatedItems(validatedItems)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void sendFailure(StreamObserver<ValidateOrderItemsResponse> responseObserver, String reason) {
        log.warn("Order items validation failed: {}", reason);
        ValidateOrderItemsResponse response = ValidateOrderItemsResponse.newBuilder()
                .setValid(false)
                .setFailureReason(reason)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
