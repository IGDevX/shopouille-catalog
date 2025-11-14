package org.shopouille.dto.response;

import lombok.Getter;
import org.shopouille.entity.Category;

import io.quarkus.runtime.annotations.RegisterForReflection;

@Getter
@RegisterForReflection
public class CategoryDTO {

    private Long id;
    private String name;
    private Long parentId;

    public CategoryDTO(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    public static CategoryDTO fromEntity(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), parentId);
    }
}
