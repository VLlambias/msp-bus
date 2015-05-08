/**
 * 
 */
package gub.msp.bus.servicecatalog;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
public class ConfigurationException extends Exception {

    private static final long serialVersionUID = 7679196016378247018L;

    public ConfigurationException(final String message, final Throwable exception) {
        super(message, exception);
    }
}
