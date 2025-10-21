package org.shopouille.dto.request.category;

public record ModifyCategory(
    Long id,
    String name,
    String slug,
    Long parent_id
) {
}
