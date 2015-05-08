/**
 * 
 */
package gub.msp.routingservice.matchers;

import java.util.Iterator;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Guzman Llambias
 * @since 07/05/2015
 * 
 */
public class SoapHeaderMatcher extends TypeSafeMatcher<SOAPMessage> {

    private final SOAPHeaderElement header;

    public SoapHeaderMatcher(final SOAPHeaderElement element) {
        this.header = element;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("a soap message with headers containing ");
    }

    @Override
    protected boolean matchesSafely(final SOAPMessage message) {
        try {
            @SuppressWarnings("unchecked")
            final Iterator<SOAPElement> iterator = message.getSOAPHeader().getChildElements();
            while (iterator.hasNext()) {
                final SOAPElement element = iterator.next();
                if (header.getNodeName().equals(element.getNodeName())
                        && header.getNamespaceURI().equals(element.getNamespaceURI())
                        && header.getTextContent().equals(element.getTextContent())) {
                    return true;
                }
            }
            return false;
        } catch (final SOAPException exception) {
            return false;
        }
    }

    @Factory
    public static <T> Matcher<SOAPMessage> hasHeader(final SOAPHeaderElement element) {
        return new SoapHeaderMatcher(element);
    }

}
