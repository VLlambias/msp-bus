/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class TestRoutingService {

    private static final String TEXT_XML = "text/xml";
    private String faultCode = "";
    private String faultString = "";
    private String detail = "";
    private String soapFaultClientAddressing = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">"
            + "<soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
            + "<wsa:Action>http://www.w3.org/2005/08/addressing/fault</wsa:Action>"
            + "</soapenv:Header>"
            + "<soapenv:Body><soapenv:Fault>"
            + "<faultcode>" + faultCode + "</faultcode>"
            + "<faultstring>" + faultString + "</faultstring>"
            + "<detail>" + detail + "</detail>"
            + "</soapenv:Fault></soapenv:Body></soapenv:Envelope>";

    private ResponseEntity<String> sendMessage(String routingServiceUrl, String soapMessage) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("soapAction", "\"\"");
        headers.add(HttpHeaders.CONTENT_TYPE, TEXT_XML);
        HttpEntity<String> request = new HttpEntity<String>(soapMessage, headers);

        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(routingServiceUrl, HttpMethod.POST, request,
                String.class);
    }

    @Test
    public void basicTest() {
        String routingServiceUrl = "http://localhost:18080/routing-service/services/router/rucaf";
        String wsaTo = "http://localhost:18080/routing-service/services/router/rucaf";

        String soapMessage = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:gs=\"http://spring.io/guides/gs-producing-web-service\"><soapenv:Header xmlns:wsa=\"http://www.w3.org/2005/08/addressing\"><wsa:Action>http://spring.io/guides/gs-producing-web-service/CountriesPort/getCountryRequest</wsa:Action><wsa:To>"
                + wsaTo
                + "</wsa:To></soapenv:Header><soapenv:Body><gs:getCountryRequest><gs:name>Spain</gs:name></gs:getCountryRequest></soapenv:Body></soapenv:Envelope>";

        ResponseEntity<String> response = sendMessage(routingServiceUrl, soapMessage);
        assertThat("Error Http. Status http distinto de 200", response.getStatusCode(), equalTo(HttpStatus.OK));
        Assert.fail("Check soap message");
    }

    @Test
    public void serviceNotRegisteredTest() {
    }

    @Test
    public void emptyWsaToTest() {

    }

    @Test
    public void noWsaToTest() throws SOAPException, IOException, ParserConfigurationException, SAXException {
        String routingServiceUrl = "http://localhost:18080/routing-service/services/router/rucaf";
        faultCode = "wsa:MessageAddressingHeaderRequired";
        faultString = "A required header representing a Message Addressing Property is not present";
        detail = "wsa:To header element is required";

        SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        try {
            sendMessage(routingServiceUrl, strMessage);
        } catch (HttpServerErrorException exception) {
            assertThat("Http status code not 500", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Wrong wsa error message.", exception.getResponseBodyAsString(),
                    equalTo(soapFaultClientAddressing));
        }

    }

    @Test
    public void noWsaMessageIdTest() {
    }
}
