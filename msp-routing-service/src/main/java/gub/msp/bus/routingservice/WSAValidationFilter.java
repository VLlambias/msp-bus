/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidator;

import org.springframework.messaging.Message;

/**
 * @author Guzman Llambias
 * 
 */
public class WSAValidationFilter {

    public boolean filter(Message<String> message) {
        return message.getHeaders().get(WSAValidator.ERROR_HEADER_NAME) == null;
    }
}
