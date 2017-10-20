package com.di.kit;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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

	public Node getById(String id) {
		return getById(this.node, id);
	}

	public Node getById(Node nod, String id) {
		List<Node> list = getByAttrValue(nod, "id", id);
		return list == null ? null : list.get(0);
	}

	public List<Node> getByAttrName(String attrName) {
		return this.getByAttrName(this.node, attrName);
	}

	public List<Node> getByAttrName(Node nod, String attrName) {
		return this.getByAttrValue(nod, attrName, null);
	}

	public List<Node> getByAttrValue(String attrName, String attrValue) {
		return this.getByAttrValue(this.node, attrName, attrValue);
	}

	public List<Node> getByAttrValue(Node nod, String attrName, String attrValue) {
		if (nod != null && attrName != null) {
			List<Node> ns = new ArrayList<>();
			if (nod.attributes() != null && nod.attributes().containsKey(attrName)) {
				if (attrValue == null || attrValue.equals(nod.attribute(attrName))) {
					ns.add(nod);
				}
			}
			if (nod.children() != null && nod.children().size() > 0) {
				for (Node n : nod.children()) {
					ns.addAll(getByAttrValue(n, attrName, attrValue));
				}
			}
			return ns;
		}
		return null;
	}

	public List<Node> getByNodeName(String name) {
		return this.getByNodeName(this.node, name);
	}

	public List<Node> getByNodeName(Node node, String name) {
		List<Node> ns = new ArrayList<>();
		if (node.name().equals(name)) {
			ns.add(node);
		} else if (node.children() != null && node.children().size() > 0) {
			for (Node n : node.children()) {
				ns.addAll(getByNodeName(n, name));
			}
		}
		return ns;
	}

	@SuppressWarnings("unchecked")
	public Node fromMapObject(Object mapObject) {
		Node node = new Node();
		node.parent = null;
		if (mapObject.getClass() == LinkedHashMap.class) {
			Map<String, ?> map = (Map<String, ?>) mapObject;
			node.attributes = (LinkedHashMap<String, String>) map.get("element attributes");
			node.name = String.valueOf(map.get("element name"));
			if (map.size() > 0) {
				List<Node> ns = new ArrayList<>();
				for (String k : map.keySet()) {
					if (k.equals("element attributes") || k.equals("element name")) {
						continue;
					}
					Node n = null;
					if (map.get(k).getClass() == LinkedHashMap.class) {
						n = fromMapObject(map.get(k));
						n.parent = node;
						ns.add(n);
					} else if (map.get(k).getClass() == ArrayList.class) {
						List<Object> os = (List<Object>) map.get(k);
						for (Object o : os) {
							n = fromMapObject(o);
							n.parent = node;
							ns.add(n);
						}
					} else {
						node.text = String.valueOf(map.get(k));
					}
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
		private LinkedHashMap<String, String> attributes;
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

		public LinkedHashMap<String, String> attributes() {
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
