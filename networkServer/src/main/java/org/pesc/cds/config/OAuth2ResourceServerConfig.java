package org.pesc.cds.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

/**
 * Created by James Whetstone on 10/2/16.
 *
 * Configure this network server as a resource server that uses the directory
 * server as a remote token store.
 *
 *
 * The @EnableResourceServer annotation adds a filter of type
 * OAuth2AuthenticationProcessingFilter automatically to the Spring
 * Security filter chain.
 *
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Bean
    public RemoteTokenServices tokenServices() {
        return new RemoteTokenServices();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServices()).resourceId("/api/v1/documents");
    }

}
