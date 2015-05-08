/**
 * 
 */
package gub.msp.bus.routingservice.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;

/**
 * @author Guzman Llambias
 * 
 */
public class XPathUtils {

    private static Object getValue(final String xpathExpression, final Node node,
            final QName type, final NamespaceContext nsContext) throws XPathExpressionException {

        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(nsContext);
        return xpath.evaluate(xpathExpression, node, type);
    }

    public static String getStringValue(final String xpathExpression,
            final NamespaceContext nsContext, final Node node) throws XPathExpressionException {

        return (String) getValue(xpathExpression, node, XPathConstants.STRING, nsContext);
    }

}
