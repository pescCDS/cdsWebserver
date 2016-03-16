package org.pesc.service;

/**
 * Created by james on 2/25/16.
 */


import javax.jws.WebService;

@WebService
public interface Directory {
    String sayHi(String text);
}