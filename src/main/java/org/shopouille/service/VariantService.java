package org.shopouille.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.shopouille.dto.request.CreateProductVariant;
import org.shopouille.dto.response.VariantDTO;
import org.shopouille.entity.Product;
import org.shopouille.entity.Variant;

import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class VariantService {

    private static final Set<String> ALLOWED_SORT = Set.of("id", "sku", "priceAmount", "quantity", "isActive");

    public List<VariantDTO> listAllVariantsDTOs(Integer pageIndex, Integer pageSize, String sortField,
            boolean asc) {
        String field = (sortField != null && ALLOWED_SORT.contains(sortField)) ? sortField : "id";
        Sort sort = asc ? Sort.by(field).ascending() : Sort.by(field).descending();

        if (pageIndex == null)
            pageIndex = 0;
        if (pageSize == null || pageSize <= 0) {
            return Variant.findAll(sort).list()
                    .stream()
                    .map(v -> VariantDTO.from((Variant) v))
                    .toList();
        }
        return Variant.findAll(sort).page(pageIndex, pageSize).list()
                .stream()
                .map(v -> VariantDTO.from((Variant) v))
                .toList();
    }

    public long count() {
        return Variant.count();
    }

    public VariantDTO findById(Long id) {
        Variant variant = Variant.findById(id);
        return VariantDTO.from(variant);
    }

    public List<VariantDTO> listByProductId(Long productId, Integer pageIndex, Integer pageSize,
            String sortField,
            boolean asc) {
        if (productId == null) {
            return List.of();
        }

        String field = (sortField != null && ALLOWED_SORT.contains(sortField)) ? sortField : "id";
        Sort sort = asc ? Sort.by(field).ascending() : Sort.by(field).descending();

        if (pageIndex == null)
            pageIndex = 0;
        if (pageSize == null || pageSize <= 0) {
            return Variant.find("product.id = ?1", sort, productId).list()
                    .stream()
                    .map(v -> VariantDTO.from((Variant) v))
                    .toList();
        }

        return Variant.find("product.id = ?1", sort, productId).page(pageIndex, pageSize).list()
                .stream()
                .map(v -> VariantDTO.from((Variant) v))
                .toList();
    }

    @Transactional
    public Variant create(CreateProductVariant create) {
        Product product = Product.findById(create.productId());
        if (product == null)
            return null;

        Variant variant = new Variant();
        variant.product = product;
        variant.sku = create.sku();
        variant.attributesJson = create.attributesJson();
        variant.priceAmount = create.priceAmount();
        variant.weightGrams = create.weightGrams();
        variant.barcode = create.barcode();
        variant.isActive = Objects.requireNonNullElse(create.isActive(), Boolean.TRUE);
        variant.quantity = create.quantity() != null ? create.quantity() : 0;
        variant.persist();
        return variant;
    }

    @Transactional
    public boolean delete(Long id) {
        return Variant.deleteById(id);
    }

    @Transactional
    public boolean patch(Long id, Variant partial) {
        Variant existing = Variant.findById(id);
        if (existing == null)
            return false;
        if (partial.sku != null)
            existing.sku = partial.sku;
        if (partial.attributesJson != null)
            existing.attributesJson = partial.attributesJson;
        if (partial.priceAmount != null)
            existing.priceAmount = partial.priceAmount;
        if (partial.weightGrams != null)
            existing.weightGrams = partial.weightGrams;
        if (partial.barcode != null)
            existing.barcode = partial.barcode;
        if (partial.isActive != null)
            existing.isActive = partial.isActive;
        if (partial.quantity != null)
            existing.quantity = partial.quantity;
        return true;
    }
}
