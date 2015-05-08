/**
 * 
 */
package gub.msp.bus.routingservice.soap.wsaddressing;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.soap.SOAPConstants;

/**
 * @author Guzman Llambias
 * 
 */
public class WSAddressingNamespaceContext implements NamespaceContext {

    @Override
    public String getNamespaceURI(final String prefix) {
        if (AddressingConstants.WSA_PREFIX.equals(prefix)) {
            return AddressingConstants.WSA_NS;
        }
        if (SOAPConstants.SOAP_ENV_PREFIX.equals(prefix)) {
            return SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE;
        }
        return null;
    }

    @Override
    public String getPrefix(final String namespaceURI) {
        throw new UnsupportedOperationException("getPrefix not supported");
    }

    @Override
    public Iterator<?> getPrefixes(final String namespaceURI) {
        throw new UnsupportedOperationException("getPrefixes not supported");
    }
}