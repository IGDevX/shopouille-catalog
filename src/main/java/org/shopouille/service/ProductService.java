package org.shopouille.service;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import org.shopouille.dto.request.CreateProduct;
import org.shopouille.dto.response.ProductDTO;
import org.shopouille.entity.Product;

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

    public List<ProductDTO> searchProducts(String searchQuery, Long categoryId, Integer pageIndex, Integer pageSize, String sortField, boolean asc) {
        String field = (sortField != null && ALLOWED_SORT.contains(sortField)) ? sortField : "id";
        Sort sort = asc ? Sort.by(field).ascending() : Sort.by(field).descending();

        if (pageIndex == null)
            pageIndex = 0;
        if (pageSize == null || pageSize <= 0)
            pageSize = 20;

        PanacheQuery<Product> query = buildSearchQuery(searchQuery, categoryId, sort);

        return query.page(pageIndex, pageSize)
                .list()
                .stream()
                .map(ProductDTO::from)
                .toList();
    }

    public long countSearchResults(String searchQuery, Long categoryId) {
        return buildSearchQuery(searchQuery, categoryId, null).count();
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

    private PanacheQuery<Product> buildSearchQuery(String searchQuery, Long categoryId, Sort sort) {
        String term = searchQuery != null ? searchQuery : "";
        String searchPattern = "%" + term + "%";

        String basePredicate = "(LOWER(p.title) LIKE LOWER(?1) OR LOWER(p.slug) LIKE LOWER(?1) OR LOWER(p.descriptionHtml) LIKE LOWER(?1) "
                + "OR LOWER(p.seoTitle) LIKE LOWER(?1) OR LOWER(p.seoDescription) LIKE LOWER(?1))";

        if (categoryId != null) {
            String predicate = basePredicate
                    + " AND EXISTS (SELECT 1 FROM Variant v JOIN v.productCategories pc WHERE v.product = p AND pc.category.id = ?2)";
            if (sort != null) {
                return Product.find("SELECT p FROM Product p WHERE " + predicate, sort, searchPattern, categoryId);
            }
            return Product.find("SELECT p FROM Product p WHERE " + predicate, searchPattern, categoryId);
        }

        if (sort != null) {
            return Product.find("SELECT p FROM Product p WHERE " + basePredicate, sort, searchPattern);
        }
        return Product.find("SELECT p FROM Product p WHERE " + basePredicate, searchPattern);
    }
}
