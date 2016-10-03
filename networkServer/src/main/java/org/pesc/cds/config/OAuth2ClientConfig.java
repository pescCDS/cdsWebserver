package org.pesc.cds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import java.util.Arrays;

/**
 * Configure the OAuth2 client options used to verify access tokens.
 *
 * Created by James Whetstone on 10/2/16.
 */
@Configuration
@EnableOAuth2Client
public class OAuth2ClientConfig {

    @Value("${authentication.oauth.accessTokenUri}")
    private String accessTokenUri;

    @Value("${authentication.oauth.userAuthorizationUri}")
    private String userAuthorizationUri;

    @Bean
    public OAuth2ProtectedResourceDetails trusted() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setId("documents");
        details.setClientId("sallen");
        details.setAccessTokenUri(accessTokenUri);
        details.setScope(Arrays.asList("trust"));
        return details;
    }



}
