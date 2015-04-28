/**
 * 
 */
package gub.msp;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Guzman Llambias
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
// @ContextConfiguration(locations = { "classpath:META-INF/service-config.xml"
// })
public class TestRoutingService {

    private static final String TEXT_XML = "text/xml";

    @Test
    public void basicTest() {
        String routingServiceUrl = "http://localhost:18080/routing-service/services/router/rucaf";
        String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gs=\"http://spring.io/guides/gs-producing-web-service\"><soapenv:Header/><soapenv:Body><gs:getCountryRequest><gs:name>Spain</gs:name></gs:getCountryRequest></soapenv:Body></soapenv:Envelope>";
        HttpHeaders headers = new HttpHeaders();
        headers.add("soapAction", "\"\"");
        headers.add(HttpHeaders.CONTENT_TYPE, TEXT_XML);
        HttpEntity<String> request = new HttpEntity<String>(soapMessage, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(routingServiceUrl, HttpMethod.POST, request,
                String.class);
        assertThat("Error Http. Status http distinto de 200", response.getStatusCode(), equalTo(HttpStatus.OK));
    }
}
