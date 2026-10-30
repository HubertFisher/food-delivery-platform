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
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * MongoDB document representing a dish or beverage in a restaurant's menu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "menu_items")
public class MenuItem {

    @Id
    private String id;

    @Indexed
    private String restaurantId;

    private String name;

    private String description;

    private String category;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal price;

    @Builder.Default
    private boolean available = true;

    private List<String> tags;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;
}
