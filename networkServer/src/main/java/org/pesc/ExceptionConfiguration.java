package org.pesc;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 3/3/16.
 */
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;


/**
 * Setup for exception handling using a {@link SimpleMappingExceptionResolver}
 * bean.
 */
@Configuration
public class ExceptionConfiguration {

    protected Logger logger;

    public ExceptionConfiguration() {
        logger = LoggerFactory.getLogger(getClass());
        logger.info("Creating ExceptionConfiguration");
    }

    /**
     * Setup the classic SimpleMappingExceptionResolver. This provides useful
     * defaults for logging and handling exceptions. It has been part of Spring
     * MVC since Spring V2 and you will probably find most existing Spring MVC
     * applications are using it.
     * <p>
     * Only invoked if the "global" profile is active.
     *
     * @return The new resolver
     */
    @Bean(name = "simpleMappingExceptionResolver")
    public SimpleMappingExceptionResolver createSimpleMappingExceptionResolver() {
        logger.info("Creating SimpleMappingExceptionResolver");
        SimpleMappingExceptionResolver r = new SimpleMappingExceptionResolver();


        r.setDefaultErrorView("error");
        return r;
    }
}