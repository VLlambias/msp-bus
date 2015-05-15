/**
 * 
 */
package gub.msp.bus.routingservice;

import java.nio.charset.Charset;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.RestTemplate;

/**
 * @author Guzman Llambias
 * @since 15/05/2015
 *
 */
public class IntegrationTestUtils {

    private static final String UTF_8 = "UTF-8";

    private static void addHttpBasicAuth(final String username, final String password,
            final HttpHeaders headers) {
        final String auth = username + ":" + password;
        final byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName(UTF_8)));
        final String authHeader = "Basic " + new String(encodedAuth);
        headers.add(HttpHeaders.AUTHORIZATION, authHeader);

    }

    private static void addContentType(final HttpHeaders headers) {
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE);
    }

    private static void addSoapAction(final HttpHeaders headers) {
        headers.add("soapAction", "\"\"");
    }

    public static ResponseEntity<String> sendMessage(final String routingServiceUrl,
            final String soapMessage, final String username, final String password) {
        final HttpHeaders headers = new HttpHeaders();
        addSoapAction(headers);
        addContentType(headers);
        addHttpBasicAuth(username, password, headers);
        final HttpEntity<String> request = new HttpEntity<String>(soapMessage, headers);

        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(routingServiceUrl, HttpMethod.POST, request, String.class);
    }
}
