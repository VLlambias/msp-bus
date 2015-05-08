/**
 * 
 */
package gub.msp.routingservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import gub.msp.bus.routingservice.SoapFaultTransformer;
import gub.msp.bus.routingservice.soap.SOAPMessageUtils;
import gub.msp.bus.routingservice.soap.wsaddressing.WSAValidatorConstants;

import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.integration.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-beans.xml")
public class SOAPFaultTransformerTest {

    @Autowired
    private SoapFaultTransformer soapFaultTransformerBean;

    @Test
    public void invalidWsaTo() throws SOAPException, ParserConfigurationException, SAXException,
            IOException {
        final String wsaTo = "http://localhost:18080/routing-service/services/router/rucaf";
        final String wsaAction = "http://localhost:18080/routing-service/services/router/rucaf/method";

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(wsaTo, wsaAction);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        final Message<String> message = MessageBuilder
                .withPayload(strMessage)
                .setHeader(WSAValidatorConstants.ERROR_HEADER_NAME,
                        WSAValidatorConstants.INVALID_WSA_TO).build();

        final Message<String> resultMessage = soapFaultTransformerBean.transform(message);

        final String strResponse = resultMessage.getPayload();
        final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(strResponse);
        final SOAPFault soapFault = soapResponse.getSOAPBody().getFault();
        final QName clientCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
                SOAPMessageUtils.CLIENT_CODE);

        assertThat("Http status code not 500",
                (HttpStatus) resultMessage.getHeaders().get(HttpHeaders.STATUS_CODE),
                equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat("No soap fault found on message", soapResponse, notNullValue());
        assertThat("Soap code fault is not " + SOAPMessageUtils.CLIENT_CODE,
                soapFault.getFaultCodeAsQName(), equalTo(clientCode));
        assertThat("Soap actor is not " + SOAPMessageUtils.BUS_ACTOR_URI,
                soapFault.getFaultActor(), equalTo(SOAPMessageUtils.BUS_ACTOR_URI));
        assertThat("Invalid soap subcode: " + soapFault.getFaultString(),
                soapFault.getFaultString(), equalTo("Invalid wsa:To header"));
    }

    @Test
    public void invalidWsaAction() throws SOAPException, ParserConfigurationException,
            SAXException, IOException {
        final String wsaTo = "http://localhost:18080/routing-service/services/router/rucaf";
        final String wsaAction = "http://localhost:18080/routing-service/services/router/rucaf/method";

        final SOAPMessage soapMessage = SOAPMessageTestingUtils.wsaTestMessage(wsaTo, wsaAction);
        final String strMessage = SOAPMessageUtils.messageToString(soapMessage);
        final Message<String> message = MessageBuilder
                .withPayload(strMessage)
                .setHeader(WSAValidatorConstants.ERROR_HEADER_NAME,
                        WSAValidatorConstants.INVALID_WSA_ACTION).build();

        final Message<String> resultMessage = soapFaultTransformerBean.transform(message);

        final String strResponse = resultMessage.getPayload();
        final SOAPMessage soapResponse = SOAPMessageUtils.stringToSOAPMessage(strResponse);
        final SOAPFault soapFault = soapResponse.getSOAPBody().getFault();
        final QName clientCode = new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE,
                SOAPMessageUtils.CLIENT_CODE);

        assertThat("Http status code not 500",
                (HttpStatus) resultMessage.getHeaders().get(HttpHeaders.STATUS_CODE),
                equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat("No soap fault found on message", soapResponse, notNullValue());
        assertThat("Soap code fault is not " + SOAPMessageUtils.CLIENT_CODE,
                soapFault.getFaultCodeAsQName(), equalTo(clientCode));
        assertThat("Soap actor is not " + SOAPMessageUtils.BUS_ACTOR_URI,
                soapFault.getFaultActor(), equalTo(SOAPMessageUtils.BUS_ACTOR_URI));
        assertThat("Invalid soap subcode: " + soapFault.getFaultString(),
                soapFault.getFaultString(), equalTo(WSAValidatorConstants.INVALID_WSA_ACTION));
    }
}
