/**
 * 
 */
package gub.msp.bus.routingservice.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

/**
 * @author Guzman Llambias
 * 
 */
public class SOAPMessageUtils {

    public static final String CLIENT_CODE = "Client";

    public static final String SERVER_CODE = "Server";

    public static final String BUS_ACTOR_URI = "http://servicios.msp.gub.uy/routing-service";

    public static SOAPMessage buildSoapFault(final String code, final String reason,
            final String actor) throws SOAPException {
        final SOAPMessage message = buildEmptySoapMessage();
        return addSoapFault(message, code, reason, actor);
    }

    private static SOAPMessage addSoapFault(final SOAPMessage message, final String code,
            final String reason, final String actor) throws SOAPException {
        final SOAPFault soapFault = message.getSOAPBody().addFault();
        final QName faultName = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, code);

        soapFault.setFaultCode(faultName);
        soapFault.setFaultActor(actor);
        soapFault.setFaultString(reason);
        return message;
    }

    public static SOAPMessage buildEmptySoapMessage() throws SOAPException {
        final MessageFactory messageFactory = MessageFactory
                .newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        return messageFactory.createMessage();
    }

    public static String messageToString(final SOAPMessage message) throws SOAPException,
            IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        return new String(out.toByteArray());
    }

    public static SOAPMessage stringToSOAPMessage(final String message) throws IOException,
            SOAPException {
        final InputStream inputStream = new ByteArrayInputStream(message.getBytes());
        return MessageFactory.newInstance().createMessage(null, inputStream);
    }
}
