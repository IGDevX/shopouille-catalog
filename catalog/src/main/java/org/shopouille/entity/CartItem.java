package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItem extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Integer quantity = 1;

    @Column(nullable = false, name = "price_amount")
    public Integer priceAmount;

    @Column(length = 3, nullable = false, name = "price_currency")
    public String priceCurrency;

    @ManyToOne(optional = false)
    @JoinColumn(name = "variant_id")
    public ProductVariant variant;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id")
    public Cart cart;
}

