package org.shopouille.controller;

import java.util.Collection;

import org.shopouille.dto.request.CreateProduct;
import org.shopouille.entity.Product;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/product")
public class ProductRessource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Product> getPaginatedProducts() {
        return Product.findAll().page(0,25).list();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response deleteProduct(@PathParam("id") Long id) {
        boolean deleted = Product.deleteById(id);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(@PathParam("id") Long id) {
        Product product = Product.findById(id);
        return product != null ? Response.ok(product).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Transactional
    public Response createProduct(CreateProduct createProduct) {
        Product product = new Product();
        product.title = createProduct.title();
        product.slug = createProduct.slug();
        product.descriptionHtml = createProduct.descriptionHtml();
        product.seoTitle = createProduct.seoTitle();
        product.seoDescription = createProduct.seoDescription();
        product.isActive = createProduct.isActive();
        product.persist();
        return product.id != null ? Response.ok().build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response patchProduct(@PathParam("id") Long id, Product partial) {
        Product existing = Product.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (partial.title != null) existing.title = partial.title;
        if (partial.slug != null) existing.slug = partial.slug;
        if (partial.descriptionHtml != null) existing.descriptionHtml = partial.descriptionHtml;
        if (partial.seoTitle != null) existing.seoTitle = partial.seoTitle;
        if (partial.seoDescription != null) existing.seoDescription = partial.seoDescription;
        if (partial.isActive != null) existing.isActive = partial.isActive;
        if (partial.publishedAt != null) existing.publishedAt = partial.publishedAt;
        return Response.ok().build();
    }

    
}
