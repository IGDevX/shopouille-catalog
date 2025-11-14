package org.shopouille.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.shopouille.dto.request.CreateProductVariant;
import org.shopouille.dto.request.UpdateProductVariant;
import org.shopouille.dto.request.UpdateStock;
import org.shopouille.dto.response.VariantDTO;
import org.shopouille.entity.Variant;
import org.shopouille.service.VariantService;

@Path("/variant")
public class VariantController {

    private final VariantService variantService;

    public VariantController(VariantService variantService) {
        this.variantService = variantService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedVariants(
            @DefaultValue("0") @QueryParam("pageIndex") Integer pageIndex,
            @DefaultValue("-1") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("id") @QueryParam("_sort") String sortField,
            @DefaultValue("asc") @QueryParam("_order") String order) {

        boolean asc = "asc".equalsIgnoreCase(order);

        List<VariantDTO> products = variantService.listAllVariantsDTOs(pageIndex, pageSize, sortField, asc);
        long total = variantService.count();

        return Response.ok(products).header("X-Total-Count", total).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{variantId}")
    public Response getVariant(@NotBlank @PathParam("variantId") Long variantId) {
        VariantDTO variant = variantService.findById(variantId);

        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Auncuns variants trouvés").build();
        }

        return Response.ok(variant).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createVariant(CreateProductVariant dto) {
        // Prevent duplicate SKU
        if (dto.sku() != null && Variant.find("sku", dto.sku()).firstResult() != null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("SKU déjà utilisé").build();
        }

        Variant created = variantService.create(dto);
        if (created == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Produit inexistant pour l'ID " + dto.productId())
                    .build();
        }

        return Response.status(Response.Status.CREATED).entity(VariantDTO.from(created)).build();
    }

    @PATCH
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateVariant(@PathParam("id") Long id, UpdateProductVariant dto) {
        // Build a partial Variant to apply
        Variant partial = new Variant();
        if (dto.sku() != null)
            partial.sku = dto.sku();
        if (dto.attributesJson() != null)
            partial.attributesJson = dto.attributesJson();
        if (dto.priceAmount() != null)
            partial.priceAmount = dto.priceAmount();
        if (dto.weightGrams() != null)
            partial.weightGrams = dto.weightGrams();
        if (dto.barcode() != null)
            partial.barcode = dto.barcode();
        if (dto.isActive() != null)
            partial.isActive = dto.isActive();
        if (dto.quantity() != null)
            partial.quantity = dto.quantity();

        boolean ok = variantService.patch(id, partial);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        VariantDTO updated = variantService.findById(id);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response deleteVariant(@PathParam("id") Long id) {
        boolean deleted = variantService.delete(id);
        return deleted ? Response.noContent().build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @GET
    @Path("/{id}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStock(@PathParam("id") Long id) {
        VariantDTO variant = variantService.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(variant.getQuantity()).build();
    }

    @GET
    @Path("/product")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVariantsByProduct(@QueryParam("productId") Long productId,
            @DefaultValue("0") @QueryParam("pageIndex") Integer pageIndex,
            @DefaultValue("-1") @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("id") @QueryParam("_sort") String sortField,
            @DefaultValue("asc") @QueryParam("_order") String order) {

        boolean asc = "asc".equalsIgnoreCase(order);
        List<VariantDTO> variants = variantService.listByProductId(productId, pageIndex, pageSize, sortField,
                asc);
        return Response.ok(variants).build();
    }

    @PATCH
    @Path("/{id}/stock")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateStock(@PathParam("id") Long id, UpdateStock updateStock) {
        VariantDTO variant = variantService.findById(id);
        if (variant == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (updateStock.stock() < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Stock invalide").build();
        }
        variant.setQuantity(updateStock.stock());
        return Response.ok(variant).build();
    }
}
