package org.example.backend.resources;

import org.example.backend.dto.ReactionRequest;
import org.example.backend.services.PublicNewsService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/public/news")
@Produces(MediaType.APPLICATION_JSON)
public class PublicNewsResource {

    private final PublicNewsService publicNewsService;

    @Inject
    public PublicNewsResource(PublicNewsService publicNewsService) {
        this.publicNewsService = publicNewsService;
    }

    @GET
    @Path("/latest")
    public Response latest() {
        return Response.ok(publicNewsService.findLatest()).build();
    }


    @GET
    @Path("/{id}")
    public Response findById(
            @PathParam("id") Long id,
            @HeaderParam("X-Session-Id") String sessionId
    ) {
        try {
            return Response.ok(
                    publicNewsService.findById(id, sessionId)
            ).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/most-read")
    public Response mostRead() {
        return Response.ok(publicNewsService.findMostRead()).build();
    }

    @GET
    @Path("/category/{categoryId}")
    public Response byCategory(
            @PathParam("categoryId") Long categoryId,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        return Response.ok(
                publicNewsService.findByCategory(categoryId, page, pageSize)
        ).build();
    }

    @GET
    @Path("/search")
    public Response search(
            @QueryParam("q") String query,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        try {
            return Response.ok(
                    publicNewsService.search(query, page, pageSize)
            ).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/tag/{tagName}")
    public Response byTag(
            @PathParam("tagName") String tagName,
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("pageSize") @DefaultValue("10") int pageSize
    ) {
        try {
            return Response.ok(
                    publicNewsService.findByTag(tagName, page, pageSize)
            ).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{id}/reaction")
    public Response reactToNews(
            @PathParam("id") Long id,
            ReactionRequest request,
            @HeaderParam("X-Session-Id") String sessionId
    ) {
        try {
            publicNewsService.reactToNews(id, sessionId, request.getReaction());

            return Response.ok("Reakcija sačuvana.").build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/{id}/reaction-stats")
    public Response getNewsReactionStats(@PathParam("id") Long id) {
        try {
            return Response.ok(
                    publicNewsService.getNewsReactionStats(id)
            ).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/most-reacted")
    public Response mostReacted() {
        return Response.ok(publicNewsService.findMostReacted()).build();
    }

    @GET
    @Path("/{id}/related")
    public Response relatedNews(
            @PathParam("id") Long id
    ) {
        return Response.ok(
                publicNewsService.findRelatedNews(id)
        ).build();
    }
}