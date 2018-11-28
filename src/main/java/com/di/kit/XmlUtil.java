package com.di.kit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

/**
 * @author d
 */
public class XmlUtil {
	private static DocumentBuilderFactory factory;

	public static <T> String toXml(T t) {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(t.getClass());
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.marshal(t, sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}

	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> target) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(target);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
			spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(xml)));
			return (T) unmarshaller.unmarshal(xmlSource);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Map<String, String> fromXml(String xml) {
		try {
			DocumentBuilderFactory factory = safeFactory();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document parse = builder.parse(new ByteArrayInputStream(xml.getBytes()));
			Element element = parse.getDocumentElement();
			Map<String, String> m = new LinkedHashMap<>();
			if (parse.hasChildNodes()) {
				NodeList nodes = element.getChildNodes();
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() == 1) {
						m.put(node.getNodeName(), node.getTextContent());
					}
				}
			}
			return m;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static DocumentBuilderFactory safeFactory() {
		if (factory == null) {
			factory = DocumentBuilderFactory.newInstance();
			try {
				factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
				factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
				factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
				factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
				factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
				factory.setXIncludeAware(false);
				factory.setExpandEntityReferences(false);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return factory;
	}
}
