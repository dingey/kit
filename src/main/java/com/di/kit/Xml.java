package com.di.kit;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * @author d
 */
public class Xml {
	public static <T> String toXml(T t) {
		StringWriter sw = new StringWriter();
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance();
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
}
