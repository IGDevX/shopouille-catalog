package org.shopouille.service;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shopouille.dto.request.CreateProductVariant;
import org.shopouille.dto.response.VariantDTO;
import org.shopouille.entity.Product;
import org.shopouille.entity.Variant;

@QuarkusTest
class VariantServiceTest {

    @Inject
    VariantService variantService;

    Long productId;

    @BeforeEach
    @Transactional
    void setUp() {
        Variant.deleteAll();
        Product.deleteAll();

        Product p = new Product();
        p.title = "Test Product";
        p.descriptionHtml = "desc";
        p.slug = "";
        p.seoTitle = "Test";
        p.updatedAt = Instant.now();
        p.createdAt = Instant.now();
        p.persist();

        productId = p.id;

        for (int i = 1; i <= 5; i++) {
            Variant v = new Variant();
            v.product = p;
            v.sku = "SKU-" + i;
            v.priceAmount = i * 10;
            v.quantity = i;
            v.isActive = true;
            v.createdAt = Instant.now();
            v.updatedAt = Instant.now();
            v.persist();
        }
    }

    // -----------------------------
    // listAllVariantsDTOs
    // -----------------------------
    @Test
    void testListAllVariants_noPagination() {
        List<VariantDTO> list = variantService.listAllVariantsDTOs(null, null, null, true);
        assertEquals(5, list.size());
    }

    @Test
    void testListAllVariants_withPagination() {
        List<VariantDTO> list = variantService.listAllVariantsDTOs(0, 2, "id", true);
        assertEquals(2, list.size());
    }

    @Test
    void testListAllVariants_sortDesc() {
        List<VariantDTO> list = variantService.listAllVariantsDTOs(null, null, "priceAmount", false);

        assertTrue(list.get(0).getPriceAmount() > list.get(1).getPriceAmount());
    }

    // -----------------------------
    // count
    // -----------------------------
    @Test
    void testCountVariants() {
        assertEquals(5, variantService.count());
    }

    // -----------------------------
    // findById
    // -----------------------------
    @Test
    void testFindById() {
        Variant v = Variant.find("sku", "SKU-1").firstResult();
        VariantDTO dto = variantService.findById(v.id);
        assertNotNull(dto);
        assertEquals(v.id, dto.getId());
    }

    // -----------------------------
    // listByProductId
    // -----------------------------
    @Test
    void testListByProductId_returnsOnlyRelatedVariants() {
        List<VariantDTO> list = variantService.listByProductId(productId, null, null, null, true);
        assertEquals(5, list.size());
    }

    @Test
    void testListByProductId_pagination() {
        List<VariantDTO> list = variantService.listByProductId(productId, 0, 2, "id", true);
        assertEquals(2, list.size());
    }

    @Test
    void testListByProductId_nullProductIdReturnsEmpty() {
        List<VariantDTO> list = variantService.listByProductId(null, null, null, null, true);
        assertTrue(list.isEmpty());
    }

    // -----------------------------
    // create
    // -----------------------------
    @Test
    @Transactional
    void testCreateVariant() {
        CreateProductVariant req = new CreateProductVariant(
                productId,
                "NEW-SKU",
                "{\"color\": \"red\"}",
                99,
                50,
                "BARCODE-NEW",
                true,
                10
        );

        Variant v = variantService.create(req);

        assertNotNull(v);
        assertEquals("NEW-SKU", v.sku);
        assertEquals(10, v.quantity);
        assertEquals(productId, v.product.id);
    }

    @Test
    @Transactional
    void testCreateVariantInvalidProductReturnsNull() {
        CreateProductVariant req = new CreateProductVariant(
                9999L,
                "BAD",
                null,
                10,
                null,
                null,
                true,
                5
        );

        Variant v = variantService.create(req);
        assertNull(v);
    }

    // -----------------------------
    // patch
    // -----------------------------
    @Test
    @Transactional
    void testPatchVariant() {
        Variant v = Variant.find("sku", "SKU-1").firstResult();

        Variant partial = new Variant();
        partial.sku = "UPDATED-SKU";
        partial.quantity = 999;

        boolean ok = variantService.patch(v.id, partial);
        assertTrue(ok);

        Variant updated = Variant.findById(v.id);
        assertEquals("UPDATED-SKU", updated.sku);
        assertEquals(999, updated.quantity);
    }

    @Test
    @Transactional
    void testPatchNonExistingReturnsFalse() {
        Variant partial = new Variant();
        partial.sku = "NEW";
        assertFalse(variantService.patch(9999L, partial));
    }

    // -----------------------------
    // delete
    // -----------------------------
    @Test
    @Transactional
    void testDeleteVariant() {
        Variant v = Variant.find("sku", "SKU-1").firstResult();
        boolean deleted = variantService.delete(v.id);
        assertTrue(deleted);
        assertNull(Variant.findById(v.id));
    }

    @Test
    @Transactional
    void testDeleteNonExisting() {
        assertFalse(variantService.delete(9999L));
    }

    // -----------------------------
    // updateStock
    // -----------------------------
    @Test
    @Transactional
    void testUpdateStock() {
        Variant v = Variant.find("sku", "SKU-2").firstResult();

        VariantDTO updated = variantService.updateStock(v.id, 777);

        assertNotNull(updated);
        assertEquals(777, updated.getQuantity());
    }

    @Test
    @Transactional
    void testUpdateStockVariantNotFoundReturnsNull() {
        VariantDTO dto = variantService.updateStock(9999L, 100);
        assertNull(dto);
    }
}
