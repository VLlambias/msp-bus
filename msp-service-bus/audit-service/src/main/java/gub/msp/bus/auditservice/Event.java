/**
 * 
 */
package gub.msp.bus.auditservice;

import java.util.Date;

/**
 * @author Guzman Llambias
 * @since 27/05/2015
 *
 */
public class Event {

    private final Date date;

    private final String application;

    private final String user;

    private final String service;

    private final String method;

    private final String messageId;

    public Event(final Date date, final String application, final String user,
            final String service, final String method, final String messageId) {
        super();
        this.date = date;
        this.application = application;
        this.user = user;
        this.service = service;
        this.method = method;
        this.messageId = messageId;
    }

    public Date getDate() {
        return date;
    }

    public String getApplication() {
        return application;
    }

    public String getUser() {
        return user;
    }

    public String getService() {
        return service;
    }

    public String getMethod() {
        return method;
    }

    public String getMessageId() {
        return messageId;
    }

}
