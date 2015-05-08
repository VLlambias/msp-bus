/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.WSActionEnricherRouter;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-beans.xml")
public class WSActionEnricherRouterTest {

    private static final String WSA_TO_URI = "http://servicios.msp.gub.uy/servicio";

    private static final String EMPTY_ACTION = "";

    private static final String WSA_ACTION_URI = "http://servicios.msp.gub.uy/servicio/metodo";

    @Autowired
    private WSActionEnricherRouter wsaActionEnricherRouterBean;

    @Test
    public void routeNoActionHeader() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String message = SOAPMessageUtils.messageToString(soapMessage);
        final String channel = wsaActionEnricherRouterBean.route(message);
        assertThat("Wrong channel: " + channel, channel,
                equalTo(WSActionEnricherRouter.NO_WSA_ACTION));
    }

    @Test
    public void routeValidActionHeader() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO_URI,
                WSA_ACTION_URI);
        final String message = SOAPMessageUtils.messageToString(soapMessage);
        final String channel = wsaActionEnricherRouterBean.route(message);
        assertThat("Wrong channel: " + channel, channel,
                equalTo(WSActionEnricherRouter.WSA_ACTION_AVAILABLE));
    }

    @Test
    public void routeEmptyActionHeader() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO_URI,
                EMPTY_ACTION);
        final String message = SOAPMessageUtils.messageToString(soapMessage);
        final String channel = wsaActionEnricherRouterBean.route(message);
        assertThat("Wrong channel: " + channel, channel,
                equalTo(WSActionEnricherRouter.WSA_ACTION_AVAILABLE));
    }
}
