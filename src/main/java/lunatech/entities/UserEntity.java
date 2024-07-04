package lunatech.entities;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.Map;

@MongoEntity(collection = "users")
public class UserEntity extends PanacheMongoEntity {
    public ObjectId id;
    public String name;
    public Map<String, Integer> basket;
    public List<CommandEntity> commands;

    public UserEntity() {}

    public UserEntity(
            @NotNull ObjectId id,
            @NotNull String name,
            @NotNull Map<String, Integer> basket,
            @NotNull List<CommandEntity> commands
    ) {
        this.id = id;
        this.name = name;
        this.basket = basket;
        this.commands = commands;
    }
}