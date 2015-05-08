/**
 * 
 */
package gub.msp.routingservice.soap.wsaddressing;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidator;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;
import gub.msp.routingservice.SOAPMessageTestingUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 07/05/2015
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-beans.xml")
public class WSAValidatorTest {

    @Autowired
    private WSAValidator wsaValidatorBean;

    private static final String WSA_TO = "http://localhost:18080/routing-service/services/router/rucaf";

    private static final String WSA_ACTION = "http://localhost:18080/routing-service/services/router/rucaf/method";

    @Test
    public void wsaComplianceMessage() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO, WSA_ACTION);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final String response = wsaValidatorBean.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.VALID_MESSAGE));
    }

    @Test
    public void noWsaToTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final String response = wsaValidatorBean.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_TO));
    }

    @Test
    public void emptyWsaToTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {
        final String emptyWsaTo = "";
        final String emptyWsaAction = "";
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(emptyWsaTo,
                emptyWsaAction);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final String response = wsaValidatorBean.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_TO));
    }

    @Test
    public void noWsaActionTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        SOAPMessageUtils.addHeader(soapMessage, AddressingConstants.WSA_TO,
                AddressingConstants.WSA_NS, AddressingConstants.WSA_PREFIX, WSA_TO);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final String response = wsaValidatorBean.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_ACTION));
    }

    @Test
    public void emptyWsaActionTest() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {

        final String wsaEmptyAction = "";
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO,
                wsaEmptyAction);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final String response = wsaValidatorBean.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_ACTION));
    }

    @Test
    public void notXmlMessage() {
        final String response = wsaValidatorBean.validate("abc");
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.MALFORMED_XML_MESSAGE));

    }

    @Test
    public void notSoapMessage() {
        final String response = wsaValidatorBean.validate("<a>e</a>");
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.MALFORMED_SOAP_MESSAGE));
    }
}
