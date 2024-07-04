package lunatech;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import lunatech.entities.CommandEntity;
import lunatech.entities.ProductEntity;
import lunatech.entities.UserEntity;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This class is executed everytime that we launch the application. We use it to load fixtures.
 */
@Singleton
public class Startup {
    private static final Logger logger = Logger.getLogger(Startup.class);

    private final List<UserEntity> users = List.of(
            new UserEntity(
                    new ObjectId("668559ff530d7301332f325f"),
                    "Ewen",
                    new HashMap<>(),
                    List.of(
                            new CommandEntity(
                                    new ObjectId(),
                                    LocalDateTime.now().minusDays(1),
                                    Map.of("66855ff2530d7301332f3273", 1),
                                    600
                            )
                    )
            )
    );

    private final List<ProductEntity> products = List.of(
            new ProductEntity(
                    new ObjectId("66855ff2530d7301332f3272"),
                    "Iphone",
                    "The best of all!",
                    5, 700
            ),
            new ProductEntity(
                    new ObjectId("66855ff2530d7301332f3273"),
                    "Android",
                    "The best of all!",
                    4, 600
            )
    );


    @Transactional
    public void loadFixtures(@Observes StartupEvent evt) {
        logger.info("Executing fixtures startup operation");

        UserEntity.deleteAll();
        users.forEach(u -> u.persist());

        ProductEntity.deleteAll();
        products.forEach(u -> u.persist());
    }
}