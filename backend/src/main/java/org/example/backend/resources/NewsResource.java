package org.example.backend.resources;

import org.example.backend.dto.CreateNewsRequest;
import org.example.backend.services.NewsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/cms/news")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NewsResource {

    private final NewsService newsService;

    @Context
    private ContainerRequestContext requestContext;

    @Inject
    public NewsResource(NewsService newsService) {
        this.newsService = newsService;
    }

    @GET
    public Response getAll() {
        return Response.ok(newsService.findAll()).build();
    }

    @POST
    public Response create(CreateNewsRequest request) {
        try {
            Long authorId = (Long) requestContext.getProperty("userId");

            return Response.ok(newsService.create(request, authorId)).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        try {
            Long userId = (Long) requestContext.getProperty("userId");
            String role = (String) requestContext.getProperty("role");

            newsService.delete(id, userId, role);

            return Response.ok("Vest obrisana.").build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, CreateNewsRequest request) {
        try {
            Long userId = (Long) requestContext.getProperty("userId");
            String role = (String) requestContext.getProperty("role");

            return Response.ok(newsService.update(id, request, userId, role)).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/search")
    public Response search(
            @QueryParam("q") String query,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        try {
            return Response.ok(newsService.search(query, page, pageSize)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}