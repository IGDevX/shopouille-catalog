package org.shopouille.dto.response;

import lombok.Data;
import org.shopouille.entity.Variant;

@Data
public class VariantDTO {
    private Long id;
    private String sku;
    private String attributesJson;
    private Integer priceAmount;
    private Integer quantity = 0;
    private Boolean isActive = true;

    public VariantDTO() {
        // constructor empty because ...
    }

    public static VariantDTO from(Variant v) {
        if (v == null)
            return null;

        VariantDTO dto = new VariantDTO();

        dto.id = v.id;
        dto.sku = v.sku;
        dto.attributesJson = v.attributesJson;
        dto.priceAmount = v.priceAmount;
        dto.quantity = v.quantity;
        dto.isActive = v.isActive;

        return dto;
    }

}
