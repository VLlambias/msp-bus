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
 * @since 07/05/2015
 * 
 */
public class IsSoapFault extends TypeSafeMatcher<SOAPMessage> {

    @Override
    public void describeTo(final Description description) {
        description.appendText("A soap fault");
    }

    @Override
    protected boolean matchesSafely(final SOAPMessage message) {
        try {
            return message.getSOAPBody().hasFault();
        } catch (final SOAPException e) {
            return false;
        }
    }

    @Factory
    public static <T> Matcher<SOAPMessage> isSoapFault() {
        return new IsSoapFault();
    }

}
