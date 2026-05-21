package org.example.backend.resources;

import org.example.backend.services.CategoryService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/public/categories")
@Produces(MediaType.APPLICATION_JSON)
public class PublicCategoryResource {

    private final CategoryService categoryService;

    @Inject
    public PublicCategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GET
    public Response getAll() {
        return Response.ok(categoryService.findAll()).build();
    }
}