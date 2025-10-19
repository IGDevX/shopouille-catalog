package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

@Entity
@Table(name = "product_categories")
public class ProductCategory extends PanacheEntityBase {

    @EmbeddedId
    public ProductCategoryId id;

    @ManyToOne
    @MapsId("variantId")
    @JoinColumn(name = "variant_id")
    public ProductVariant variant;

    @ManyToOne
    @MapsId("mediaId")
    @JoinColumn(name = "media_id")
    public MediaLibrary media;

    @Column(nullable = false, name = "sort_order")
    public Integer sortOrder = 0;
}

@Embeddable
class ProductCategoryId {
    public Long variantId;
    public Long mediaId;

    // equals() / hashCode() à générer
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductCategoryId)) return false;
        ProductCategoryId that = (ProductCategoryId) o;
        return variantId.equals(that.variantId) && mediaId.equals(that.mediaId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(variantId, mediaId);
    }
}


