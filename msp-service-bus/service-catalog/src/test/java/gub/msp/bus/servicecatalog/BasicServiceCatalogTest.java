/**
 * 
 */
package gub.msp.bus.servicecatalog;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Guzman Llambias
 * @since 08/05/2015
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/service-catalog-beans.xml")
public class BasicServiceCatalogTest {

    @Autowired
    private ServiceCatalog serviceCatalogBean;

    @Test
    public void getServiceTest() throws ServiceNotFoundException, ConfigurationException,
            MalformedURLException {
        final String serviceUrn = "http://servicios.msp.gub.uy/servicio";
        final URL expected = new URL("http://localhost:8080/ws");

        final URL url = serviceCatalogBean.getServiceEndpointAddress(serviceUrn);
        assertThat("Wrong url for service: " + url, url, equalTo(expected));
    }

    @Test(expected = ServiceNotFoundException.class)
    public void serviceNotFoundTest() throws ServiceNotFoundException, ConfigurationException,
            MalformedURLException {
        final String serviceUrn = "http://servicios.msp.gub.uy/servicio1";

        serviceCatalogBean.getServiceEndpointAddress(serviceUrn);
    }

    @Test(expected = ConfigurationException.class)
    public void configurationErrorTest() throws ServiceNotFoundException, ConfigurationException,
            MalformedURLException {
        final String serviceUrn = "http://servicios.msp.gub.uy/servicio2";

        serviceCatalogBean.getServiceEndpointAddress(serviceUrn);
    }
}
