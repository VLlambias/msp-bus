/**
 * 
 */
package gub.msp.routingservice.matchers;

import static org.hamcrest.core.IsEqual.equalTo;

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
public class SoapFaultMatcher extends TypeSafeMatcher<SOAPMessage> {

    private final Matcher<String> actorMatcher;

    public SoapFaultMatcher(final Matcher<String> actorMatcher) {
        this.actorMatcher = actorMatcher;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("A soap fault");
    }

    @Override
    protected boolean matchesSafely(final SOAPMessage soapMessage) {
        try {
            return actorMatcher.matches(soapMessage.getSOAPBody().getFault().getFaultActor());
        } catch (final SOAPException e) {
            return false;
        }
    }

    @Factory
    public static <T> Matcher<SOAPMessage> hasSoapActor(final String actor) {
        return new SoapFaultMatcher(equalTo(actor));
    }

}
