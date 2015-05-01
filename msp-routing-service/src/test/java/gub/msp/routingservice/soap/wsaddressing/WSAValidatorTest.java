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
import gub.msp.bus.routingservice.soap.wsaddressing.WSAddressingNamespaceContext;
import gub.msp.routingservice.SOAPMessageTestingUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * 
 */
public class WSAValidatorTest {

    private static final String wsaTo = "http://localhost:18080/routing-service/services/router/rucaf";

    private static final String wsaAction = "http://localhost:18080/routing-service/services/router/rucaf/method";

    @Test
    public void wsaComplicanceMessage() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(wsaTo, wsaAction);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final WSAValidator wsavalidator = new WSAValidator(new WSAddressingNamespaceContext());
        final String response = wsavalidator.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.VALID_MESSAGE));
    }

    @Test
    public void noWsaToTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final WSAValidator wsavalidator = new WSAValidator(new WSAddressingNamespaceContext());
        final String response = wsavalidator.validate(request);
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

        final WSAValidator wsavalidator = new WSAValidator(new WSAddressingNamespaceContext());
        final String response = wsavalidator.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_TO));
    }

    @Test
    public void noWsaActionTest() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        SOAPMessageTestingUtils.addHeader(soapMessage, AddressingConstants.WSA_TO,
                AddressingConstants.WSA_NS, AddressingConstants.WSA_PREFIX, wsaTo);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final WSAValidator wsavalidator = new WSAValidator(new WSAddressingNamespaceContext());
        final String response = wsavalidator.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_ACTION));
    }

    @Test
    public void emptyWsaActionTest() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {

        final String wsaEmptyAction = "";
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(wsaTo,
                wsaEmptyAction);
        final String request = SOAPMessageUtils.messageToString(soapMessage);

        final WSAValidator wsavalidator = new WSAValidator(new WSAddressingNamespaceContext());
        final String response = wsavalidator.validate(request);
        assertThat("Wrong validation message: " + response, response,
                equalTo(WSAValidatorConstants.INVALID_WSA_ACTION));
    }
}
