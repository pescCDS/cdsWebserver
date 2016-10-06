package org.pesc.cds.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * Created by James Whetstone on 10/4/16.
 */
@Configuration
@EnableResourceServer
public class OAuthServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${authentication.oauth.secret}") String secret;
    @Value("${networkServer.id}") String clientId;

    @Bean
    public ResourceServerTokenServices tokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost/oauth/check_token");
        remoteTokenServices.setClientId(clientId);
        remoteTokenServices.setClientSecret(secret);
        return remoteTokenServices;
    }
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.tokenServices(tokenServices()).resourceId("edexchange");
    }



    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/api/v1/documents/test").and().authorizeRequests().antMatchers("/api/v1/documents/test").authenticated();
    }
}
