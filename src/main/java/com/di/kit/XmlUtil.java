package com.di.kit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

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
		T t = null;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(target);
			Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
			t = (T) unmarshaller.unmarshal(new StringReader(xml));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return t;
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
				String FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
				factory.setFeature(FEATURE, true);
				FEATURE = "http://xml.org/sax/features/external-general-entities";
				factory.setFeature(FEATURE, false);
				FEATURE = "http://xml.org/sax/features/external-parameter-entities";
				factory.setFeature(FEATURE, false);
				FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
				factory.setFeature(FEATURE, false);
				factory.setXIncludeAware(false);
				factory.setExpandEntityReferences(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return factory;
	}	
}
