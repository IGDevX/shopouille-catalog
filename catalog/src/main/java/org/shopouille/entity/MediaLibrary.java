package org.shopouille.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "media_library")
public class MediaLibrary extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(length = 512, nullable = false)
    public String url;

    @Column
    public String alt;

    @Column(name = "size_bytes")
    public Long sizeBytes;

    @Column(name = "width_px")
    public Integer widthPx;
    @Column(name = "height_px")
    public Integer heightPx;

    @CreationTimestamp
    @Column(nullable = false, name = "created_at")
    public Instant createdAt;
}
