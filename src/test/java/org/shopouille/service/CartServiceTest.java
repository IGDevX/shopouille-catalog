package org.shopouille.service;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shopouille.dto.request.AddCartItem;
import org.shopouille.dto.request.UpdateCartItemQuantity;
import org.shopouille.entity.Cart;
import org.shopouille.entity.CartItem;
import org.shopouille.entity.Product;
import org.shopouille.entity.Variant;

@QuarkusTest
class CartServiceTest {

    @Inject
    CartService cartService;

    Long userId = 1L;
    Long otherUserId = 99L;

    Product product;
    Variant activeVariant;
    Variant inactiveVariant;

    @BeforeEach
    @Transactional
    void setUp() {
        CartItem.deleteAll();
        Cart.deleteAll();
        Variant.deleteAll();
        Product.deleteAll();

        product = new Product();
        product.title = "Test Product";
        product.descriptionHtml = "desc";
        product.slug = "";
        product.seoTitle = "Test";
        product.createdAt = Instant.now();
        product.updatedAt = Instant.now();
        product.persist();

        activeVariant = new Variant();
        activeVariant.product = product;
        activeVariant.sku = "sku-1";
        activeVariant.isActive = true;
        activeVariant.quantity = 999;
        activeVariant.priceAmount = 10;
        activeVariant.createdAt = Instant.now();
        activeVariant.updatedAt = Instant.now();
        activeVariant.persist();

        inactiveVariant = new Variant();
        inactiveVariant.product = product;
        inactiveVariant.sku = "sku-2";
        inactiveVariant.isActive = false;
        inactiveVariant.quantity = 500;
        inactiveVariant.priceAmount = 5;
        inactiveVariant.createdAt = Instant.now();
        inactiveVariant.updatedAt = Instant.now();
        inactiveVariant.persist();
    }

    // ---------------------------------------------------------
    // getOrCreateCart
    // ---------------------------------------------------------
    @Test
    @Transactional
    void testGetOrCreateCartCreatesNew() {
        Cart cart = cartService.getOrCreateCart(userId);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart);

        assertNotNull(cart);
        assertEquals(userId, cart.userId);
        assertTrue(cart.items.isEmpty());
    }

    // ---------------------------------------------------------
    // addItem
    // ---------------------------------------------------------
    @Test
    @Transactional
    void testAddItemCreatesNewCartItem() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 2;

        Cart cart = cartService.addItem(userId, req);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart);

        assertEquals(1, cart.items.size());
        assertEquals(2, cart.items.get(0).quantity);
    }

    @Test
    @Transactional
    void testAddItemToExistingCartIncreasesQuantity() {
        AddCartItem req1 = new AddCartItem();
        req1.variantId = activeVariant.id;
        req1.quantity = 1;
        cartService.addItem(userId, req1);

        AddCartItem req2 = new AddCartItem();
        req2.variantId = activeVariant.id;
        req2.quantity = 3;

        Cart cart = cartService.addItem(userId, req2);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart);

        assertEquals(1, cart.items.size());
        assertEquals(4, cart.items.get(0).quantity);
    }

    @Test
    @Transactional
    void testAddItemInvalidVariantThrows() {
        AddCartItem req = new AddCartItem();
        req.variantId = 999L;
        req.quantity = 1;

        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(userId, req));
    }

    @Test
    @Transactional
    void testAddItemInactiveVariantThrows() {
        AddCartItem req = new AddCartItem();
        req.variantId = inactiveVariant.id;
        req.quantity = 1;

        assertThrows(IllegalArgumentException.class, () -> cartService.addItem(userId, req));
    }

    // ---------------------------------------------------------
    // updateItemQuantity
    // ---------------------------------------------------------
    @Test
    @Transactional
    void testUpdateItemQuantityChangesValue() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 2;

        Cart created = cartService.addItem(userId, req);
        created = Cart.findById(created.id);
        created.items = CartItem.list("cart", created);
        Long itemId = created.items.get(0).id;

        UpdateCartItemQuantity update = new UpdateCartItemQuantity();
        update.quantity = 5;

        Cart cart = cartService.updateItemQuantity(userId, itemId, update);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart);

        assertEquals(1, cart.items.size());
        assertEquals(5, cart.items.get(0).quantity);
    }

    @Test
    @Transactional
    void testUpdateItemQuantityZeroDeletesItem() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 2;

        Cart created = cartService.addItem(userId, req);
        created = Cart.findById(created.id);
        created.items = CartItem.list("cart", created);
        Long itemId = created.items.get(0).id;

        UpdateCartItemQuantity update = new UpdateCartItemQuantity();
        update.quantity = 0;

        Cart cart = cartService.updateItemQuantity(userId, itemId, update);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart);

        assertTrue(cart.items.isEmpty());
    }

    @Test
    @Transactional
    void testUpdateItemQuantityOtherUserThrows() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 2;

        Cart created = cartService.addItem(userId, req);
        created = Cart.findById(created.id);
        created.items = CartItem.list("cart", created);
        Long itemId = created.items.get(0).id;

        UpdateCartItemQuantity update = new UpdateCartItemQuantity();
        update.quantity = 3;

        assertThrows(IllegalArgumentException.class, () ->
                cartService.updateItemQuantity(otherUserId, itemId, update));
    }

    // ---------------------------------------------------------
    // removeItem
    // ---------------------------------------------------------
    @Test
    @Transactional
    void testRemoveItemWorks() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 1;

        Cart created = cartService.addItem(userId, req);
        created = Cart.findById(created.id);
        created.items = CartItem.list("cart", created);
        Long itemId = created.items.get(0).id;

        Cart cart = cartService.removeItem(userId, itemId);
        cart = Cart.findById(cart.id);
        cart.items = CartItem.list("cart", cart); // recharge la collection après suppression
        assertTrue(cart.items.isEmpty());

    }

    // ---------------------------------------------------------
    // clearCart
    // ---------------------------------------------------------
    @Test
    @Transactional
    void testClearCartWorks() {
        AddCartItem req = new AddCartItem();
        req.variantId = activeVariant.id;
        req.quantity = 1;

        cartService.addItem(userId, req);
        cartService.addItem(userId, req);

        Cart cart = cartService.clearCart(userId);
        cart = Cart.findById(cart.id);

        assertTrue(cart.items.isEmpty());
    }
}
