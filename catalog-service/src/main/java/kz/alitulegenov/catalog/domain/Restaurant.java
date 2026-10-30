package kz.alitulegenov.catalog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing a partner restaurant on the platform.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants")
public class Restaurant {

    @Id
    private String id;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private CuisineType cuisineType;

    private String address;

    private String phone;

    @Builder.Default
    private boolean open = true;

    @Builder.Default
    private double rating = 5.0;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
