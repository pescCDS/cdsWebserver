package org.pesc.cds.service;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * Created by James Whetstone on 10/4/16.
 */
@Service
public class OAuthService {


    private HttpHeaders createHeaders( String username, String password ){
        return new HttpHeaders(){
            {
                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(
                        auth.getBytes(Charset.forName("US-ASCII")));
                String authHeader = "Basic " + new String( encodedAuth );
                set("Authorization", authHeader);
            }
        };
    }


    private HttpHeaders createOAuthHeaders( String oauthToken ){
        return new HttpHeaders(){
            {
                String authHeader = "Bearer " + oauthToken;
                set("Authorization", authHeader);
            }
        };
    }

    public String getOAuthToken() {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "password");
        map.add("username", "sallen");
        map.add("password", "admin");
        map.add("client_id", "sallen");

        ResponseEntity<String> response = restTemplate.exchange
                ("http://localhost/oauth/token", HttpMethod.POST, new HttpEntity<Object>(map, createHeaders("sallen", "admin")), String.class);

        return response.getBody();
    }


}
