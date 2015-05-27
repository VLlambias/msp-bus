/**
 * 
 */
package gub.msp.bus.auditservice;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Guzman Llambias
 * @since 27/05/2015
 *
 */
public class BasicAuditService implements AuditService {

    private final JdbcTemplate jdbcTemplate;

    private final static String INSERT_STATEMENT = "insert into events (datetime, application, username, service, method, messageId) values (?, ?, ?, ?, ?, ?)";

    public BasicAuditService(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void registerEvent(final Event event) {
        final Timestamp timestamp = new Timestamp(event.getDate().getTime());
        jdbcTemplate.update(INSERT_STATEMENT, new Object[] {
                timestamp,
                event.getApplication(), event.getUser(), event.getService(), event.getMethod(),
                event.getMessageId() });
    }

}
