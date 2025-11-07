package org.shopouille.controller;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.shopouille.dto.request.CreateProductVariant;
import org.shopouille.dto.request.UpdateProductVariant;
import org.shopouille.dto.request.UpdateStock;
import org.shopouille.entity.Product;
import org.shopouille.entity.ProductVariant;

import java.util.List;
import java.util.Set;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

@Path("/variant")
public class ProductVariantRessource {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVariant(@PathParam("id") Long id) {
        ProductVariant variant = ProductVariant.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(variant).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVariants() {
        Sort sort = Sort.by("id").ascending(); // tri par id croissant
        List<ProductVariant> variants = ProductVariant.findAll(sort).list();
        long total = ProductVariant.count();
        return Response.ok(variants).header("X-Total-Count", total).build();
    }

    @GET
    @Path("/by-product/{productId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVariantsByProduct(@PathParam("productId") Long productId) {
        List<ProductVariant> variants = ProductVariant.find("product.id", productId).list();
        long total = variants.size();
        return Response.ok(variants).header("X-Total-Count", total).build();
    }



    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createVariant(CreateProductVariant dto) {
        Product product = Product.findById(dto.productId());
        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Produit inexistant pour l'ID " + dto.productId())
                    .build();
        }
        if (ProductVariant.find("sku", dto.sku()).firstResult() != null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("SKU déjà utilisé")
                    .build();
        }

        ProductVariant variant = new ProductVariant();
        variant.product = product;
        variant.sku = dto.sku();
        variant.attributesJson = dto.attributesJson();
        variant.priceAmount = dto.priceAmount();
        variant.weightGrams = dto.weightGrams();
        variant.barcode = dto.barcode();
        variant.isActive = dto.isActive() != null ? dto.isActive() : true;
        variant.quantity = dto.quantity() != null ? dto.quantity() : 0;

        variant.persist();

        return Response.status(Response.Status.CREATED).entity(variant).build();
    }


    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateVariant(@PathParam("id") Long id, UpdateProductVariant dto) {
        ProductVariant variant = ProductVariant.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (dto.sku() != null) variant.sku = dto.sku();
        if (dto.attributesJson() != null) variant.attributesJson = dto.attributesJson();
        if (dto.priceAmount() != null) variant.priceAmount = dto.priceAmount();
        if (dto.weightGrams() != null) variant.weightGrams = dto.weightGrams();
        if (dto.barcode() != null) variant.barcode = dto.barcode();
        if (dto.isActive() != null) variant.isActive = dto.isActive();
        if (dto.quantity() != null) variant.quantity = dto.quantity();

        return Response.ok(variant).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteVariant(@PathParam("id") Long id) {
        boolean deleted = ProductVariant.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }


    @GET
    @Path("/{id}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStock(@PathParam("id") Long id) {
        ProductVariant variant = ProductVariant.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(variant.quantity).build();
    }

    @PATCH
    @Path("/{id}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateStock(@PathParam("id") Long id, UpdateStock updateStock) {
        ProductVariant variant = ProductVariant.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updateStock.stock() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Stock invalide").build();
        }
        variant.quantity = updateStock.stock();
        return Response.ok(variant).build();
    }
}
