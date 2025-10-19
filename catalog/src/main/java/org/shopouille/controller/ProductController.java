package org.shopouille.controller;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;

@Path("/product")
public class ProductController {
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getPaginatedProducts() {
        return "Paginated products";
    }
}
