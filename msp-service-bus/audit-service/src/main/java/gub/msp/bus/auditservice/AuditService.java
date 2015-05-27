/**
 * 
 */
package gub.msp.bus.auditservice;

/**
 * 
 * @author Guzman Llambias
 * @since 27/05/2015
 * 
 */
public interface AuditService {

    /**
     * Registers an event in the audit service
     * 
     * @param event
     */
    void registerEvent(Event event);
}
