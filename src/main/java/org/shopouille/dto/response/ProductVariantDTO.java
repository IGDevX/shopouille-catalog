package org.shopouille.dto.response;

import org.shopouille.entity.Product;
import org.shopouille.entity.Variant;

import lombok.Data;

@Data
public class ProductVariantDTO {
    private Long id;

    private String title;
    private String slug;
    private String descriptionHtml;
    private String seoTitle;
    private String seoDescription;

    private String sku;
    private String attributesJson;
    private Integer priceAmount;
    private Integer quantity = 0;
    private Boolean isActive = true;

    public ProductVariantDTO() {
    }

    public static ProductVariantDTO from(Variant v) {
        if (v == null)
            return null;

        ProductVariantDTO dto = new ProductVariantDTO();

        dto.id = v.id;
        dto.sku = v.sku;
        dto.attributesJson = v.attributesJson;
        dto.priceAmount = v.priceAmount;
        dto.quantity = v.quantity;
        dto.isActive = v.isActive;

        Product p = v.product;
        if (p == null)
            return null;

        dto.title = p.title;
        dto.slug = p.slug;
        dto.descriptionHtml = p.descriptionHtml;
        dto.seoTitle = p.seoTitle;
        dto.seoDescription = p.seoDescription;

        return dto;
    }

}
