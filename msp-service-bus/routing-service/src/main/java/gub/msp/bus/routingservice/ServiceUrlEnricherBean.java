/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidator;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAddressingNamespaceContext;
import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;
import gub.msp.bus.servicecatalog.ConfigurationException;
import gub.msp.bus.servicecatalog.ServiceCatalog;
import gub.msp.bus.servicecatalog.ServiceNotFoundException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 * 
 */
public class ServiceUrlEnricherBean {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceUrlEnricherBean.class);

    private final ServiceCatalog serviceCatalog;

    private final WSAddressingNamespaceContext nsContext;

    public ServiceUrlEnricherBean(final ServiceCatalog serviceCatalog,
            final WSAddressingNamespaceContext nsContext) {
        this.serviceCatalog = serviceCatalog;
        this.nsContext = nsContext;
    }

    public String enrich(final String soapMessage) throws RoutingServiceExeption {

        final String wsaTo = getWsaddressingTo(soapMessage);
        try {
            return serviceCatalog.getServiceEndpointAddress(wsaTo).toString();
        } catch (ServiceNotFoundException | ConfigurationException exception) {
            throw new RoutingServiceExeption("Error querying service catalog for service urn "
                    + wsaTo, exception);
        }
    }

    private String getWsaddressingTo(final String soapMessage) throws RoutingServiceExeption {
        // TODO Colocar en un AddressingMessageTemplate
        try {
            final Document doc = XMLUtils.stringToDocument(soapMessage);
            return XPathUtils.getStringValue(WSAValidator.XPATH_WSA_TO, nsContext, doc);
        } catch (ParserConfigurationException | SAXException | IOException
                | XPathExpressionException exception) {
            LOG.error("Error processing wsa:To header on message: " + soapMessage, exception);
            throw new RoutingServiceExeption("Error processing wsa:To header on message: "
                    + soapMessage, exception);
        }
    }
}
