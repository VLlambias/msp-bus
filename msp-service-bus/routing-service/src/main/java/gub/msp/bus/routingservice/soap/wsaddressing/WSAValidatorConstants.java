/**
 * 
 */
package gub.msp.bus.routingservice.soap.wsaddressing;

/**
 * @author Guzman Llambias
 * 
 */
public class WSAValidatorConstants {

    public final static String ERROR_HEADER_NAME = "wsaValidator";

    public final static String WSA_REQUEST_TO = "wsaRequestTo";

    public final static String WSA_REQUEST_ACTION = "wsaRequestAction";

    public final static String WSA_RESPONSE_SUFFIX = "Response";

    public final static String INTERNAL_SERVER_ERROR = "Internal server error";

    public static final String MALFORMED_XML_MESSAGE = "Malformed xml message";

    public static final String INVALID_WSA_TO = "Invalid wsa:To header";

    public static final String VALID_MESSAGE = "Valid addressing message";

    public static final String INVALID_WSA_ACTION = "Invalid wsa:Action header";

    public static final String MALFORMED_SOAP_MESSAGE = "Malformed soap message";

    public static final String SERVICE_NOT_FOUND = "Service not found on registry. Check wsa:To header";

    public static final String DESTINATION_UNREACHABLE = "Destination unreachable: No route can be determined to reach the destination role defined by the WS-Addressing To header";

    public static final String SERVICE_TIMEOUT = "Destination timeout: Could not process destination service response";
}
