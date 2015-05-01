/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAddressingNamespaceContext;
import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    private ResponseEntity<String> sendMessage(final String routingServiceUrl,
            final String soapMessage) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("soapAction", "\"\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_XML_VALUE);
        final HttpEntity<String> request = new HttpEntity<String>(soapMessage, headers);

        final RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(routingServiceUrl, HttpMethod.POST, request, String.class);
    }

    @Test
    public void basicTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {
        final String routingServiceUrl = "http://localhost:18080/routing-service/services/router/rucaf";
        final String wsaTo = "http://localhost:18080/routing-service/services/router/rucaf";
        final String wsaAction = "http://localhost:18080/routing-service/services/router/rucaf/method";

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(wsaTo, wsaAction);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        final ResponseEntity<String> response = sendMessage(routingServiceUrl, strMessage);

        final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(response.getBody());
        final MediaType contentType = response.getHeaders().getContentType();

        final String wsaActionResponse = XPathUtils.getStringValue(
                "/env:Envelope/env:Header/wsa:Action", new WSAddressingNamespaceContext(),
                XMLUtils.stringToDocument(response.getBody()));

        assertThat("Error Http. Status http distinto de 200", response.getStatusCode(),
                equalTo(HttpStatus.OK));
        assertThat("Invalid soap body: " + soapMessage.getSOAPBody(), soapResponse.getSOAPBody()
                .getFirstChild().getLocalName(), equalTo("getCountryResponse"));

        assertThat("No wsa:Action header on response", wsaActionResponse,
                not(isEmptyOrNullString()));
        assertThat("Content-type is not text/xml", contentType, equalTo(MediaType.TEXT_XML));
    }

    @Test
    public void noWsaToTest() throws SOAPException, IOException, ParserConfigurationException,
            SAXException {
        final String routingServiceUrl = "http://localhost:18080/routing-service/services/router/rucaf";
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        try {
            sendMessage(routingServiceUrl, strMessage);
            fail("No error occurred");
        } catch (final HttpServerErrorException exception) {
            final String strResponseMessage = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils
                    .stringToSOAPMessage(strResponseMessage);
            final SOAPFault soapFault = soapResponse.getSOAPBody().getFault();
            final QName clientCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
                    SOAPMessageUtils.CLIENT_CODE);

            assertThat("Http status code not 500", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("No soap fault found on message", soapFault, notNullValue());
            assertThat("Soap code fault is not " + SOAPMessageUtils.CLIENT_CODE,
                    soapFault.getFaultCodeAsQName(), equalTo(clientCode));
            assertThat("Soap actor is not " + SOAPMessageUtils.BUS_ACTOR_URI,
                    soapFault.getFaultActor(), equalTo(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Invalid soap subcode: " + soapFault.getFaultString(),
                    soapFault.getFaultString(), equalTo("Invalid wsa:To header"));
        }
    }

}
