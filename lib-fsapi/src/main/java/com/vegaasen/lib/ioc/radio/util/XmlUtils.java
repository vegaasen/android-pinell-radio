package com.vegaasen.lib.ioc.radio.util;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public enum XmlUtils {

    INSTANCE;

    public Document getDocument(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTextContentByNode(final Document document, final String nodeName) {
        return (document == null) ? null : document.getElementsByTagName(nodeName).item(0).getTextContent();
    }

    public int getNumberContentByNode(final Document document, final String nodeName) {
        try {
            return (document == null) ? 0 : Integer.parseInt(document.getElementsByTagName(nodeName).item(0).getTextContent());
        } catch (NumberFormatException e) {
            // *gulp*
        }
        return 0;
    }

}
