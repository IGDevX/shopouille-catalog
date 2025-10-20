package org.shopouille.dto.request;

public record CreateProduct(
    String title, 
    String slug, 
    String descriptionHtml, 
    String seoTitle, 
    String seoDescription,
    Boolean isActive
) {}
