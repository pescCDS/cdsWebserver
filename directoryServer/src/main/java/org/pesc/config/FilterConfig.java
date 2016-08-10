package org.pesc.config;

/**
 * Created by james on 8/8/16.
 */
import org.pesc.api.security.CorsFilter;
import org.pesc.api.security.StatelessCSRFFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

@Configuration
public class FilterConfig {

    @Value("${rest.api.host}")
    private String restApiHost;

    @Bean
    public Filter corsFilter() {
        return new CorsFilter();
    }

    @Bean
    public Filter csrfFilter() {
        return new StatelessCSRFFilter();
    }

}