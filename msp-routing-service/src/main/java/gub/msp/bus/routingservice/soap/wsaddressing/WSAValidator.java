package gub.msp.bus.routingservice.soap.wsaddressing;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.xml.NamespaceContextFactory;
import gub.msp.bus.routingservice.xml.XMLUtils;
import gub.msp.bus.routingservice.xml.XPathUtils;

import java.io.IOException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class WSAValidator {

    private static final Logger logger = LoggerFactory.getLogger(WSAValidator.class);

    public final static String ERROR_HEADER_NAME = "error";

    private final static String INTERNAL_SERVER_ERROR = "Internal server error";

    private final static String XPATH_WSA_TO = "/soap:Envelope/soap:Header/wsa:To";

    public Message<String> validate(Message<String> message) {
        String soapMsg = message.getPayload();
        Document doc;

        try {
            doc = XMLUtils.stringToDocument(soapMsg);
        } catch (ParserConfigurationException e) {
            logger.error("Not an xml message: " + soapMsg, e);
            return notXmlMessageError();
        } catch (SAXException | IOException e) {
            logger.error("Error processing soap message: " + soapMsg, e);
            return internalError();
        }

        NamespaceContext nsContext = NamespaceContextFactory.getNamespaceContext();

        if (!validateWsaTo(doc, nsContext)) {
            logger.error("Invalid wsaddressing message.");
            return internalError();
        }

        return message;
    }

    private boolean validateWsaTo(Document doc, NamespaceContext nsContext) {
        try {
            String wsaTo = XPathUtils.getStringValue(XPATH_WSA_TO, nsContext, doc);
            if (wsaTo != null && !wsaTo.isEmpty())
                return true;
            return false;
        } catch (XPathExpressionException exception) {
            logger.error("Error in xpath expression:" + exception.getMessage(), exception);
            return false;
        }
    }

    private Message<String> internalError() {
        String errorMessage = INTERNAL_SERVER_ERROR;

        try {
            SOAPMessage message = SOAPMessageUtils.buildSoapFault(SOAPMessageUtils.CLIENT_CODE, errorMessage,
                    SOAPMessageUtils.BUS_ACTOR_URI);
            String strMsg = SOAPMessageUtils.messageToString(message);
            return buildErrorMessage(strMsg, errorMessage);
        } catch (SOAPException | IOException e) {
            logger.error("Error creating soap fault: " + errorMessage, e);
            return buildErrorMessage("", errorMessage);
        }
    }

    private Message<String> buildErrorMessage(String message, String errorMessage) {
        return MessageBuilder.withPayload(message)
                .setHeader(HttpHeaders.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR)
                .setHeader(ERROR_HEADER_NAME, errorMessage)
                .build();
    }

    private Message<String> notXmlMessageError() {
        return MessageBuilder.withPayload("").setHeader(ERROR_HEADER_NAME, true).build();
    }

    private Message<String> wsaErrorMessage() {
        return MessageBuilder.withPayload("").setHeader(ERROR_HEADER_NAME, true).build();
    }
}
