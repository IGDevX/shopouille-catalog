package org.shopouille.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.shopouille.entity.Cart;
import org.shopouille.entity.CartItem;
import org.shopouille.entity.Variant;
import org.shopouille.dto.request.AddCartItem;
import org.shopouille.dto.request.UpdateCartItemQuantity;

import java.util.ArrayList;

@ApplicationScoped
public class CartService {

    public Cart getOrCreateCart(Long userId) {
        Cart cart = Cart.find("userId", userId).firstResult();
        if (cart == null) {
            cart = new Cart();
            cart.userId = userId;
            cart.items = new ArrayList<>();
            cart.persist();
        }
        return cart;
    }

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
            throw new IllegalArgumentException("Variant not found");
        }

        if (req.quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }

        CartItem existing = CartItem.find(
                "cart = ?1 and variant = ?2",
                cart, variant
        ).firstResult();

        if (existing != null) {
            existing.quantity += req.quantity;
            return cart;
        }

        CartItem item = new CartItem();
        item.cart = cart;
        item.variant = variant;
        item.quantity = req.quantity;
        item.priceAmount = variant.priceAmount;
        item.priceCurrency = "EUR"; // default for now
        item.persist();

        return cart;
    }

    // -------------------------------------------------------
    // UPDATE ITEM QUANTITY
    // -------------------------------------------------------
    @Transactional
    public Cart updateItemQuantity(Long userId, Long itemId, UpdateCartItemQuantity req) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = CartItem.findById(itemId);
        if (item == null || !item.cart.id.equals(cart.id)) {
            throw new IllegalArgumentException("Cart item not found");
        }

        if (req.quantity <= 0) {
            // quantité <= 0 → on supprime l’item automatiquement
            item.delete();
        } else {
            item.quantity = req.quantity;
        }

        return cart;
    }

    // -------------------------------------------------------
    // REMOVE ITEM
    // -------------------------------------------------------
    @Transactional
    public Cart removeItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = CartItem.findById(itemId);
        if (item == null || !item.cart.id.equals(cart.id)) {
            throw new IllegalArgumentException("Cart item not found");
        }

        item.delete();

        return cart;
    }

    // -------------------------------------------------------
    // CLEAR CART
    // -------------------------------------------------------
    @Transactional
    public Cart clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);

        CartItem.delete("cart", cart);

        return cart;
    }
}
