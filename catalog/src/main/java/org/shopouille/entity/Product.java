package org.shopouille.entity;

import java.time.Instant;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "products")
public class Product extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public String title;

    @Column(nullable = false, unique = true)
    public String slug;

    @Column(columnDefinition = "TEXT", name = "description_html")
    public String descriptionHtml;

    @Column(name = "seo_title")
    public String seoTitle;

    @Column(columnDefinition = "TEXT", name = "seo_description")
    public String seoDescription;

    @Column(nullable = false, name = "is_active")
    public Boolean isActive;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    public Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false, name = "updated_at")
    public Instant updatedAt;

    @Column(name = "published_at")
    public Instant publishedAt;
}
    