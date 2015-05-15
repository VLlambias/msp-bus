/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.RoutingServiceExeption;
import gub.msp.bus.routingservice.ServiceUrlEnricherBean;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAddressingNamespaceContext;
import gub.msp.bus.servicecatalog.ConfigurationException;
import gub.msp.bus.servicecatalog.ServiceCatalog;
import gub.msp.bus.servicecatalog.ServiceNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 13/05/2015
 *
 */
public class ServiceUrlEnricherBeanTest {

    private ServiceUrlEnricherBean serviceUrlEnricherBean;

    private static final String WSA_TO_URI = "http://servicios.msp.gub.uy/servicio";

    private static final String WSA_ACTION_URI = "http://servicios.msp.gub.uy/servicio/metodo";

    private static final String SERVICE_URL = "http://servicios.msp.gub.uy/servicio2";

    private static final String EMPTY_STRING = "";

    private static final String SERVICE_NOT_FOUND_URN = "SERVICE_NOT_FOUND_URN";

    @Before
    public void setUp() {
        serviceUrlEnricherBean = new ServiceUrlEnricherBean(
                new ServiceCatalog() {

                    @Override
                    public URL getServiceEndpointAddress(final String service)
                            throws ServiceNotFoundException, ConfigurationException {
                try {
                    if (service.equals(WSA_TO_URI)) {
                        return new URL(SERVICE_URL);
                    }
                    throw new ServiceNotFoundException("Service not found");

                } catch (final MalformedURLException e) {
                    throw new ConfigurationException(
                            "Error in test case configuration. Bad wsa:To url", e);
                }
                    }
        }, new WSAddressingNamespaceContext());
    }

    @Test
    public void enrichTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(WSA_TO_URI,
                WSA_ACTION_URI);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        final String value = serviceUrlEnricherBean.enrich(strMessage);

        assertThat("Wrong header value: " + value, value, equalTo(SERVICE_URL));
    }

    @Test(expected = RoutingServiceExeption.class)
    public void noWsaToHeaderTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.basicTestMessage();
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        serviceUrlEnricherBean.enrich(strMessage);
    }

    @Test(expected = RoutingServiceExeption.class)
    public void emptyWsaToHeaderTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(EMPTY_STRING,
                WSA_ACTION_URI);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        serviceUrlEnricherBean.enrich(strMessage);
    }

    @Test(expected = ServiceNotFoundException.class)
    public void serviceNotFoundTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(
                SERVICE_NOT_FOUND_URN, WSA_ACTION_URI);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        serviceUrlEnricherBean.enrich(strMessage);
    }

    @Test(expected = RoutingServiceExeption.class)
    public void invalidSoapMessageTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final String invalidSoapMessage = "<a>not a soap message</a>";
        serviceUrlEnricherBean.enrich(invalidSoapMessage);
    }

    @Test(expected = RoutingServiceExeption.class)
    public void invalidXmlMessageTest() throws RoutingServiceExeption, SOAPException, IOException,
            ParserConfigurationException, SAXException, ServiceNotFoundException {
        final String invalidSoapMessage = "Bad xml";
        serviceUrlEnricherBean.enrich(invalidSoapMessage);
    }
}
