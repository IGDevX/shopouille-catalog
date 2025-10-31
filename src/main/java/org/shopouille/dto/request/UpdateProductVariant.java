package org.shopouille.dto.request;

public record UpdateProductVariant(
        String sku,
        String attributesJson,
        Integer priceAmount,
        Integer weightGrams,
        String barcode,
        Boolean isActive,
        Integer quantity
) {}
