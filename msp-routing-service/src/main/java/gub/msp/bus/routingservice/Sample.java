package gub.msp.bus.routingservice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

public class Sample {

    public static void main(String[] args) throws SOAPException, IOException {
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);
        SOAPMessage message = messageFactory.createMessage();
        SOAPFault soapFault = message.getSOAPBody().addFault();
        QName faultName = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Client");
        soapFault.setFaultCode(faultName);
        soapFault.setFaultActor("http://gub.msp/routing");
        soapFault.setFaultString("Server not responding");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        message.writeTo(out);
        String strMsg = new String(out.toByteArray());
        System.out.println(strMsg);
    }

}
