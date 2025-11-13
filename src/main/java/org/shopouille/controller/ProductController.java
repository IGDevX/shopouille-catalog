package org.shopouille.controller;

import java.util.List;

import org.jboss.logging.Logger;
import org.shopouille.dto.request.CreateProduct;
import org.shopouille.dto.response.ProductDTO;
import org.shopouille.entity.Product;
import org.shopouille.service.ProductService;

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

@Path("/product")
public class ProductController {

    private final Logger logger = Logger.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPaginatedProducts(
            @DefaultValue("0") @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize,
            @DefaultValue("id") @QueryParam("_sort") String sortField,
            @DefaultValue("asc") @QueryParam("_order") String order) {

        boolean asc = "asc".equalsIgnoreCase(order);

        List<ProductDTO> products = productService.listAllProductsDTOs(pageIndex, pageSize, sortField, asc);
        long total = productService.count();

        return Response.ok(products).header("X-Total-Count", total).build();
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProduct(@PathParam("id") Long id) {
        Product product = productService.findById(id);
        return product != null ? Response.ok(product).build() : Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public Response createProduct(CreateProduct createProduct) {
        Product product = productService.create(createProduct);
        return product.id != null ? Response.ok(product.id).build()
                : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchProduct(@PathParam("id") Long id, Product partial) {
        boolean ok = productService.patch(id, partial);
        if (!ok) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        Product updated = productService.findById(id);
        return Response.ok(ProductDTO.from(updated)).build();
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response deleteProduct(@PathParam("id") Long id) {
        boolean deleted = productService.delete(id);
        return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
    }
}
