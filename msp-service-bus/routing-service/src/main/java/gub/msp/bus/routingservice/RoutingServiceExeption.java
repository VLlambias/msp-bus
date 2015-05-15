/**
 * 
 */
package gub.msp.bus.routingservice;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
public class RoutingServiceExeption extends Exception {

    private static final long serialVersionUID = -5361402566023338692L;

    public RoutingServiceExeption(final String message, final Throwable exception) {
        super(message, exception);
    }

    public RoutingServiceExeption(final String message) {
        super(message);
    }
}
