/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidator;
import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;
import gub.msp.bus.servicecatalog.ConfigurationException;
import gub.msp.bus.servicecatalog.ServiceCatalog;
import gub.msp.bus.servicecatalog.ServiceNotFoundException;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 * 
 */
public class ServiceUrlEnricherBean {

    private final ServiceCatalog serviceCatalog;

    private final NamespaceContext nsContext;

    public ServiceUrlEnricherBean(final ServiceCatalog serviceCatalog,
            final NamespaceContext nsContext) {
        this.serviceCatalog = serviceCatalog;
        this.nsContext = nsContext;
    }

    public String enrich(final String soapMessage) throws RoutingServiceExeption,
            ServiceNotFoundException {

        final String wsaTo = getWsaddressingTo(soapMessage);
        try {
            return serviceCatalog.getServiceEndpointAddress(wsaTo).toString();
        } catch (final ConfigurationException exception) {
            throw new RoutingServiceExeption("Error querying service catalog for service urn '"
                    + wsaTo + "'", exception);
        }
    }

    private String getWsaddressingTo(final String soapMessage) throws RoutingServiceExeption {
        // TODO Colocar en un AddressingMessageTemplate
        try {
            final Document doc = XMLUtils.stringToDocument(soapMessage);
            final String wsaToHeader = XPathUtils.getStringValue(WSAValidator.XPATH_WSA_TO,
                    nsContext, doc);
            if (wsaToHeader.isEmpty()) {
                throw new RoutingServiceExeption("Error processing wsa:To header on message: "
                        + soapMessage);
            }
            return wsaToHeader;
        } catch (ParserConfigurationException | SAXException | IOException
                | XPathExpressionException exception) {
            throw new RoutingServiceExeption("Error processing wsa:To header on message: "
                    + soapMessage, exception);
        }
    }
}
