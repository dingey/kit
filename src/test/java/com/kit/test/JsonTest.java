package com.kit.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.di.kit.JsonUtil;
import com.di.kit.XmlUtil;

/**
 * @author d
 */
public class JsonTest {
	@Test
	public void test() {
		String json = "{\"animals\":{\"dog\":[{\"name\":\"Rufus\",\"breed\":\"labrador\",\"count\":1,\"twoFeet\":false},{\"name\":\"Marty\",\"breed\":\"whippet\",\"count\":1,\"twoFeet\":false}],\"cat\":{\"name\":\"Matilda\"}}}";
		JsonUtil.createFromJson(json, "com.kit.test");
		Root r = JsonUtil.toObject(json, Root.class);
		System.out.println(r.getAnimals().getDog().get(0).getName());
		System.out.println(r.getAnimals().getCat().getName());
		String xml = "<root><animals><cat><name>Matilda</name></cat><dog><dog><name>Rufus</name><count>1</count><twoFeet>false</twoFeet><breed>labrador</breed></dog><dog><name>Marty</name><count>1</count><twoFeet>false</twoFeet><breed>whippet</breed></dog></dog></animals></root>";
		XmlUtil.createFromXml(xml, "com.kit.test");
		Root rx = XmlUtil.toObject(xml, Root.class);
		System.out.println(rx.getAnimals().getCat().getName());
		List<Root> roots=new ArrayList<>();
		roots.add(rx);
		roots.add(rx);
		System.out.println(JsonUtil.toJson(roots));
		System.out.println(XmlUtil.toXml(roots));
	}
}
