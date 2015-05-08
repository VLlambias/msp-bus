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

    public static final String XPATH_WSA_TO = "/" + SOAPConstants.SOAP_ENV_PREFIX + ":Envelope/"
            + SOAPConstants.SOAP_ENV_PREFIX + ":Header/" + AddressingConstants.WSA_PREFIX + ":"
            + AddressingConstants.WSA_TO;

    private static final String XPATH_WSA_ACTION = "/" + SOAPConstants.SOAP_ENV_PREFIX
            + ":Envelope/" + SOAPConstants.SOAP_ENV_PREFIX + ":Header/"
            + AddressingConstants.WSA_PREFIX + ":" + AddressingConstants.WSA_ACTION;

    private static final String XPATH_SOAP = "/" + SOAPConstants.SOAP_ENV_PREFIX + ":Envelope";

    public WSAValidator(final NamespaceContext nsContext) {
        this.nsContext = nsContext;
    }

    public String validate(final String message) {
        Document doc;
        try {
            doc = XMLUtils.stringToDocument(message);
        } catch (final SAXException e) {
            LOG.error("Not an xml message: " + message, e);
            return WSAValidatorConstants.MALFORMED_XML_MESSAGE;
        } catch (ParserConfigurationException | IOException e) {
            LOG.error("Error processing soap message: " + message, e);
            return WSAValidatorConstants.INTERNAL_SERVER_ERROR;
        }

        if (!validateSoapFormat(doc)) {
            LOG.error("Malformed soap message: " + message);
            return WSAValidatorConstants.MALFORMED_SOAP_MESSAGE;
        }

        if (!validateWsaTo(doc)) {
            LOG.error("Invalid wsaddressing message: " + WSAValidatorConstants.INVALID_WSA_TO);
            return WSAValidatorConstants.INVALID_WSA_TO;
        }
        if (!validateWsaAction(doc)) {
            LOG.error("Invalid wsaddressing message: " + WSAValidatorConstants.INVALID_WSA_ACTION);
            return WSAValidatorConstants.INVALID_WSA_ACTION;
        }

        return WSAValidatorConstants.VALID_MESSAGE;
    }

    private boolean validateSoapFormat(final Document doc) {
        return existAndNotEmpty(doc, XPATH_SOAP);
    }

    private boolean validateWsaTo(final Document doc) {
        return existAndNotEmpty(doc, XPATH_WSA_TO);
    }

    private boolean validateWsaAction(final Document doc) {
        return existAndNotEmpty(doc, XPATH_WSA_ACTION);
    }

    private boolean existAndNotEmpty(final Document doc, final String xpathExpr) {
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
