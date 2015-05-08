/**
 * 
 */
package gub.msp.bus.servicecatalog;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
public class ServiceNotFoundException extends Exception {

    private static final long serialVersionUID = 976627719836963733L;

    public ServiceNotFoundException(final String message) {
        super(message);
    }
}
