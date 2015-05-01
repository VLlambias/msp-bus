/**
 * 
 */
package gub.msp.routingservice;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    public static SOAPMessage basicTestMessage() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {
        SOAPMessage soapMessage = SOAPMessageUtils.buildEmptySoapMessage();
        return addBusinessData(soapMessage);
    }

    private static SOAPMessage addBusinessData(SOAPMessage soapMessage) throws ParserConfigurationException,
            SAXException, IOException, SOAPException {
        String message = "<gs:getCountryRequest xmlns:gs=\"http://spring.io/guides/gs-producing-web-service\">"
                + "<gs:name>Spain</gs:name></gs:getCountryRequest>";

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document data = builder.parse(new InputSource(new ByteArrayInputStream(message.getBytes("utf-8"))));
        soapMessage.getSOAPBody().addDocument(data);
        return soapMessage;
    }
}
