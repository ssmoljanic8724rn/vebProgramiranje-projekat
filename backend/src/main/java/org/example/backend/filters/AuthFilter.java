package org.example.backend.filters;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.backend.services.UserService;
import org.example.backend.util.JwtUtil;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    private UserService userService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (!isAuthRequired(requestContext)) {
            return;
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Nedostaje Authorization token.")
                            .build()
            );
            return;
        }

        String token = authHeader.replace("Bearer ", "");

        if (!userService.isAuthorized(token)) {
            requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity("Token nije validan.")
                            .build()
            );
            return;
        }

        DecodedJWT jwt = JwtUtil.verifyToken(token);

        requestContext.setProperty("userId", Long.parseLong(jwt.getSubject()));
        requestContext.setProperty("email", jwt.getClaim("email").asString());
        requestContext.setProperty("role", jwt.getClaim("role").asString());
    }

    private boolean isAuthRequired(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();

        return path.startsWith("cms");
    }
}