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