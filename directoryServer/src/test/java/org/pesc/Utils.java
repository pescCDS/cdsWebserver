package org.pesc;

import org.springframework.http.HttpHeaders;

/**
 * Created by james on 8/15/16.
 */
public class Utils {

    public static HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.add("Content-Type", "application/json");
        headers.add("Accept", "application/json");

        return headers;
    }

    public static void addCSRFHeaders(HttpHeaders headers) {
        final String clientSecret = "csrf-secret-token";
        headers.set("X-CSRF-TOKEN", clientSecret);
        headers.set("Cookie", "CSRF-TOKEN=" + clientSecret);
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



}
