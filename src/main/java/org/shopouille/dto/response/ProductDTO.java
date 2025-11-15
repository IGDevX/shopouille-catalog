package org.shopouille.dto.response;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;
import org.shopouille.entity.Product;

@Data
@RegisterForReflection
public class ProductDTO {
    private Long id;
    private String title;
    private String slug;
    private String descriptionHtml;
    private String seoTitle;
    private String seoDescription;

    public ProductDTO() {
    }

    public static ProductDTO from(Product p) {
        if (p == null)
            return null;

        ProductDTO dto = new ProductDTO();
        dto.id = p.id;
        dto.title = p.title;
        dto.slug = p.slug;
        dto.descriptionHtml = p.descriptionHtml;
        dto.seoTitle = p.seoTitle;
        dto.seoDescription = p.seoDescription;

        return dto;
    }
}
