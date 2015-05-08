/**
 * 
 */
package gub.msp.bus.servicecatalog;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
public class Service {

    private final String endpointAddress;

    public Service(final String endpointAddress) {
        super();
        this.endpointAddress = endpointAddress;
    }

    public String getEndpointAddress() {
        return endpointAddress;
    }

}
