package org.shopouille.dto.request;

public record UpdateProduct(
    String title, 
    String slug, 
    String descriptionHtml, 
    String seoTitle, 
    String seoDescription,
    Boolean isActive
) {}
