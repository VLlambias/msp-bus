/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.io.IOException;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Guzman Llambias
 * 
 */
public class SoapFaultTransformer {

    private static final Logger LOG = LoggerFactory.getLogger(SoapFaultTransformer.class);

    public Message<String> transform(final Message<String> message) {
        final String errorType = (String) message.getHeaders().get(
                WSAValidatorConstants.ERROR_HEADER_NAME);

        String soapFault = "";
        switch (errorType) {
        case WSAValidatorConstants.INVALID_WSA_TO:
            soapFault = buildSOAPFault(SOAPMessageUtils.CLIENT_CODE,
                    WSAValidatorConstants.INVALID_WSA_TO);
            break;
        case WSAValidatorConstants.INVALID_WSA_ACTION:
            soapFault = buildSOAPFault(SOAPMessageUtils.CLIENT_CODE,
                    WSAValidatorConstants.INVALID_WSA_ACTION);
            break;
        default:
            soapFault = buildSOAPFault(SOAPMessageUtils.SERVER_CODE,
                    WSAValidatorConstants.INTERNAL_SERVER_ERROR);
            break;
        }
        return MessageBuilder.withPayload(soapFault).copyHeaders(message.getHeaders())
                .setHeader(HttpHeaders.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private String buildSOAPFault(final String code, final String errorMessage) {
        try {
            final SOAPMessage message = SOAPMessageUtils.buildSoapFault(code, errorMessage,
                    SOAPMessageUtils.BUS_ACTOR_URI);
            return SOAPMessageUtils.messageToString(message);
        } catch (SOAPException | IOException e) {
            LOG.error("Error creating soap fault: " + errorMessage, e);
            return "";
        }
    }

}
