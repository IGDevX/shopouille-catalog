package org.shopouille.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_variants")
public class ProductVariant extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id")
    public Product product;

    @Column(nullable = false, unique = true)
    public String sku;

    @Column(columnDefinition = "json", name = "attributes_json")
    public String attributesJson;

    @Column(nullable = false, name = "price_amount")
    public Integer priceAmount;

    @Column(name = "weight_grams")
    public Integer weightGrams;
    @Column
    public String barcode;

    @Column(nullable = false, name = "is_active")
    public Boolean isActive = true;

    @Column(nullable = false)
    public Integer quantity = 0;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    public Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    public Instant updatedAt;

    @OneToMany(mappedBy = "variant", cascade = CascadeType.ALL)
    public List<ProductCategory> productCategories;
}
