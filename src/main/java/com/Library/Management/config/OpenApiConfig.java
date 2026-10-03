package com.Library.Management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String BASIC_AUTH = "basicAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Management API")
                        .version("v1")
                        .description("""
                                To call secured endpoints from Swagger UI:
                                1. Click **Authorize**.
                                2. Enter your **email** as the username and your password.
                                3. Try a protected endpoint (for example GET /api/users/me).

                                You can also use POST /login (form fields). That sets a session cookie
                                for later requests from this same Swagger tab.
                                """))
                .components(new Components()
                        .addSecuritySchemes(BASIC_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Use your account email as the username.")))
                .addSecurityItem(new SecurityRequirement().addList(BASIC_AUTH));
    }

    @Bean
    public OpenApiCustomizer authEndpointsCustomizer() {
        return openApi -> {
            openApi.path("/login", new PathItem().post(new Operation()
                    .addTagsItem("Authentication")
                    .summary("Login (session cookie)")
                    .description("Form login handled by Spring Security. Success returns 200 and Set-Cookie.")
                    .security(List.of())
                    .requestBody(new RequestBody()
                            .required(true)
                            .content(new Content().addMediaType(
                                    "application/x-www-form-urlencoded",
                                    new MediaType().schema(new Schema<>()
                                            .addProperty("email", new StringSchema())
                                            .addProperty("password", new PasswordSchema())
                                            .required(List.of("email", "password"))))))
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Logged in"))
                            .addApiResponse("401", new ApiResponse().description("Invalid credentials")))));

            openApi.path("/logout", new PathItem().post(new Operation()
                    .addTagsItem("Authentication")
                    .summary("Logout")
                    .security(List.of())
                    .responses(new ApiResponses()
                            .addApiResponse("200", new ApiResponse().description("Logged out")))));

            if (openApi.getPaths() != null && openApi.getPaths().get("/register") != null) {
                openApi.getPaths().get("/register").readOperations()
                        .forEach(operation -> operation.setSecurity(List.of()));
            }
        };
    }
}
