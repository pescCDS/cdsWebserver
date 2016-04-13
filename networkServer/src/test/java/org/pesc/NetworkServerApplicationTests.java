package org.pesc;

import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = NetworkServerApplication.class)
@WebAppConfiguration
public class NetworkServerApplicationTests {

	@ClassRule
	public static DockerContainerRule dockerContainerRule = new DockerContainerRule("edex_networkserver_db_image");

	@Test
	public void contextLoads() {
	}

}
