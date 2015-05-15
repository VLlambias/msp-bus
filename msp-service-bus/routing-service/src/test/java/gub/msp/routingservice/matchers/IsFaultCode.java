/**
 * 
 */
package gub.msp.routingservice.matchers;

import gub.msp.bus.routingservice.soap.SOAPMessageUtils;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
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
public class IsFaultCode extends TypeSafeMatcher<SOAPMessage> {

    private final QName faultCode;

    public IsFaultCode(final QName faultCode) {
        super();
        this.faultCode = faultCode;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("A soap fault");
    }

    @Override
    protected boolean matchesSafely(final SOAPMessage message) {
        try {
            return faultCode.equals(message.getSOAPBody().getFault().getFaultCodeAsQName());
        } catch (final SOAPException e) {
            return false;
        }
    }

    @Factory
    public static <T> Matcher<SOAPMessage> isClientCodeFault() {
        return new IsFaultCode(new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE,
                SOAPMessageUtils.CLIENT_CODE, SOAPConstants.SOAP_ENV_PREFIX));
    }

    @Factory
    public static <T> Matcher<SOAPMessage> isServerCodeFault() {
        return new IsFaultCode(new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
                SOAPMessageUtils.SERVER_CODE, SOAPConstants.SOAP_ENV_PREFIX));
    }

}
