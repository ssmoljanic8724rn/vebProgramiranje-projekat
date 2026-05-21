package org.example.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.backend.filters.CorsFilter;
import org.example.backend.repositories.*;
import org.example.backend.repositories.impl.*;
import org.example.backend.services.*;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
public class HelloApplication extends ResourceConfig {

    public HelloApplication(){

        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);


        AbstractBinder binder = new AbstractBinder() {
            @Override
            protected void configure() {

                this.bind(UserRepositoryImpl.class).to(UserRepository.class).in(Singleton.class);
                this.bind(CategoryRepositoryImpl.class).to(CategoryRepository.class).in(Singleton.class);
                this.bind(NewsRepositoryImpl.class).to(NewsRepository.class).in(Singleton.class);
                this.bind(PublicNewsRepositoryImpl.class).to(PublicNewsRepository.class).in(Singleton.class);
                this.bind(CommentRepositoryImpl.class).to(CommentRepository.class).in(Singleton.class);

                this.bindAsContract(UserService.class);
                this.bindAsContract(AuthService.class);
                this.bindAsContract(CategoryService.class);
                this.bindAsContract(NewsService.class);
                this.bindAsContract(PublicNewsService.class);
                this.bindAsContract(CommentService.class);

            }
        };

        ObjectMapper mapper = new ObjectMapper();

        mapper.registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JacksonJaxbJsonProvider provider =
                new JacksonJaxbJsonProvider();

        provider.setMapper(mapper);

        register(provider);

        System.out.println("Registered classes: " + getClasses());
        System.out.println("Registered instances: " + getInstances());

        register(binder);
        packages("org.example.backend");
    }

}