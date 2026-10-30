package kz.alitulegenov.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Enables automatic auditing fields (@CreatedDate, @LastModifiedDate) on MongoDB documents.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
}
