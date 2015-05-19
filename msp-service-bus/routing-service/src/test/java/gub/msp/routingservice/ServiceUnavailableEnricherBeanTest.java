/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.ServiceUnavailableEnricherBean;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.net.ConnectException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @author Guzman Llambias
 * @since 19/05/2015
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-beans.xml")
public class ServiceUnavailableEnricherBeanTest {

    @Autowired
    private ServiceUnavailableEnricherBean serviceUnavailableEnricherBean;
    
    @Test
    public void http404ErrorTest() {
        final MessageHandlingException exception = new MessageHandlingException(null,
                new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final String value = serviceUnavailableEnricherBean.enrich(exception);
        assertThat("Not a destination unreachable value", value,
                equalTo(WSAValidatorConstants.DESTINATION_UNREACHABLE));
    }

    @Test
    public void http40xErrorTest() {
        final MessageHandlingException exception = new MessageHandlingException(null,
                new HttpClientErrorException(HttpStatus.BAD_GATEWAY));
        final String value = serviceUnavailableEnricherBean.enrich(exception);
        assertThat("Not an internal server error", value,
                equalTo(WSAValidatorConstants.INTERNAL_SERVER_ERROR));
    }

    @Test
    public void httpConnectionExceptionTest() {
        final MessageHandlingException exception = new MessageHandlingException(null,
                new ConnectException());
        final String value = serviceUnavailableEnricherBean.enrich(exception);
        assertThat("Not a destination unreachable value", value,
                equalTo(WSAValidatorConstants.DESTINATION_UNREACHABLE));
    }
}
