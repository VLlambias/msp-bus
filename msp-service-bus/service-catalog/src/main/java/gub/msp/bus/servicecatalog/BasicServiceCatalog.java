/**
 * 
 */
package gub.msp.bus.servicecatalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 * 
 */
public class BasicServiceCatalog implements ServiceCatalog {

    private final JdbcTemplate jdbcTemplate;

    private final static String QUERY_SERVICE_BY_URN = "select endpoint_address from services where urn = ?";

    public BasicServiceCatalog(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public URL getServiceEndpointAddress(final String serviceUrn) throws ServiceNotFoundException,
            ConfigurationException {

        final List<Service> services = jdbcTemplate.query(QUERY_SERVICE_BY_URN,
                new Object[] { serviceUrn }, new ServiceRowMapper());
        if (services.isEmpty()) {
            throw new ServiceNotFoundException("No service found with service_urn '" + serviceUrn
                    + "'");
        }

        final Service service = services.get(0);
        try {
            return new URL(service.getEndpointAddress().trim());
        } catch (final MalformedURLException exception) {
            throw new ConfigurationException("Configuration error in service with service_urn "
                    + serviceUrn, exception);
        }
    }

}
