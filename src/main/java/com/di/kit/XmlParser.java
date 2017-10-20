package com.di.kit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author d
 */
public class XmlParser {
	private Map<String, Object> xmlMap;
	private Node node;

	public XmlParser(String xml) {
		parse(xml);
	}

	public XmlParser parse(String xml) {
		this.xmlMap = XmlUtil.toMap(xml);
		this.node = fromMapObject(this.xmlMap);
		return this;
	}

	@SuppressWarnings("unchecked")
	public Node fromMapObject(Object mapObject) {
		Node node = new Node();
		node.parent = null;
		if (mapObject.getClass() == HashMap.class) {
			Map<String, ?> map = (Map<String, ?>) mapObject;
			node.attributes = (HashMap<String, String>) (((List)map.get("element attributes")).get(0));
			node.name = String.valueOf(((List)map.get("element name")).get(0));
			if (map.size() > 0) {
				List<Node> ns = new ArrayList<>();
				for (String k : map.keySet()) {
					if(k.equals("element attributes")||k.equals("element name")){
						continue;
					}
					Node n = null;
					if (map.get(k).getClass() == HashMap.class) {
						n = fromMapObject(map.get(k));
					}else if (map.get(k).getClass() == ArrayList.class) {
						List<Object> os = (List<Object>) map.get(k);
						for (Object o : os) {
							ns.add(fromMapObject(o));
						}
					} else {
						n = fromMapObject(map.get(k));
						n.name = k;
					}
					n.parent = node;
					ns.add(n);
				}
				node.nodes = ns;
			}
		} else {
			node.text = String.valueOf(mapObject);
		}
		return node;
	}

	public Map<String, Object> toMap() {
		return xmlMap;
	}

	public Node rootNode() {
		return node;
	}

	public static class Node {
		private Node parent;
		private HashMap<String, String> attributes;
		private List<Node> nodes;
		private String name;
		private String text;

		public Node() {
		}

		public Node(String name) {
			super();
			this.name = name;
		}

		public String name() {
			return this.name;
		}

		public String text() {
			return this.text;
		}

		public Node parent() {
			return parent;
		}

		public String attribute(String name) {
			return attributes == null ? null : attributes.get(name);
		}

		public HashMap<String, String> attributes() {
			return attributes;
		}

		public List<Node> children() {
			if (nodes == null) {
				nodes = new ArrayList<>();
			}
			return nodes;
		}

		public Node append(Node node) {
			this.children().add(node);
			return this;
		}
	}
}
