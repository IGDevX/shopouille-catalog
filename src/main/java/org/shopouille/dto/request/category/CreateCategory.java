package org.shopouille.dto.request.category;
import java.util.Optional;

public record CreateCategory(
    String name,
    String slug,
    Optional<Long> parent_id
) {

}
