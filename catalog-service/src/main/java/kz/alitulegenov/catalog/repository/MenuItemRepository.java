package kz.alitulegenov.catalog.repository;

import kz.alitulegenov.catalog.domain.MenuItem;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

    List<MenuItem> findByRestaurantId(String restaurantId);

    List<MenuItem> findByRestaurantIdAndIdIn(String restaurantId, Collection<String> ids);
}
