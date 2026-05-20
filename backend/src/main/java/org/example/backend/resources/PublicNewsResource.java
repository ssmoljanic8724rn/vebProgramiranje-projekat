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
            @CookieParam("SESSION_ID") String sessionId
    ) {
        try {

            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            return Response.ok(
                    publicNewsService.findById(id, sessionId)
            ).cookie(
                    new NewCookie("SESSION_ID", sessionId)
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
            @CookieParam("SESSION_ID") String sessionId
    ) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            publicNewsService.reactToNews(id, sessionId, request.getReaction());

            return Response.ok("Reakcija sačuvana.")
                    .cookie(new NewCookie("SESSION_ID", sessionId))
                    .build();

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
}