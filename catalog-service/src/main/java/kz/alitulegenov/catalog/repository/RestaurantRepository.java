package kz.alitulegenov.catalog.repository;

import kz.alitulegenov.catalog.domain.CuisineType;
import kz.alitulegenov.catalog.domain.Restaurant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {

    List<Restaurant> findByOpenTrue();

    List<Restaurant> findByCuisineTypeAndOpenTrue(CuisineType cuisineType);
}
