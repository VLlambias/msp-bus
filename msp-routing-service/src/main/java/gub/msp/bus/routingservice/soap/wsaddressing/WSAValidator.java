package gub.msp.bus.routingservice.soap.wsaddressing;

import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConstants;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class WSAValidator {

    private static final Logger LOG = LoggerFactory.getLogger(WSAValidator.class);

    private final NamespaceContext nsContext;

    private static final String XPATH_WSA_TO = "/" + SOAPConstants.SOAP_ENV_PREFIX + ":Envelope/"
            + SOAPConstants.SOAP_ENV_PREFIX + ":Header/" + AddressingConstants.WSA_PREFIX + ":"
            + AddressingConstants.WSA_TO;

    private static final String XPATH_WSA_ACTION = "/" + SOAPConstants.SOAP_ENV_PREFIX
            + ":Envelope/" + SOAPConstants.SOAP_ENV_PREFIX + ":Header/"
            + AddressingConstants.WSA_PREFIX + ":" + AddressingConstants.WSA_ACTION;

    public WSAValidator(final NamespaceContext nsContext) {
        this.nsContext = nsContext;
    }

    public String validate(final String message) {
        Document doc;
        try {
            doc = XMLUtils.stringToDocument(message);
        } catch (final ParserConfigurationException e) {
            LOG.error("Not an xml message: " + message, e);
            return WSAValidatorConstants.NOT_XML_MESSAGE;
        } catch (SAXException | IOException e) {
            LOG.error("Error processing soap message: " + message, e);
            return WSAValidatorConstants.INTERNAL_SERVER_ERROR;
        }

        if (!validateWsaTo(doc, nsContext)) {
            LOG.error("Invalid wsaddressing message: " + WSAValidatorConstants.INVALID_WSA_TO);
            return WSAValidatorConstants.INVALID_WSA_TO;
        }
        if (!validateWsaAction(doc, nsContext)) {
            LOG.error("Invalid wsaddressing message: " + WSAValidatorConstants.INVALID_WSA_ACTION);
            return WSAValidatorConstants.INVALID_WSA_ACTION;
        }

        return WSAValidatorConstants.VALID_MESSAGE;
    }

    private boolean validateWsaTo(final Document doc, final NamespaceContext nsContext) {
        return existAndNotEmpty(doc, nsContext, XPATH_WSA_TO);
    }

    private boolean validateWsaAction(final Document doc, final NamespaceContext nsContext) {
        return existAndNotEmpty(doc, nsContext, XPATH_WSA_ACTION);
    }

    private boolean existAndNotEmpty(final Document doc, final NamespaceContext nsContext,
            final String xpathExpr) {
        try {
            final String element = XPathUtils.getStringValue(xpathExpr, nsContext, doc);
            if (element != null && !element.isEmpty()) {
                return true;
            }
            return false;
        } catch (final XPathExpressionException exception) {
            LOG.error("Error in xpath expression:" + xpathExpr, exception);
            return false;
        }
    }
}
