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
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import java.util.Arrays;

/**
 * Created by James Whetstone on 10/4/16.
 */

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
                                                       @Value("${networkServer.id}") String clientId) {

        OAuth2RestTemplate template = new OAuth2RestTemplate(fullAccessresourceDetailsClientOnly(tokenUrl, secret, clientId), new DefaultOAuth2ClientContext(
                new DefaultAccessTokenRequest()));
        return prepareTemplate(template);
    }

    public OAuth2RestTemplate prepareTemplate(OAuth2RestTemplate template) {

        template.setRequestFactory(getClientHttpRequestFactory());
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
        resource.setScope(Arrays.asList("write_inbox", "read_inbox", "write_transactions", "read_transactions"));
        return resource;
    }
}