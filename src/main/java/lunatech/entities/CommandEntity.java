package lunatech.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class CommandEntity {
    public ObjectId id;
    public LocalDateTime ts;
    public Map<String, Integer> basket;
    public Integer totalPrice;

    public CommandEntity() {}

    public CommandEntity(
        @NotNull ObjectId id,
        @NotNull LocalDateTime ts,
        @NotNull Map<String, Integer> basket,
        @NotNull Integer totalPrice
    ) {
        this.id = id;
        this.ts = ts;
        this.basket = basket;
        this.totalPrice = totalPrice;
    }
}