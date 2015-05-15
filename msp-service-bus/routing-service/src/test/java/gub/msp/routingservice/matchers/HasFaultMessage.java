/**
 * 
 */
package gub.msp.routingservice.matchers;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * @author Guzman Llambias
 * @since 15/05/2015
 *
 */
public class HasFaultMessage extends TypeSafeMatcher<SOAPMessage> {

    private final String expectedFaultMessage;

    public HasFaultMessage(final String message) {
        this.expectedFaultMessage = message;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("A soap fault");
    }

    @Override
    protected boolean matchesSafely(final SOAPMessage message) {
        try {
            return expectedFaultMessage.equals(message.getSOAPBody().getFault().getFaultString());
        } catch (final SOAPException e) {
            return false;
        }
    }

    @Factory
    public static <T> Matcher<SOAPMessage> hasFaultString(final String message) {
        return new HasFaultMessage(message);
    }

}
