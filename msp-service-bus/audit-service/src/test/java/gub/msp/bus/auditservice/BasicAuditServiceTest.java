package gub.msp.bus.auditservice;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import gub.msp.bus.auditservice.support.EventRowMapper;

import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
/**
 * 
 */
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Guzman Llambias
 * @since 27/05/2015
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/test-audit-service-app.xml")
public class BasicAuditServiceTest {

    private final static String statement = "select * from events where application = ?";

    @Autowired
    private JdbcTemplate jdbcTemplateBean;

    @Autowired
    private AuditService auditServiceBean;

    @Test
    public void registerEvent() {
        final Date date = new Date();
        final String applicationName = "Sample Application";
        final String username = "eperez";
        final String service = "cnve";
        final String method = "consultarCNVE";
        final String messageId = UUID.randomUUID().toString();
        final Event event = new Event(date, applicationName, username, service, method,
                messageId);
        auditServiceBean.registerEvent(event);

        final Event eventResult = jdbcTemplateBean.query(statement,
                new Object[] { "Sample Application" },
                new EventRowMapper());

        assertThat("Invalid date. ", eventResult.getDate(), equalTo(date));
        assertThat("Invalid application. ", eventResult.getApplication(), equalTo(applicationName));
        assertThat("Invalid username. ", eventResult.getUser(), equalTo(username));
        assertThat("Invalid service. ", eventResult.getService(), equalTo(service));
        assertThat("Invalid method. ", eventResult.getMethod(), equalTo(method));
        assertThat("Invalid messageId. ", eventResult.getMessageId(), equalTo(messageId));
    }
}
