package org.pesc;


import com.google.common.collect.Maps;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import org.junit.rules.ExternalResource;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;
/**
 * Created by James Whetstone on 3/16/16.
 */

public class DockerContainerRule extends ExternalResource {

    public static final String DOCKER_SERVICE_URL = "http://localhost:2375";
    private final DockerClient dockerClient;

    private ContainerCreation container;


    public DockerContainerRule(String imageName) {
        this(imageName, DOCKER_SERVICE_URL);
    }

    public DockerContainerRule(String imageName, String dockerServiceUrl) {


        ContainerConfig containerConfig = ContainerConfig.builder()
                .image(imageName)
                .networkDisabled(false)
                .build();

        dockerClient = new DefaultDockerClient(dockerServiceUrl);

        try {
            //dockerClient.pull(imageName);
            container = dockerClient.createContainer(containerConfig);
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean isSet(String value) {
        return value != null && !value.equals("");
    }


    @Override
    public Statement apply(Statement base, Description description) {
        return super.apply(base, description);
    }

    @Override
    protected void before() throws Throwable {
        super.before();
        dockerClient.startContainer(container.id());
        Thread.sleep(5000);
    }

    @Override
    protected void after() {
        super.after();
        try {
            dockerClient.killContainer(container.id());
            dockerClient.removeContainer(container.id(), true);
        } catch (DockerException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static int findFreePort() {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(0);
            socket.setReuseAddress(true);
            int port = socket.getLocalPort();
            try {
                socket.close();
            } catch (IOException e) {
                // Ignore IOException on close()
            }
            return port;
        } catch (IOException e) {
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }
        throw new IllegalStateException("Could not find a free TCP/IP port to start embedded Jetty HTTP Server on");
    }
}