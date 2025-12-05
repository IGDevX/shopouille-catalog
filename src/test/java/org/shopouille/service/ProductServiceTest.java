package org.shopouille.service;

import static org.junit.jupiter.api.Assertions.*;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shopouille.dto.request.CreateProduct;
import org.shopouille.dto.response.ProductDTO;
import org.shopouille.entity.Product;

@QuarkusTest
class ProductServiceTest {

    @Inject
    ProductService productService;

    Product existingProduct;

    @BeforeEach
    @Transactional
    void setUp() {
        Product.deleteAll(); // reset table

        existingProduct = new Product();
        existingProduct.title = "Test Product";
        existingProduct.slug = "test-product";
        existingProduct.descriptionHtml = "Description";
        existingProduct.seoTitle = "SEO Test";
        existingProduct.seoDescription = "SEO Desc";
        existingProduct.persist();
    }

    @Test
    @Transactional
    void testCreateProduct() {
        CreateProduct create = new CreateProduct(
                "New Product",
                "new-product",
                "New desc",
                "SEO New",
                "SEO New Desc"
        );

        Product created = productService.create(create);
        assertNotNull(created.id);
        assertEquals("New Product", created.title);
    }


    @Test
    @Transactional
    void testPatchProduct() {
        Product partial = new Product();
        partial.title = "Updated Title";
        partial.descriptionHtml = "Updated Description";

        boolean success = productService.patch(existingProduct.id, partial);
        assertTrue(success);

        Product updated = Product.findById(existingProduct.id);
        assertEquals("Updated Title", updated.title);
        assertEquals("Updated Description", updated.descriptionHtml);
        assertEquals(existingProduct.seoTitle, updated.seoTitle); // unchanged
    }

    @Test
    @Transactional
    void testDeleteProduct() {
        boolean deleted = productService.delete(existingProduct.id);
        assertTrue(deleted);

        Product found = Product.findById(existingProduct.id);
        assertNull(found);
    }

    @Test
    @Transactional
    void testListAllProductsDTOs() {
        List<ProductDTO> products = productService.listAllProductsDTOs(0, 10, "title", true);
        assertFalse(products.isEmpty());
        assertEquals(existingProduct.title, products.get(0).getTitle());
    }

    @Test
    @Transactional
    void testSearchProducts() {
        List<ProductDTO> results = productService.searchProducts("Test", null, 0, 10, "title", true);
        assertFalse(results.isEmpty());
        assertEquals(existingProduct.title, results.get(0).getTitle());

        long count = productService.countSearchResults("Test", null);
        assertEquals(1, count);
    }

    @Test
    @Transactional
    void testCountProducts() {
        long count = productService.count();
        assertEquals(1, count);
    }

    @Test
    @Transactional
    void testFindById() {
        Product found = productService.findById(existingProduct.id);
        assertNotNull(found);
        assertEquals(existingProduct.title, found.title);
    }
}
