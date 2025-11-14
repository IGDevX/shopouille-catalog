package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_categories")
public class ProductCategory extends PanacheEntityBase {

    @EmbeddedId
    public ProductCategoryId id;

    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    public Variant variant;

    @ManyToOne
    @MapsId("categoryId")
    @JoinColumn(name = "categories_id")
    public Category category;
}
