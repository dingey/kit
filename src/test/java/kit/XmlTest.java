package kit;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.di.kit.Xml;

/**
 * @author d
 */
public class XmlTest {
	@SuppressWarnings("unused")
	@Test
	public void test() {
		Man m = new Man();
		m.setId(1);
		m.setCreate(new Date());
		m.setN("alice");
		m.setNl(Arrays.asList("bob"));
		m.setNs(new String[] { "Chaly" });
		// String xml = XmlUtil.toXml(m);
		// System.out.println(xml);
		// Man object = XmlUtil.toObject(xml, Man.class);
		String xml = "<man n  = \"a!=null and a!=''\" m = \"c\" ><n>a</n></man>";
		String e = xml.substring(xml.indexOf("<"), xml.indexOf(">") + 1).replaceAll(" +", " ").replaceAll(" +=", "=")
				.replaceAll("= +", "=").replaceAll("< +", "<").replaceAll(" +>", ">");
		String n = e.substring(1, e.indexOf(" ") > 0 ? e.indexOf(" ") : (e.length() - 1));
		String att = e.substring(n.length() + 2, e.length() - 1);
		System.out.println(n + ":" + e);
		System.out.println(att);
		Man man = new Xml().toObject(xml, Man.class);

		String xml1 = "<man n=\"aa\"><a><![CDATA[a1<=5]]></a></man><man><a>a2</a></man>";
		String e1 = xml1.substring(xml1.indexOf("<"), xml1.indexOf(">") + 1).replaceAll(" +", " ")
				.replaceAll(" +=", "=").replaceAll("= +", "=").replaceAll("< +", "<").replaceAll(" +>", ">");
		String n1 = e1.substring(1, e1.indexOf(" ") > 0 ? e1.indexOf(" ") : (e1.length() - 1));
		System.out.println(n1 + ":" + e1);
		Object o1 = new Xml().toObject(xml1);
	}

	public static class Man {
		Integer id;
		String n;
		int[] is;
		String[] ns;
		List<String> nl;
		Date create;
		List<Child> cs;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getN() {
			return n;
		}

		public void setN(String n) {
			this.n = n;
		}

		public String[] getNs() {
			return ns;
		}

		public void setNs(String[] ns) {
			this.ns = ns;
		}

		public List<String> getNl() {
			return nl;
		}

		public void setNl(List<String> nl) {
			this.nl = nl;
		}

		public Date getCreate() {
			return create;
		}

		public void setCreate(Date create) {
			this.create = create;
		}

	}

	public static class Child {
		private String n;
		int[] is;
		String[] ns;

		public String getN() {
			return n;
		}

		public void setN(String n) {
			this.n = n;
		}
	}
}
