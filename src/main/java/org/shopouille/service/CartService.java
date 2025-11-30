package org.shopouille.service;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import org.shopouille.dto.request.AddCartItem;
import org.shopouille.dto.request.UpdateCartItemQuantity;
import org.shopouille.entity.Cart;
import org.shopouille.entity.CartItem;
import org.shopouille.entity.Variant;

@ApplicationScoped
public class CartService {

    // -------------------------------------------------------
    // GET OR CREATE CART
    // -------------------------------------------------------
    @Transactional
    public Cart getOrCreateCart(Long userId) {
        Cart cart = Cart.find("userId", userId).firstResult();
        if (cart == null) {
            cart = new Cart();
            cart.userId = userId;
            cart.persist();
            cart.items = List.of(); // initialise la collection vide
        }
        return Cart.findById(cart.id); // refresh pour synchroniser la collection
    }

    @Transactional
    public Cart getCart(Long userId) {
        return getOrCreateCart(userId);
    }

    // -------------------------------------------------------
    // ADD ITEM
    // -------------------------------------------------------
    @Transactional
    public Cart addItem(Long userId, AddCartItem req) {
        Cart cart = getOrCreateCart(userId);

        Variant variant = Variant.findById(req.variantId);
        if (variant == null || !variant.isActive) {
            throw new IllegalArgumentException("Variant not found or inactive");
        }

        if (req.quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        CartItem existing = CartItem.find("cart = ?1 and variant = ?2", cart, variant).firstResult();

        if (existing != null) {
            existing.quantity += req.quantity;
            existing.persist();
            return Cart.findById(cart.id); // refresh
        }

        CartItem item = new CartItem();
        item.cart = cart;
        item.variant = variant;
        item.quantity = req.quantity;
        item.priceAmount = variant.priceAmount;
        item.priceCurrency = "EUR";
        item.persist();

        return Cart.findById(cart.id); // refresh
    }

    // -------------------------------------------------------
    // UPDATE ITEM QUANTITY
    // -------------------------------------------------------
    @Transactional
    public Cart updateItemQuantity(Long userId, Long itemId, UpdateCartItemQuantity req) {
        CartItem item = CartItem.findById(itemId);
        if (item == null || !Objects.equals(item.cart.userId, userId)) {
            throw new IllegalArgumentException("Cart item not found");
        }
        Cart cart = item.cart;

        if (req.quantity <= 0) {
            item.delete();
            if (cart.items != null) {
                cart.items.removeIf(ci -> ci.id.equals(itemId)); // supprime de la liste en mémoire
            }
        } else {
            item.quantity = req.quantity;
            item.persist();
        }

        return Cart.findById(item.cart.id); // refresh
    }

    // -------------------------------------------------------
    // REMOVE ITEM
    // -------------------------------------------------------
    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        CartItem item = CartItem.findById(itemId);
        if (item == null || !Objects.equals(item.cart.userId, userId)) {
            throw new IllegalArgumentException("Cart item not found");
        }

        Cart cart = item.cart;
        item.delete(); // supprime de la base

        if (cart.items != null) {
            cart.items.removeIf(ci -> ci.id.equals(itemId)); // supprime de la liste en mémoire
        }

        return cart;
    }



    // -------------------------------------------------------
    // CLEAR CART
    // -------------------------------------------------------
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        List<CartItem> items = CartItem.list("cart", cart);
        for (CartItem item : items) {
            item.delete();
        }

        return Cart.findById(cart.id); // refresh
    }
}
