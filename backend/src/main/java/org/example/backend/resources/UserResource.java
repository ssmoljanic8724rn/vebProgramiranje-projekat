package org.example.backend.resources;

import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.UpdateUserRequest;
import org.example.backend.entities.User;
import org.example.backend.services.UserService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/cms/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    UserService userService;

    @Context
    private ContainerRequestContext requestContext;

    @GET
    @Path("/by-email")
    public Response findByEmail(@QueryParam("email") String email) {
        User user = userService.findByEmail(email);

        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("User not found")
                    .build();
        }

        return Response.ok(user).build();
    }

    @GET
    public Response getAll() {
        try {
            String role = (String) requestContext.getProperty("role");
            return Response.ok(userService.findAll(role)).build();
        } catch (RuntimeException e) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @POST
    public Response create(CreateUserRequest request) {
        try {
            String role = (String) requestContext.getProperty("role");

            return Response.ok(
                    userService.create(request, role)
            ).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(
            @PathParam("id") Long id,
            UpdateUserRequest request
    ) {
        try {

            String role =
                    (String) requestContext.getProperty("role");

            return Response.ok(
                    userService.update(id, request, role)
            ).build();

        } catch (RuntimeException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }

    @PATCH
    @Path("/{id}/active")
    public Response changeActiveStatus(
            @PathParam("id") Long id,
            @QueryParam("active") boolean active
    ) {
        try {

            String role =
                    (String) requestContext.getProperty("role");

            userService.changeActiveStatus(id, active, role);

            return Response.ok("Status uspešno promenjen.").build();

        } catch (RuntimeException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }
    }
}