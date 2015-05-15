/**
 * 
 */
package gub.msp.bus.routingservice;

import static gub.msp.routingservice.matchers.HasFaultMessage.hasFaultString;
import static gub.msp.routingservice.matchers.IsFaultCode.isClientCodeFault;
import static gub.msp.routingservice.matchers.IsFaultCode.isServerCodeFault;
import static gub.msp.routingservice.matchers.IsSoapFault.isSoapFault;
import static gub.msp.routingservice.matchers.SoapFaultMatcher.hasSoapActor;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAddressingNamespaceContext;
import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;
import gub.msp.routingservice.SOAPMessageTestingUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * 
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class RoutingServiceIntegTest {

    private static final String WSA_TO = "http://servicios.msp.gub.uy/servicio";
    private static final String WSA_ACTION = "http://servicios.msp.gub.uy/servicio/method";
    private static final String BAD_WSA_TO = "bad wsa:To";
    private static final String SERVICE_URL = "http://localhost:18080/routing-service/services/router/rucaf";
    private static final String USERNAME = "bob";
    private static final String PASSWORD = "bobspassword";

    @Test
    public void basicTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException, XPathExpressionException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        final ResponseEntity<String> response = IntegrationTestUtils.sendMessage(SERVICE_URL,
                strMessage, USERNAME, PASSWORD);

        final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(response.getBody());

        final String wsaActionResponse = XPathUtils.getStringValue(
                "/env:Envelope/env:Header/wsa:Action", new WSAddressingNamespaceContext(),
                XMLUtils.stringToDocument(response.getBody()));

        assertThat("Error Http. Status http distinto de 200", response.getStatusCode(),
                equalTo(HttpStatus.OK));
        assertThat("Invalid soap body: " + soapMessage.getSOAPBody(), soapResponse.getSOAPBody()
                .getFirstChild().getLocalName(), equalTo("getCountryResponse"));

        assertThat("No wsa:Action header on response", wsaActionResponse,
                not(isEmptyOrNullString()));
        assertThat("No wsa:Action header on response", wsaActionResponse, equalTo(WSA_ACTION
                + "Response"));
    }

    @Test
    public void badContentTypeRequestTest() {

    }
    
    @Test
    public void badContentTypeResponseTest() {

    }

    @Test
    public void noSoapActionTest() {

    }

    @Test
    public void noWsaToTest() throws SOAPException, IOException, ParserConfigurationException,
            SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        try {
            IntegrationTestUtils.sendMessage(SERVICE_URL, strMessage, USERNAME, PASSWORD);
            fail("No error occurred");
        } catch (final HttpServerErrorException exception) {
            final String strResponseMessage = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils
                    .stringToSOAPMessage(strResponseMessage);

            assertThat("Http status code not 500", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse,
                    hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a client code soap fault", soapResponse, isClientCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.INVALID_WSA_TO));
        }
    }

    @Test
    public void serviceNotRegisteredOnCatalogTest() throws SOAPException, IOException,
            ParserConfigurationException,
            SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(BAD_WSA_TO,
                WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        try {
            IntegrationTestUtils.sendMessage(SERVICE_URL, strMessage, USERNAME, PASSWORD);
            fail("No error occurred");
        } catch (final HttpServerErrorException exception) {
            final String strResponseMessage = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils
                    .stringToSOAPMessage(strResponseMessage);

            assertThat("Http status code not 500", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse,
                    hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a server code soap fault", soapResponse, isServerCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.SERVICE_NOT_FOUND));
        }
    }

    @Test
    public void noWsaActionTest() throws SOAPException, IOException, ParserConfigurationException,
            SAXException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        SOAPMessageUtils.addHeader(soapMessage, AddressingConstants.WSA_TO,
                AddressingConstants.WSA_NS, AddressingConstants.WSA_PREFIX, WSA_TO);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        try {
            IntegrationTestUtils.sendMessage(SERVICE_URL, strMessage, USERNAME, PASSWORD);
            fail("No error occurred");
        } catch (final HttpServerErrorException exception) {
            final String strResponseMessage = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils
                    .stringToSOAPMessage(strResponseMessage);

            assertThat("Http status code not 500", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse,
                    hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a client code soap fault", soapResponse, isClientCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.INVALID_WSA_ACTION));
        }
    }
}
