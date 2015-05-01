package gub.msp.bus.routingservice.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author Guzman Llambias
 * 
 */
public class XMLUtils {

    public static Document stringToDocument(String string) throws ParserConfigurationException,
            UnsupportedEncodingException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new ByteArrayInputStream(string.getBytes("utf-8"))));
    }

}
