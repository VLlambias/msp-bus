/**
 * 
 */
package gub.msp.bus.routingservice;

/**
 * @author Guzman Llambias
 * @since 13/05/2015
 *
 */
public class InvalidWSAddressingHeader extends Exception {

    private static final long serialVersionUID = -8472564479134555729L;

    public InvalidWSAddressingHeader(final String messsage) {
        super(messsage);
    }
}
