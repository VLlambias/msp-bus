/**
 * 
 */
package gub.msp.bus.routingservice.xml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

/**
 * @author Guzman Llambias
 * 
 */
public class NamespaceContextFactory {

    private static Map<String, String> namespaces;
    private static Map<String, String> prefixs;

    public static NamespaceContext getNamespaceContext() {
        String addressingNamespace = "http://www.w3.org/2005/08/addressing";
        String addressingPrefix = "wsa";
        String soapPrefix = "soapenv";
        String soapNamespace = "http://schemas.xmlsoap.org/soap/envelope/";

        namespaces = new HashMap<String, String>(2);
        namespaces.put(addressingPrefix, addressingNamespace);
        namespaces.put(soapPrefix, soapNamespace);

        prefixs = new HashMap<String, String>(2);
        prefixs.put(soapNamespace, soapPrefix);
        prefixs.put(addressingNamespace, addressingPrefix);
        return new NamespaceContext() {

            @Override
            public Iterator<?> getPrefixes(String namespaceURI) {
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public String getNamespaceURI(String prefix) {
                if ("wsa".equals(prefix))
                    return "http://www.w3.org/2005/08/addressing";
                if ("soap".equals(prefix))
                    return "http://schemas.xmlsoap.org/soap/envelope/";
                return null;
            }
        };
    }
}
