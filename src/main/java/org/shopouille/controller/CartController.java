package org.shopouille.controller;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.stream.Collectors;
import org.shopouille.dto.request.AddCartItem;
import org.shopouille.dto.request.UpdateCartItemQuantity;
import org.shopouille.dto.response.CartDTO;
import org.shopouille.dto.response.CartItemDTO;
import org.shopouille.entity.Cart;
import org.shopouille.service.CartService;

@RequestScoped
@Path("/cart")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CartController {

    @Inject
    CartService cartService;

    // -------------------------------------------------------
    // POST /cart → créer un panier vide pour un user
    // -------------------------------------------------------
    @POST
    @Transactional
    public Response createCart(@QueryParam("userId") Long userId) {
        if (userId == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("userId requis").build();
        }
        Cart cart = cartService.getOrCreateCart(userId);
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // GET /cart/{userId} → récupérer le panier d’un user
    // -------------------------------------------------------
    @GET
    @Path("/{userId}")
    public Response getCart(@PathParam("userId") Long userId) {
        Cart cart = cartService.getCart(userId);
        if (cart == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // POST /cart/{userId}/items → ajouter un item
    // -------------------------------------------------------
    @POST
    @Path("/{userId}/items")
    @Transactional
    public Response addItem(@PathParam("userId") Long userId, AddCartItem req) {
        Cart cart = cartService.addItem(userId, req);
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // PUT /cart/{userId}/items/{itemId} → mettre à jour la quantité
    // -------------------------------------------------------
    @PUT
    @Path("/{userId}/items/{itemId}")
    @Transactional
    public Response updateItem(
            @PathParam("userId") Long userId,
            @PathParam("itemId") Long itemId,
            UpdateCartItemQuantity req
    ) {
        Cart cart = cartService.updateItemQuantity(userId, itemId, req);
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // DELETE /cart/{userId}/items/{itemId} → supprimer un item
    // -------------------------------------------------------
    @DELETE
    @Path("/{userId}/items/{itemId}")
    @Transactional
    public Response removeItem(
            @PathParam("userId") Long userId,
            @PathParam("itemId") Long itemId
    ) {
        Cart cart = cartService.removeItem(userId, itemId);
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // DELETE /cart/{userId} → vider le panier
    // -------------------------------------------------------
    @DELETE
    @Path("/{userId}")
    @Transactional
    public Response clearCart(@PathParam("userId") Long userId) {
        Cart cart = cartService.clearCart(userId);
        return Response.ok(toCartResponse(cart)).build();
    }

    // -------------------------------------------------------
    // Helper pour transformer Cart en DTO Response
    // -------------------------------------------------------
    private CartDTO toCartResponse(Cart cart) {
        CartDTO resp = new CartDTO();
        resp.id = cart.id;
        resp.userId = cart.userId;
        resp.items = cart.items == null ? null :
                cart.items.stream().map(item -> {
                    CartItemDTO ci = new CartItemDTO();
                    ci.id = item.id;
                    ci.variantId = item.variant != null ? item.variant.id : null;
                    ci.quantity = item.quantity;
                    ci.priceAmount = item.priceAmount;
                    ci.priceCurrency = item.priceCurrency;
                    return ci;
                }).collect(Collectors.toList());
        return resp;
    }
}
