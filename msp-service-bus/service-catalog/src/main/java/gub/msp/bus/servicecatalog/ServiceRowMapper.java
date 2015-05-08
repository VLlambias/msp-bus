/**
 * 
 */
package gub.msp.bus.servicecatalog;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
public class ServiceRowMapper implements RowMapper<Service> {

    private final static String ENDPOINT_ADDRESS = "endpoint_address";

    @Override
    public Service mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {
        return new Service(resultSet.getString(ENDPOINT_ADDRESS));
    }

}
