package com.vegaasen.lib.ioc.radio.util;

import com.vegaasen.lib.ioc.radio.adapter.constants.ApiResponse;
import com.vegaasen.lib.ioc.radio.model.response.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum XmlUtils {

    INSTANCE;

    private static final Logger LOG = Logger.getLogger(XmlUtils.class.getSimpleName());
    private static final String EMPTY = "";
    private static final int INIT = -1;

    public Document getDocument(String candidate) {
        if (candidate == null || candidate.isEmpty()) {
            return null;
        }
        InputSource source = new InputSource();
        source.setCharacterStream(new StringReader(candidate));
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
        } catch (SAXException e) {
            LOG.info("Unable to parse the stream due to elements");
        } catch (IOException e) {
            LOG.info("Unable to stream. It could be that the stream has been closed");
        } catch (ParserConfigurationException e) {
            LOG.info("Unable to parse the stream");
        }
        return null;
    }

    public Document getDocument(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        } catch (SAXException e) {
            LOG.info("Unable to parse the stream due to elements");
        } catch (IOException e) {
            LOG.info("Unable to stream. It could be that the stream has been closed");
        } catch (ParserConfigurationException e) {
            LOG.info("Unable to parse the stream");
        }
        return null;
    }

    public int getNumberContentByNode(final Element ele, final String nodeName) {
        try {
            return (ele == null) ? 0 : Integer.parseInt(getTextContentByNode(ele, nodeName));
        } catch (NumberFormatException e) {
            // *gulp*
        }
        return 0;
    }

    public String getTextContentByNode(final Element ele, final String nodeName) {
        return (ele == null) ? EMPTY : ele.getElementsByTagName(nodeName).item(0).getTextContent();
    }

    /**
     * Whenever dealing with elements of type "Item", this stuff is being created into an object. easy-pease
     * Example:
     * --------
     * <fsapiResponse>
     * <status>FS_OK</status>
     * <item key="38">
     * <field name="name"><c8_array>?NRK SUPER       </c8_array></field>
     * <field name="type"><u8>1</u8></field>
     * <field name="subtype"><u8>1</u8></field>
     * </item>
     * <item key="25">
     * <field name="name"><c8_array>?NRK TEST        </c8_array></field>
     * <field name="type"><u8>1</u8></field>
     * <field name="subtype"><u8>1</u8></field>
     * </item>
     * ...
     * </fsapiResponse>
     * <p/>
     *
     * @param ele _
     * @return _
     */
    public Set<Item> getItems(final Element ele) {
        final Set<Item> items = new HashSet<Item>();
        final NodeList candidates = ele.getElementsByTagName(ApiResponse.ITEM);
        if (candidates == null) {
            return items;
        }
        int i = INIT;
        while (i++ != candidates.getLength()) {
            final Map<String, String> fieldValues = new HashMap<String, String>();
            final Element item = (Element) candidates.item(i);
            if (item != null) {
                int found = INIT;
                final NodeList fields = item.getElementsByTagName(ApiResponse.FIELD);
                if (fields != null) {
                    while (found++ != fields.getLength()) {
                        final Element field = (Element) fields.item(found);
                        if (field != null) {
                            if (field.getAttributes().getLength() > 0) {
                                fieldValues.put(field.getAttributes().item(0).getTextContent(), field.getTextContent());
                            }
                        }
                    }
                }
            }
            items.add(Item.create(getItemKey(item), fieldValues));
        }
        return items;
    }

    public String asString(Document candidate) {
        if (candidate == null) {
            return EMPTY;
        }
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(candidate), new StreamResult(sw));
            return sw.toString();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Unable to convert document", e);
            return EMPTY;
        }
    }

    private int getItemKey(final Element item) {
        if (item != null) {
            return Integer.parseInt(item.getAttribute(ApiResponse.ITEM_KEY));
        }
        return 0;
    }

}
