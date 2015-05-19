/**
 * 
 */
package gub.msp.bus.routingservice;

import static gub.msp.routingservice.matchers.HasFaultMessage.hasFaultString;
import static gub.msp.routingservice.matchers.IsFaultCode.isServerCodeFault;
import static gub.msp.routingservice.matchers.IsSoapFault.isSoapFault;
import static gub.msp.routingservice.matchers.SoapFaultMatcher.hasSoapActor;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;
import gub.msp.routingservice.SOAPMessageTestingUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 19/05/2015
 *
 */
public class TimeoutsIntegTest {

    private static final String WSA_TO_404_ERROR = "http://servicios.msp.gub.uy/servicioHttp404Error";
    private static final String WSA_TO_CONNECTION_ERROR = "http://servicios.msp.gub.uy/servicioConnectionError";
    private static final String WSA_TO_TIMEOUT_ERROR = "http://servicios.msp.gub.uy/servicioConnectionTimeoutError";
    private static final String WSA_ACTION = "http://servicios.msp.gub.uy/servicio/method";
    private static final String SERVICE_URL = "http://localhost:18080/routing-service/services/router/rucaf";
    private static final String USERNAME = "bob";
    private static final String PASSWORD = "bobspassword";

    @Test
    public void http404ErrorCallingService() throws SOAPException, ParserConfigurationException,
            SAXException, IOException, XPathExpressionException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO_404_ERROR, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try{
            IntegrationTestUtils.sendMessage(SERVICE_URL,strMessage, USERNAME, PASSWORD);
        }catch(final HttpServerErrorException exception){
            final String response = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(response);

            assertThat("Http cod is not 500 Internal Server Error", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse, hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a server code soap fault", soapResponse, isServerCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.DESTINATION_UNREACHABLE));
        }
    }

    @Test
    public void httpConnectionErrorCallingService() throws SOAPException,
            ParserConfigurationException, SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(
                WSA_TO_CONNECTION_ERROR, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try {
            IntegrationTestUtils.sendMessage(SERVICE_URL, strMessage, USERNAME, PASSWORD);
        } catch (final HttpServerErrorException exception) {
            final String response = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(response);

            assertThat("Http cod is not 500 Internal Server Error", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse,
                    hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a server code soap fault", soapResponse, isServerCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.SERVICE_TIMEOUT));
        }
    }

    @Test(timeout = 10000)
    public void httpReadTimeoutCallingService() throws IOException, SOAPException,
            ParserConfigurationException, SAXException {
        final SOAPMessage soapMessage = buildTimeoutTestMessage(WSA_TO_TIMEOUT_ERROR, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try {
            IntegrationTestUtils.sendMessage(SERVICE_URL, strMessage, USERNAME, PASSWORD);
        } catch (final HttpServerErrorException exception) {
            final String response = exception.getResponseBodyAsString();
            final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(response);

            assertThat("Http cod is not 500 Internal Server Error", exception.getStatusCode(),
                    equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
            assertThat("Not a soap fault", soapResponse, isSoapFault());
            assertThat("Invalid soap actor", soapResponse,
                    hasSoapActor(SOAPMessageUtils.BUS_ACTOR_URI));
            assertThat("Not a server code soap fault", soapResponse, isServerCodeFault());
            assertThat("Invalid soap error message ", soapResponse,
                    hasFaultString(WSAValidatorConstants.SERVICE_TIMEOUT));
        }
    }

    private SOAPMessage buildTimeoutTestMessage(final String wsaTo, final String wsaAction)
            throws SOAPException, ParserConfigurationException, SAXException, IOException {
        final SOAPMessage message = basicTestMessage();
        SOAPMessageUtils.addHeader(message, AddressingConstants.WSA_TO, AddressingConstants.WSA_NS,
                AddressingConstants.WSA_PREFIX, wsaTo);
        return SOAPMessageUtils.addHeader(message, AddressingConstants.WSA_ACTION,
                AddressingConstants.WSA_NS, AddressingConstants.WSA_PREFIX, wsaAction);
    }

    private static SOAPMessage basicTestMessage() throws SOAPException,
            ParserConfigurationException, SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageUtils.buildEmptySoapMessage();
        return addBusinessData(soapMessage);
    }

    private static SOAPMessage addBusinessData(final SOAPMessage soapMessage)
            throws ParserConfigurationException, SAXException, IOException, SOAPException {
        final String message = "<test:echo xmlns:test=\"http://test/\"><arg0>pp</arg0></test:echo>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document data = builder.parse(new InputSource(new ByteArrayInputStream(message
                .getBytes("utf-8"))));
        soapMessage.getSOAPBody().addDocument(data);
        return soapMessage;
    }

}
