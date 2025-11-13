package org.shopouille.dto.request;

public record CreateProductVariant(
                Long productId,
                String sku,
                String attributesJson,
                Integer priceAmount,
                Integer weightGrams,
                String barcode,
                Boolean isActive,
                Integer quantity) {
}
