package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, name = "user_id")
    public Long userId;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    public Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    public Instant updatedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    public List<CartItem> items;
}
