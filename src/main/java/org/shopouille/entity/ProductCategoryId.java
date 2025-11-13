package org.shopouille.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ProductCategoryId {
    @Column(name = "variant_id")
    public Long variantId;

    @Column(name = "category_id")
    public Long categoryId;
}
