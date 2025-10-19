package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    public Category parent;

    @OneToMany(mappedBy = "parent")
    public List<Category> children;

    @Column(nullable = false)
    public String name;

    @Column(nullable = false, unique = true)
    public String slug;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    public Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    public Instant updatedAt;
}
