package org.pesc.service;

/**
 * Created by james on 2/25/16.
 */
import javax.jws.WebService;

@WebService(endpointInterface = "org.pesc.service.Directory")
public class DirectoryImpl implements Directory {

    public String sayHi(String text) {
        System.out.println("sayHi called");
        return "Hello " + text;
    }
}