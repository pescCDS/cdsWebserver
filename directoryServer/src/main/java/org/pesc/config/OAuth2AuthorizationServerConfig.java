package org.pesc.config;

import org.pesc.oauth.EdExClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

import javax.sql.DataSource;

/**
 * The directory server is an OAuth2 authorization server.  Configure the OAuth2 options here.
 *
 * Created by James Whetstone on 10/2/16.
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Value("${authentication.oauth.secret}")
    private String secret;

    @Value("${authentication.oauth.clientid}")
    private String clientID;

    @Value("${authentication.oauth.tokenValidityInSeconds}")
    private Integer tokenValiditySeconds;

    @Autowired
    private DataSource dataSource;

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(dataSource);
    }


    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;



    @Bean
    public ClientDetailsService clientDetailsService() {
        return new EdExClientDetailsService();
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints)
    throws Exception {

        endpoints
                .tokenStore(tokenStore())
                //Approval handler not needed.  Approval is handled via enabling or disabling an organization.
                //.userApprovalHandler(userApprovalHandler())
                .authenticationManager(authenticationManager);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService());

    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //Allow the resource server to validate tokens...
        security.checkTokenAccess("isAuthenticated()");    //change to permitAll() for public access.
    }

}
