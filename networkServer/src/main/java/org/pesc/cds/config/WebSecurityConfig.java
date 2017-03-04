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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.xml.sax.SAXParseException;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/17/16.
 */

@Configuration
@EnableOAuth2Client
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${edex.sso.enabled}")
    private boolean ssoEnabled;

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/", "/home", "/about", "/documentation", "/js/**", "/fonts/**", "/images/**", "/css/**", "favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout().logoutSuccessUrl("/");

                if (ssoEnabled == true) {
                    http.addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
                }

    }

    @Autowired
    private ResourceServerTokenServices tokenServices;

    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login/edex");
        OAuth2RestTemplate template = new OAuth2RestTemplate(edexchangeDirectoryServer(), oauth2ClientContext);
        //UserInfoTokenServices tokenServices = new UserInfoTokenServices(edexDirectoryServerResource().getUserInfoUri(), edexchangeDirectoryServer().getClientId());
        filter.setTokenServices(tokenServices);
        filter.setRestTemplate(template);
        return filter;
    }

    @Bean
    @ConfigurationProperties("edex.directory.client")
    public AuthorizationCodeResourceDetails edexchangeDirectoryServer() {
        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setScope(Arrays.asList("read_transactions", "write_outbox", "read_outbox", "read_inbox"));
        return resourceDetails;
    }

    @Bean
    @ConfigurationProperties("edex.directory.resource")
    public ResourceServerProperties edexDirectoryServerResource() {
        return new ResourceServerProperties();
    }


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth,
                                @Value("${networkServer.admin.username}") String username,
                                @Value("${networkServer.admin.password}") String password) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser(username).password(password).roles("ORG_ADMIN", "SUPERUSER");
    }


    //Customize error messages returned when exceptions are thrown from REST controllers.
    @Bean
    public ErrorAttributes errorAttributes() {
        return new DefaultErrorAttributes() {
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                Throwable error = getError(requestAttributes);

                if (error instanceof SAXParseException) {
                    SAXParseException ex = (SAXParseException)error;
                    errorAttributes.put("line_number", ex.getLineNumber());
                    errorAttributes.put("column_number", ex.getColumnNumber());
                    errorAttributes.put("status", 400);
                    errorAttributes.put("error", "Invalid PESC XML");
                }
                else if (error instanceof IllegalArgumentException) {
                    errorAttributes.put("status", 400);
                }
                return errorAttributes;
            }

        };
    }


}