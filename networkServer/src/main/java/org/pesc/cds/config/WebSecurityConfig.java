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
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.context.request.RequestAttributes;
import org.xml.sax.SAXParseException;

import java.util.Map;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/17/16.
 */

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf()
                .disable()
                .authorizeRequests()
                .antMatchers("/", "/home", "/api/**","/transactions", "/about", "/upload", "/documentation", "/js/**", "/fonts/**","/images/**", "/css/**", "favicon.ico").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin").password("admin").roles("ADMIN");
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