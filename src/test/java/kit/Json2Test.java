package kit;

import java.util.Date;
import java.util.List;

import com.di.kit.Json;

/**
 * @author di
 */
public class Json2Test {

	public void test() {
		Json j = new Json();
		// String j1 = "[1,2]";
		// Object[] object = j.toObject(j1, Object[].class);
		// Object o1 = j.toObject(j1);
		// String j2 = "[[1],[1]]";
		// Object o2 = j.toObject(j2);
		// j.toObject(j2, List.class);
		String j3 = "{\"id\":9,\"n\":\"alice\",\"create\":2017-11-21 13:56:20,\"ns\":[\"a\"],\"cs\":[{\"n\":\"a\",\"ns\":[\"a\"]}]}";
		Man o3 = j.toObject(j3, Man.class);
		System.out.println(j.toJson(o3));
		String j4 = "{\"cs\":[{\"n\":\"a\"}]}";
		Man o4 = j.toObject(j4, Man.class);
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
