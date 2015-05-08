/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.AddressingConstants;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

/**
 * @author Guzman Llambias
 * @since 07/05/2015
 * 
 */
public class WSAddressingEnricher {

    private static final Logger LOG = LoggerFactory.getLogger(WSAddressingEnricher.class);

    private final SoapFaultTransformer soapFaultTransformer;

    public WSAddressingEnricher(final SoapFaultTransformer transformer) {
        this.soapFaultTransformer = transformer;
    }

    public Message<String> transform(final Message<String> message) {
        try {
            final String payload = message.getPayload();
            final String requestAction = (String) message.getHeaders().get(
                    WSAValidatorConstants.WSA_REQUEST_ACTION);

            if (requestAction == null) {
                throw new NoMessageHeaderException("No message header: "
                        + WSAValidatorConstants.WSA_REQUEST_ACTION);
            }

            final SOAPMessage soapMessage = SOAPMessageUtils.stringToSOAPMessage(payload);
            SOAPMessageUtils.addHeader(soapMessage, AddressingConstants.WSA_ACTION,
                    AddressingConstants.WSA_NS, AddressingConstants.WSA_PREFIX, requestAction
                            + WSAValidatorConstants.WSA_RESPONSE_SUFFIX);
            final String result = SOAPMessageUtils.messageToString(soapMessage);
            return MessageBuilder.createMessage(result, message.getHeaders());
        } catch (SOAPException | IOException | NoMessageHeaderException exception) {
            LOG.error("Error adding wsa:action on response message", exception);
            final Collection<String> keys = message.getHeaders().keySet();
            final Map<String, Object> headers = new HashMap<String, Object>(message.getHeaders()
                    .size() + 1);
            for (final String key : keys) {
                final Object value = message.getHeaders().get(key);
                headers.put(key, value);
            }
            headers.put(WSAValidatorConstants.ERROR_HEADER_NAME,
                    WSAValidatorConstants.INTERNAL_SERVER_ERROR);
            final Message<String> errorMessage = MessageBuilder.fromMessage(message)
                    .copyHeaders(headers).build();
            return soapFaultTransformer.transform(errorMessage);
        }
    }

    private class NoMessageHeaderException extends Exception {

        private static final long serialVersionUID = -17246994392672548L;

        public NoMessageHeaderException(final String message) {
            super(message);
        }
    }

}
