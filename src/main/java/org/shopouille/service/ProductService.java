package org.shopouille.service;

import java.util.List;
import java.util.Set;

import org.shopouille.dto.request.CreateProduct;
import org.shopouille.dto.response.ProductDTO;
import org.shopouille.entity.Product;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ProductService {

    private static final Set<String> ALLOWED_SORT = Set.of("id", "title", "slug", "publishedAt", "isActive", "seoTitle",
            "seoDescription");

    public List<ProductDTO> listAllProductsDTOs(Integer pageIndex, Integer pageSize, String sortField, boolean asc) {
        String field = (sortField != null && ALLOWED_SORT.contains(sortField)) ? sortField : "id";
        Sort sort = asc ? Sort.by(field).ascending() : Sort.by(field).descending();

        if (pageIndex == null)
            pageIndex = 0;
        if (pageSize == null || pageSize <= 0) {
            return Product.findAll(sort).list()
                    .stream()
                    .map(p -> ProductDTO.from((Product) p))
                    .toList();
        }
        return Product.findAll(sort).page(pageIndex, pageSize).list()
                .stream()
                .map(p -> ProductDTO.from((Product) p))
                .toList();
    }

    public long count() {
        return Product.count();
    }

    public Product findById(Long id) {
        return Product.findById(id);
    }

    @Transactional
    public Product create(CreateProduct createProduct) {
        Product product = new Product();
        product.title = createProduct.title();
        product.slug = createProduct.slug();
        product.descriptionHtml = createProduct.descriptionHtml();
        product.seoTitle = createProduct.seoTitle();
        product.seoDescription = createProduct.seoDescription();
        product.persist();
        return product;
    }

    @Transactional
    public boolean delete(Long id) {
        return Product.deleteById(id);
    }

    @Transactional
    public boolean patch(Long id, Product partial) {
        Product existing = Product.findById(id);
        if (existing == null)
            return false;
        if (partial.title != null)
            existing.title = partial.title;
        if (partial.slug != null)
            existing.slug = partial.slug;
        if (partial.descriptionHtml != null)
            existing.descriptionHtml = partial.descriptionHtml;
        if (partial.seoTitle != null)
            existing.seoTitle = partial.seoTitle;
        if (partial.seoDescription != null)
            existing.seoDescription = partial.seoDescription;
        if (partial.publishedAt != null)
            existing.publishedAt = partial.publishedAt;
        return true;
    }
}
