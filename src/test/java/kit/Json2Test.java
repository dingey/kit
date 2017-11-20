package kit;

import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.di.kit.Json;

/**
 * @author di
 */
public class Json2Test {
	@SuppressWarnings("unused")
	@Test
	public void test() {
		Json j = new Json();
		String j1 = "[1,2]";
		// Object[] object = j.toObject(j1, Object[].class);
		// Object o1 = j.toObject(j1);
		// String j2 = "[[1],[1]]";
		// Object o2 = j.toObject(j2);
		// j.toObject(j2, List.class);
		String j3 = "{\"id\":9,\"n\":\"alice\",\"ns\":[\"a\"]}";
		Man o3 = j.toObject(j3, Man.class);
	}

	public static class Man {
		int id;
		String n;
		int[]is;
		String[] ns;
		List<String> nl;
		Date create;

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
}
