package org.example.backend.resources;

import org.example.backend.dto.CreateCommentRequest;
import org.example.backend.dto.ReactionRequest;
import org.example.backend.services.CommentService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path("/public/news/{newsId}/comments")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CommentResource {

    private final CommentService commentService;

    @Inject
    public CommentResource(CommentService commentService) {
        this.commentService = commentService;
    }

    @GET
    public Response getByNewsId(@PathParam("newsId") Long newsId) {
        return Response.ok(commentService.findByNewsId(newsId)).build();
    }

    @POST
    public Response create(
            @PathParam("newsId") Long newsId,
            CreateCommentRequest request
    ) {
        try {
            return Response.ok(commentService.create(newsId, request)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    @Path("/{commentId}/reaction")
    public Response reactToComment(
            @PathParam("commentId") Long commentId,
            ReactionRequest request,
            @CookieParam("SESSION_ID") String sessionId
    ) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }

            commentService.reactToComment(commentId, sessionId, request.getReaction());

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
    @Path("/{commentId}/reaction-stats")
    public Response getCommentReactionStats(@PathParam("commentId") Long commentId) {
        try {
            return Response.ok(
                    commentService.getCommentReactionStats(commentId)
            ).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        }
    }
}