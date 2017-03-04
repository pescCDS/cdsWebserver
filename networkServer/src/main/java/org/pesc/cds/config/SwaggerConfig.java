/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by james on 1/31/17.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${edex.directory.client.clientId}")
    private String clientId;

    @Value("${edex.directory.client.clientSecret}")
    private String clientSecret;

    @Value("${edex.directory.client.accessTokenUri}")
    private String tokenURL;

    @Value("${edex.directory.client.scope}")
    private String[] scopes;



    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.pesc.cds.web"))
                .paths(PathSelectors.ant("/api/v1/**"))
                .build().securitySchemes(newArrayList(oAuth(), basicAuth()));//.securityContexts(newArrayList(securityContext()));
                //securitySchemes(newArrayList(apiKey())).securityContexts(newArrayList(securityContext()))
    }

    private OAuth oAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("read_inbox", "Read Inbox");
       // AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
       // authorizationScopes[0] = authorizationScope;

        GrantType grantType = new ClientCredentialsGrant(tokenURL);

        return new OAuth("oauth", newArrayList(authorizationScope), newArrayList(grantType));
    }

    private BasicAuth basicAuth() {
        return new BasicAuth("basic");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder().securityReferences(defaultAuth()).forPaths(PathSelectors.regex("/anyPath.*"))
                .build();
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return newArrayList(new SecurityReference("mykey", authorizationScopes));
    }


    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration("3", "Testing123&4000", "test-app-realm", "test-app",
                "apiKey", ApiKeyVehicle.HEADER, "api_key", "," /*
                                                                * scope
                                                                * separator
                                                                */);
    }

}