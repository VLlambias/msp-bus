/**
 * 
 */
package gub.msp.bus.routingservice;

import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
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
public class WSActionEnricherRouter {

    private static final Logger LOG = LoggerFactory.getLogger(WSActionEnricherRouter.class);

    public static final String INTERNAL_ERROR = "Internal error";

    public static final String NO_WSA_ACTION = "No wsa:action";

    public static final String WSA_ACTION_AVAILABLE = "Available wsa:action";

    private final String xpathExpression;

    private final NamespaceContext nsContext;

    public WSActionEnricherRouter(final NamespaceContext nsContext, final String xpathExpression) {
        this.nsContext = nsContext;
        this.xpathExpression = xpathExpression;
    }

    public String route(final String soapMessage) {
        try {
            final Document doc = XMLUtils.stringToDocument(soapMessage);
            final String result = XPathUtils.getStringValue(xpathExpression, nsContext, doc);
            LOG.debug("xpath result: " + result);
            if (result == null || result.isEmpty()) {
                return NO_WSA_ACTION;
            }
            return WSA_ACTION_AVAILABLE;
        } catch (final XPathExpressionException | ParserConfigurationException | SAXException
                | IOException exception) {
            LOG.error("Error reading wsa:Action from message: " + soapMessage, exception);
            return INTERNAL_ERROR;
        }
    }
}
