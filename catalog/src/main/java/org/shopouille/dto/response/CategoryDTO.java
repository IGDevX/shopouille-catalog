package org.shopouille.dto.response;

import org.shopouille.entity.Category;

import lombok.Getter;

@Getter
public class CategoryDTO {

    private Long id;
    private String name;
    private String slug;
    private Long parentId;

    public CategoryDTO(Long id, String name, String slug, Long parentId) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.parentId = parentId;
    }

    public static CategoryDTO fromEntity(Category category) {
        Long parentId = category.getParent() != null ? category.getParent().getId() : null;
        return new CategoryDTO(category.getId(), category.getName(), category.getSlug(), parentId);
    }
}
