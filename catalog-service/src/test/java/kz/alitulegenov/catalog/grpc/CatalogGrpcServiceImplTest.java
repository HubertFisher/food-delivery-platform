package kz.alitulegenov.catalog.grpc;

import io.grpc.stub.StreamObserver;
import kz.alitulegenov.catalog.domain.CuisineType;
import kz.alitulegenov.catalog.domain.MenuItem;
import kz.alitulegenov.catalog.domain.Restaurant;
import kz.alitulegenov.catalog.repository.MenuItemRepository;
import kz.alitulegenov.catalog.repository.RestaurantRepository;
import kz.alitulegenov.common.grpc.catalog.OrderItemRequest;
import kz.alitulegenov.common.grpc.catalog.ValidateOrderItemsRequest;
import kz.alitulegenov.common.grpc.catalog.ValidateOrderItemsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogGrpcServiceImplTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private StreamObserver<ValidateOrderItemsResponse> responseObserver;

    @InjectMocks
    private CatalogGrpcServiceImpl catalogGrpcService;

    @Test
    void validateOrderItems_shouldReturnValidResponse_whenAllItemsAvailable() {
        String restaurantId = "rest-1";
        Restaurant restaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Napoli Pizza")
                .cuisineType(CuisineType.PIZZA)
                .open(true)
                .build();

        MenuItem margherita = MenuItem.builder()
                .id("item-1")
                .restaurantId(restaurantId)
                .name("Margherita")
                .price(new BigDecimal("14.50"))
                .available(true)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(restaurant));
        when(menuItemRepository.findByRestaurantIdAndIdIn(eq(restaurantId), anyCollection()))
                .thenReturn(List.of(margherita));

        ValidateOrderItemsRequest request = ValidateOrderItemsRequest.newBuilder()
                .setRestaurantId(restaurantId)
                .addItems(OrderItemRequest.newBuilder().setMenuItemId("item-1").setQuantity(2).build())
                .build();

        catalogGrpcService.validateOrderItems(request, responseObserver);

        ArgumentCaptor<ValidateOrderItemsResponse> captor = ArgumentCaptor.forClass(ValidateOrderItemsResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ValidateOrderItemsResponse response = captor.getValue();
        assertThat(response.getValid()).isTrue();
        assertThat(response.getRestaurantName()).isEqualTo("Napoli Pizza");
        assertThat(response.getValidatedItemsCount()).isEqualTo(1);
        assertThat(response.getValidatedItems(0).getUnitPrice()).isEqualTo("14.50");
        assertThat(response.getValidatedItems(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void validateOrderItems_shouldReturnInvalid_whenRestaurantIsClosed() {
        String restaurantId = "rest-1";
        Restaurant closedRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Napoli Pizza")
                .open(false)
                .build();

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(closedRestaurant));

        ValidateOrderItemsRequest request = ValidateOrderItemsRequest.newBuilder()
                .setRestaurantId(restaurantId)
                .addItems(OrderItemRequest.newBuilder().setMenuItemId("item-1").setQuantity(1).build())
                .build();

        catalogGrpcService.validateOrderItems(request, responseObserver);

        ArgumentCaptor<ValidateOrderItemsResponse> captor = ArgumentCaptor.forClass(ValidateOrderItemsResponse.class);
        verify(responseObserver).onNext(captor.capture());
        verify(responseObserver).onCompleted();

        ValidateOrderItemsResponse response = captor.getValue();
        assertThat(response.getValid()).isFalse();
        assertThat(response.getFailureReason()).contains("currently closed");
    }
}
