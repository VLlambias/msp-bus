/**
 * 
 */
package gub.msp.routingservice;

import static gub.msp.routingservice.matchers.IsSoapFault.isSoapFault;
import static gub.msp.routingservice.matchers.SoapHeaderMatcher.hasHeader;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.WSAddressingEnricher;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 07/05/2015
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-beans.xml")
public class WSAddressingEnricherTest {

    private final static String WSA_ACTION_REQUEST = "http://servicios.msp.gub.uy/servicios/rucaf/metodo";
    private static final String EMPTY_WSA_ACTION_REQUEST = "";

    @Autowired
    private WSAddressingEnricher wsaActionEnricherBean;

    @Test
    public void transformTest() throws SOAPException, IOException, ParserConfigurationException,
            SAXException {
        final Message<String> message = buildTestMessageWithActionHeader(WSA_ACTION_REQUEST);
        final Message<String> transformedMessage = wsaActionEnricherBean.transform(message);
        final String payload = transformedMessage.getPayload();
        final SOAPMessage soapMessage = SOAPMessageUtils.stringToSOAPMessage(payload);

        final QName qname = new QName(AddressingConstants.WSA_NS, AddressingConstants.WSA_ACTION,
                AddressingConstants.WSA_PREFIX);
        final SOAPHeaderElement expectedHeader = SOAPMessageUtils.buildEmptySoapMessage()
                .getSOAPHeader().addHeaderElement(qname);
        expectedHeader.setTextContent(WSA_ACTION_REQUEST + "Response");
        assertThat("No wsa:Action header in soap message", soapMessage, hasHeader(expectedHeader));
    }

    @Test
    public void noAddressingActionRequest() throws SOAPException, IOException,
            ParserConfigurationException, SAXException {

        final SOAPMessage soapRequest = SOAPMessageTestingUtils.basicTestMessage();
        final String strSoapRequest = SOAPMessageUtils.messageToString(soapRequest);
        final Message<String> message = MessageBuilder.withPayload(strSoapRequest).build();
        final Message<String> transformedMessage = wsaActionEnricherBean.transform(message);
        final String payload = transformedMessage.getPayload();
        final SOAPMessage soapMessage = SOAPMessageUtils.stringToSOAPMessage(payload);

        final QName qname = new QName(AddressingConstants.WSA_NS, AddressingConstants.WSA_ACTION,
                AddressingConstants.WSA_PREFIX);
        final SOAPHeaderElement expectedHeader = SOAPMessageUtils.buildEmptySoapMessage()
                .getSOAPHeader().addHeaderElement(qname);
        expectedHeader.setTextContent(WSA_ACTION_REQUEST + "Response");
        assertThat("No wsa:Action header in soap message", soapMessage,
                not(hasHeader(expectedHeader)));
    }

    @Test
    public void hasNoActionRequestHeader() throws SOAPException, IOException,
            ParserConfigurationException, SAXException {

        final Message<String> message = buildTestMessageWithoutActionHeader();
        final Message<String> transformedMessage = wsaActionEnricherBean.transform(message);
        final String payload = transformedMessage.getPayload();
        final SOAPMessage soapMessage = SOAPMessageUtils.stringToSOAPMessage(payload);

        assertThat("Not a soap fault: " + soapMessage, soapMessage, isSoapFault());
    }

    @Test
    public void hasEmptyActionRequestHeader() throws SOAPException, IOException,
            ParserConfigurationException, SAXException {

        final Message<String> message = buildTestMessageWithActionHeader(EMPTY_WSA_ACTION_REQUEST);
        final Message<String> transformedMessage = wsaActionEnricherBean.transform(message);
        final String payload = transformedMessage.getPayload();
        final SOAPMessage soapMessage = SOAPMessageUtils.stringToSOAPMessage(payload);

        final QName qname = new QName(AddressingConstants.WSA_NS, AddressingConstants.WSA_ACTION,
                AddressingConstants.WSA_PREFIX);
        final SOAPHeaderElement expectedHeader = SOAPMessageUtils.buildEmptySoapMessage()
                .getSOAPHeader().addHeaderElement(qname);
        expectedHeader.setTextContent("Response");
        assertThat("No wsa:Action header in soap message", soapMessage, hasHeader(expectedHeader));
    }

    private Message<String> buildTestMessageWithoutActionHeader() throws SOAPException,
            IOException, ParserConfigurationException, SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String strSoapMessage = SOAPMessageUtils.messageToString(soapMessage);
        return MessageBuilder.withPayload(strSoapMessage).build();
    }

    private Message<String> buildTestMessageWithActionHeader(final String actionHeader)
            throws SOAPException, IOException, ParserConfigurationException, SAXException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String strSoapMessage = SOAPMessageUtils.messageToString(soapMessage);
        return MessageBuilder.withPayload(strSoapMessage)
                .setHeader(WSAValidatorConstants.WSA_REQUEST_ACTION, actionHeader).build();
    }
}
