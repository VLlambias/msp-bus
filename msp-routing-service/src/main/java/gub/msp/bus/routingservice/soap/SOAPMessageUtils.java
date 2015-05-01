/**
 * 
 */
package gub.msp.bus.routingservice.soap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

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

    public static final String BUS_ACTOR_URI = "http://servicios.msp.gub.uy/routing-service";

    public static SOAPMessage buildSoapFault(String code, String reason, String actor) throws SOAPException {
        SOAPMessage message = buildEmptySoapMessage();
        return addSoapFault(message, code, reason, actor);
    }

    private static SOAPMessage addSoapFault(SOAPMessage message, String code, String reason, String actor)
            throws SOAPException {
        SOAPFault soapFault = message.getSOAPBody().addFault();
        QName faultName = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, code);
        soapFault.setFaultCode(faultName);
        soapFault.setFaultActor(actor);
        soapFault.setFaultString(reason);
        return message;
    }

    public static SOAPMessage buildEmptySoapMessage() throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage message = messageFactory.createMessage();
        return message;
    }

    public static String messageToString(SOAPMessage message) throws SOAPException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        return new String(out.toByteArray());
    }
}
