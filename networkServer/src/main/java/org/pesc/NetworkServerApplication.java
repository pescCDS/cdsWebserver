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


package org.pesc;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan
@EnableAsync
public class NetworkServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(NetworkServerApplication.class, args);
	}

	/**
	 * Enables JNDI On Tomcat
	 * @return TomcatEmbeddedServletContainerFactory
	 */
	@Bean

	public TomcatEmbeddedServletContainerFactory tomcatFactory(@Value("${http.port}")Integer port,
															   @Value("${server.port}")Integer securePort) {
		TomcatEmbeddedServletContainerFactory factory =  new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
					Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}

			//Uncomment the code below if the server is used without a load balancer that's handling HTTPS.
            /*
			@Override
			protected void postProcessContext(Context context) {
				SecurityConstraint securityConstraint = new SecurityConstraint();
				securityConstraint.setUserConstraint("CONFIDENTIAL");
				SecurityCollection collection = new SecurityCollection();
				collection.addPattern("/*");
				securityConstraint.addCollection(collection);
				context.addConstraint(securityConstraint);
			}
			*/
		};


		//HTTPS
		factory.addAdditionalTomcatConnectors(createStandardConnector(port,securePort));


	  /* Not using AJP do to reverse proxy issues revolving around redirects that use an absolute path.  The
        funny thing is that the exact same configuration for HTTP works fine, so something up with AJP. */

        /*

        Connector ajpConnector = new Connector("AJP/1.3");
        ajpConnector.setPort(8009);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        factory.addAdditionalTomcatConnectors(ajpConnector);
        */

		//factory.addErrorPages(new ErrorPage(HttpStatus.UNAUTHORIZED, "/unauthenticated"));
		return factory;
	}

	private Connector createStandardConnector(Integer port, Integer securePort) {
		Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
		connector.setScheme("http");
		connector.setSecure(false);
		connector.setRedirectPort(securePort);
		connector.setPort(port);
		return connector;
	}

}
