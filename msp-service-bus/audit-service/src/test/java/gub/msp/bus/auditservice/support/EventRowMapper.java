/**
 * 
 */
package gub.msp.bus.auditservice.support;

import gub.msp.bus.auditservice.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

/**
 * @author Guzman Llambias
 * @since 27/05/2015
 *
 */
public class EventRowMapper implements ResultSetExtractor<Event> {

    /* (non-Javadoc)
     * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
     */
    @Override
    public Event extractData(final ResultSet resultSet) throws SQLException, DataAccessException {
        resultSet.next();
        
        return new Event(new Date(resultSet.getTimestamp("datetime").getTime()),
                resultSet.getNString("application"),
                resultSet.getNString("username"), resultSet.getNString("service"),
                resultSet.getNString("method"), resultSet.getNString("messageId"));
    }

}
