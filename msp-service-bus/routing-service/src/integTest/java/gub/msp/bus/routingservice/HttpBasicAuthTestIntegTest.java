/**
 * 
 */
package gub.msp.bus.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.routingservice.SOAPMessageTestingUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 15/05/2015
 *
 */
public class HttpBasicAuthTestIntegTest {

    private static final String WSA_TO = "http://servicios.msp.gub.uy/servicio";
    private static final String WSA_ACTION = "http://servicios.msp.gub.uy/servicio/method";
    private static final String BOBS_SERVICE = "http://localhost:18080/routing-service/services/router/rucaf";
    private static final String BAD_USERNAME = "bobs";
    private static final String BAD_USERNAME_PASSWORD = "bobspassword";
    private static final String ACCESS_DENIED_SERVICE_URL = "http://localhost:18080/routing-service/services/router/cde";
    private static final String ACCESS_DENIED_USER = "bob";
    private static final String ACCESS_DENIED_USER_PASSWORD = "bobspassword";
    private static final String INVALID_DENIED_SERVICE_URL = "http://localhost:18080/routing-service/services/router/cocoa";
    private static final String INVALID_SERVICE_USERNAME = "bob";
    private static final String INVALID_SERVICE_USERNAME_PASSWORD = "bobspassword";

    @Test
    public void invalidUsernameTest() throws SOAPException, IOException,
            ParserConfigurationException,
            SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try {
            IntegrationTestUtils.sendMessage(BOBS_SERVICE, strMessage, BAD_USERNAME,
                    BAD_USERNAME_PASSWORD);
            fail("Expected an http 401 error");
        } catch (final HttpClientErrorException exception) {
            assertThat("Not an http error 401", exception.getStatusCode(),
                equalTo(HttpStatus.UNAUTHORIZED));
        }
    }

    @Test
    public void accessDeniedTest() throws SOAPException, IOException, ParserConfigurationException,
            SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try {
            IntegrationTestUtils.sendMessage(ACCESS_DENIED_SERVICE_URL, strMessage,
                    ACCESS_DENIED_USER,
                    ACCESS_DENIED_USER_PASSWORD);
            fail("Expected an http 403 error");
        } catch (final HttpClientErrorException exception) {
            assertThat("Not an http error 403", exception.getStatusCode(),
                    equalTo(HttpStatus.FORBIDDEN));
        }
    }

    @Test
    public void invalidServiceUrlTest() throws SOAPException, IOException,
            ParserConfigurationException, SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO, WSA_ACTION);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);

        try {
            IntegrationTestUtils.sendMessage(INVALID_DENIED_SERVICE_URL, strMessage,
                    INVALID_SERVICE_USERNAME, INVALID_SERVICE_USERNAME_PASSWORD);
            fail("Expected an http 403 error");
        } catch (final HttpClientErrorException exception) {
            assertThat("Not an http error 403", exception.getStatusCode(),
                    equalTo(HttpStatus.FORBIDDEN));
        }
    }
}
