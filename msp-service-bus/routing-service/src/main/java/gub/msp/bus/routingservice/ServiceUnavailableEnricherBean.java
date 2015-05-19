/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.net.ConnectException;

import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Guzman Llambias
 * @since 19/05/2015
 *
 */
public class ServiceUnavailableEnricherBean {

    public String enrich(final MessageHandlingException exception) {
        final Throwable throwable = exception.getMostSpecificCause();
        if (throwable instanceof HttpClientErrorException) {
            final HttpClientErrorException clientError = (HttpClientErrorException) throwable;
            if (HttpStatus.NOT_FOUND.equals(clientError.getStatusCode())) {
                return WSAValidatorConstants.DESTINATION_UNREACHABLE;
            }
        }
        if (throwable instanceof ConnectException) {
            return WSAValidatorConstants.DESTINATION_UNREACHABLE;
        }
        return WSAValidatorConstants.INTERNAL_SERVER_ERROR;
    }
}
