/**
 * 
 */
package gub.msp.routingservice;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * 
 */
public class SOAPMessageTestingUtils {

    public static SOAPMessage basicTestMessage() throws SOAPException,
            ParserConfigurationException, SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageUtils.buildEmptySoapMessage();
        return addBusinessData(soapMessage);
    }

    private static SOAPMessage addBusinessData(final SOAPMessage soapMessage)
            throws ParserConfigurationException, SAXException, IOException, SOAPException {
        final String message = "<gs:getCountryRequest xmlns:gs=\"http://spring.io/guides/gs-producing-web-service\">"
                + "<gs:name>Spain</gs:name></gs:getCountryRequest>";

        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        final Document data = builder.parse(new InputSource(new ByteArrayInputStream(message
                .getBytes("utf-8"))));
        soapMessage.getSOAPBody().addDocument(data);
        return soapMessage;
    }

    public static SOAPMessage wsaTestMessage(final String wsaTo, final String wsaAction)
            throws SOAPException, ParserConfigurationException, SAXException, IOException {
        final SOAPMessage message = basicTestMessage();
        addHeader(message, AddressingConstants.WSA_TO, AddressingConstants.WSA_NS,
                AddressingConstants.WSA_PREFIX, wsaTo);
        return addHeader(message, AddressingConstants.WSA_ACTION, AddressingConstants.WSA_NS,
                AddressingConstants.WSA_PREFIX, wsaAction);
    }

    public static SOAPMessage addHeader(final SOAPMessage message, final String name,
            final String namespace, final String prefix, final String content) throws SOAPException {
        final QName qname = new QName(namespace, name, prefix);
        final SOAPElement wsaHeader = message.getSOAPHeader().addHeaderElement(qname);
        wsaHeader.addTextNode(content);
        return message;
    }
}
