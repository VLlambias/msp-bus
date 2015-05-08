/**
 * 
 */
package gub.msp.bus.servicecatalog;

import java.net.URL;

/**
 * Provides operations for a generic service catalog
 * 
 * @author Guzman Llambias
 * @since 08/05/2015
 * 
 */
public interface ServiceCatalog {

    /**
     * Returns the endpoint address of a given service
     * 
     * @param service
     *            service name
     * @return endpoint addresss of the service
     * @throws ServiceNotFoundException
     *             when no service was found with service name
     * @throws ConfigurationException
     *             configuration error in service catalog
     */
    URL getServiceEndpointAddress(String service) throws ServiceNotFoundException,
            ConfigurationException;

}
