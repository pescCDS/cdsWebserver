package org.pesc.cds.config;

import org.apache.http.auth.AUTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;

import java.util.Arrays;

@Configuration
public class OAuthClientConfig {

    @Autowired(required = false)
    ClientHttpRequestFactory clientHttpRequestFactory;

    /*
     * ClientHttpRequestFactory is autowired and checked in case somewhere in
     * your configuration you provided {@link ClientHttpRequestFactory}
     * implementation Bean where you defined specifics of your connection, if
     * not it is instantiated here with {@link SimpleClientHttpRequestFactory}
     */
    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        if (clientHttpRequestFactory == null) {
            clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        }
        return clientHttpRequestFactory;
    }

    @Bean
    @Qualifier("myRestTemplate")
    public OAuth2RestOperations restTemplate(@Value("${authentication.oauth.accessTokenUri}") String tokenUrl,
                                                       @Value("${authentication.oauth.secret}") String secret,
                                                       @Value("${networkServer.id}") String clientId,
                                             @Value("${networkServer.ssl.trust-certificates}") Boolean trustAllCertificates) {

        OAuth2RestTemplate template = new OAuth2RestTemplate(fullAccessresourceDetailsClientOnly(tokenUrl, secret, clientId), new DefaultOAuth2ClientContext(
                new DefaultAccessTokenRequest()));
        return prepareTemplate(template, trustAllCertificates);
    }

    public OAuth2RestTemplate prepareTemplate(OAuth2RestTemplate template, Boolean trustAllCertificates) {
        if (trustAllCertificates) {
            template.setRequestFactory(getClientHttpRequestFactory());
        }

        template.setAccessTokenProvider(clientAccessTokenProvider());

        return template;
    }

    @Bean
    public AccessTokenProvider clientAccessTokenProvider() {
        ClientCredentialsAccessTokenProvider accessTokenProvider = new ClientCredentialsAccessTokenProvider();
        accessTokenProvider.setRequestFactory(getClientHttpRequestFactory());
        return accessTokenProvider;
    }


    @Bean
    public OAuth2ProtectedResourceDetails fullAccessresourceDetailsClientOnly(String tokenUrl, String secret, String clientId) {
        ClientCredentialsResourceDetails resource = new ClientCredentialsResourceDetails();
        resource.setAccessTokenUri(tokenUrl);
        resource.setClientId(clientId);
        resource.setClientSecret(secret);
        resource.setGrantType("client_credentials");
        resource.setScope(Arrays.asList("read", "write"));
        return resource;
    }
}