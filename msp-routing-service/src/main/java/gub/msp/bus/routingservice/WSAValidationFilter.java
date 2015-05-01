/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import org.springframework.messaging.Message;

/**
 * @author Guzman Llambias
 * 
 */
public class WSAValidationFilter {

    public boolean filter(final Message<String> message) {
        final String errorHeader = (String) message.getHeaders().get(
                WSAValidatorConstants.ERROR_HEADER_NAME);
        return errorHeader.equals(WSAValidatorConstants.VALID_MESSAGE);
    }
}