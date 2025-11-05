package org.shopouille.controller;

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
import org.shopouille.dto.request.category.CreateCategory;
import org.shopouille.dto.request.category.ModifyCategory;
import org.shopouille.dto.response.CategoryDTO;
import org.shopouille.entity.Category;

@Path("/category")
public class CategoryRessource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		return Response
		.ok(Category
			.findAll()
			.list()
			.stream()
			.map(Category.class::cast)
			.map(CategoryDTO::fromEntity)
			.toList()).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getById(@PathParam("id") Long id) {
		Category c = Category.findById(id);
		return c != null ? Response.ok(c).build() : Response.status(Response.Status.NOT_FOUND).build();
	}

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response create(CreateCategory incoming) {
		Category c = new Category();
		c.name = incoming.name();
		c.slug = incoming.slug();
        if (incoming.parent_id().isPresent()){
            Category parent = Category.findById(incoming.parent_id().get());
            c.parent = parent;
        }
		c.persist();
		return c.id != null ? Response.ok().build() : Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	}

	@PATCH
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response patch(@PathParam("id") Long id, ModifyCategory partial) {
		Category existing = Category.findById(id);
		if (existing == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
        if (partial.name() != null) {
            existing.name = partial.name();
        }
        if (partial.slug() != null) {
            existing.slug = partial.slug();
        }
        if (partial.parent_id() != null) {
            existing.parent = Category.findById(partial.parent_id());
        }
		return Response.ok().build();
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response delete(@PathParam("id") Long id) {
		Category existing = Category.findById(id);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        existing.getChildren().forEach(child -> child.parent = null);
		boolean deleted = Category.deleteById(id);
		return deleted ? Response.ok().build() : Response.status(Response.Status.NOT_FOUND).build();
	}

}
