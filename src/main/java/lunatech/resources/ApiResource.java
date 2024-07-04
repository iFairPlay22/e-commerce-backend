package lunatech.resources;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import lunatech.entities.CommandEntity;
import lunatech.entities.ProductEntity;
import lunatech.entities.UserEntity;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
public class ApiResource {
    private static final Logger logger = Logger.getLogger(ApiResource.class);

    private void slowDown() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GET
    @Path("/products")
    public Response getProducts() {
        slowDown();

        List<ProductEntity> products = ProductEntity.listAll();
        return Response.ok(products).build();
    }

    @GET
    @Path("/user/{userId}")
    public Response getUser(
            @NotNull @PathParam("userId") ObjectId userId
    ) {
        slowDown();

        UserEntity user = UserEntity.findById(userId);
        return Response.ok(user).build();
    }

    @POST
    @Path("/user/{userId}/basket/{productId}")
    public Response addProductToBasket(
            @NotNull @PathParam("userId") ObjectId userId,
            @NotNull @PathParam("productId") ObjectId productId
    ) {
        slowDown();

        UserEntity user = UserEntity.findById(userId);

        // Check that product exists
        ProductEntity productEntity = ProductEntity.findById(productId);

        // Check that we have enough quantity in stock
        int actualRequestedQuantity = user.basket.getOrDefault(productId.toString(), 0);
        if (actualRequestedQuantity + 1 > productEntity.availableQuantity)
            return Response.status(Response.Status.BAD_REQUEST).entity("Not enough product quantity").build();

        // Update the requested quantity in user basket
        user.basket.put(productId.toString(), actualRequestedQuantity+ 1);
        user.persistOrUpdate();

        URI uri = URI.create(String.format("/api/user/%s", userId));
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/user/{userId}/basket/{productId}")
    public Response removeProductToBasket(
            @NotNull @PathParam("userId") ObjectId userId,
            @NotNull @PathParam("productId") ObjectId productId
    ) {
        slowDown();

        UserEntity user = UserEntity.findById(userId);

        // Check that product is in basket
        int actualRequestedQuantity = user.basket.getOrDefault(productId.toString(), 0);
        if (actualRequestedQuantity == 0)
            return Response.status(Response.Status.BAD_REQUEST).build();

        // Update the requested quantity in user basket
        if (actualRequestedQuantity == 1)
            user.basket.remove(productId.toString());
        else
            user.basket.put(productId.toString(), actualRequestedQuantity - 1);
        user.persistOrUpdate();

        URI uri = URI.create(String.format("/api/user/%s", userId));
        return Response.created(uri).build();
    }

    @DELETE
    @Path("/user/{userId}/basket")
    public Response clearProductToBasket(
            @NotNull @PathParam("userId") ObjectId userId
    ) {
        slowDown();

        // Check that product is in basket
        UserEntity user = UserEntity.findById(userId);
        user.basket = new HashMap<>();
        user.persistOrUpdate();

        return Response.noContent().build();
    }

    @POST
    @Path("/user/{userId}/basket/buy")
    public Response buy(
            @NotNull @PathParam("userId") ObjectId userId
    ) {
        slowDown();

        UserEntity user = UserEntity.findById(userId);
        List<ProductEntity> products = ProductEntity.listAll();

        // Reduce product quantity
        user.basket.forEach((productToCommand, quantityToCommand) -> {
            ProductEntity product = products
                    .stream()
                    .filter(p -> p.id.toString().equals(productToCommand))
                    .findFirst()
                    .orElseThrow(IllegalStateException::new);

            product.availableQuantity -= quantityToCommand;

            if (product.availableQuantity <= 0) {
                product.delete();
            } else {
                product.persistOrUpdate();
            }
        });

        // Create new command for this product
        CommandEntity newCommand = new CommandEntity(
                new ObjectId(),
                LocalDateTime.now(),
                user.basket,
                user.basket
                        .entrySet()
                        .stream()
                        .map(commandLot -> {
                            String productToCommand = commandLot.getKey();
                            Integer quantityToCommand = commandLot.getValue();

                            ProductEntity product = products
                                    .stream()
                                    .filter(p -> p.id.toString().equals(productToCommand))
                                    .findFirst()
                                    .orElseThrow(IllegalStateException::new);

                            return quantityToCommand * product.price;
                        })
                        .reduce(Integer::sum)
                        .orElseThrow(IllegalStateException::new)
        );

        // Add command and reset current user basket
        user.commands.add(newCommand);
        user.basket = new HashMap<>();
        user.persistOrUpdate();

        URI uri = URI.create(String.format("/api/user/%s", userId));
        return Response.created(uri).build();
    }
}

