package org.pesc.web;

import java.util.Map;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pesc.DirectoryApplication;
import org.pesc.DockerContainerRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

/**
 * Created by James Whetstone (jwhetstone@ccctechcenter.org) on 2/23/16.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = DirectoryApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
@DirtiesContext
public class HomeControllerTests {

    @ClassRule
    public static DockerContainerRule dockerContainerRule = new DockerContainerRule("cdswebserver_directoryserver_db_image");


    @Value("${local.server.port}")
    private int sslPort;


    @Value("${http.port}")
    private int port;

    @Test
    public void testHome() throws Exception {
        ResponseEntity<String> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + this.port + "/home", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }



}

