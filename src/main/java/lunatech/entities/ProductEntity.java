package lunatech.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.util.Map;

@MongoEntity(collection = "products")
public class ProductEntity extends PanacheMongoEntity {
    public ObjectId id;
    public String name;
    public String description;
    public Integer availableQuantity;
    public Integer price;

    public ProductEntity() {}

    public ProductEntity(
        @NotNull ObjectId id,
        @NotNull String name,
        @NotNull String description,
        @NotNull Integer availableQuantity,
        @NotNull Integer price
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.availableQuantity = availableQuantity;
        this.price = price;
    }
}