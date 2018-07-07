package kit;


import com.di.kit.IdGenerator;
import com.di.kit.IdWorker;

/**
 * @author di
 */
public class IdTest {

	public static void main(String[] args) {
		IdGenerator.nextId();
		long s1 = System.currentTimeMillis();
		long l1 = IdGenerator.nextId();
		System.out.println(to(l1) + " -> " + to(Long.toHexString(l1)) + " , " + to(Long.toString(l1, 32)));
		long l2 = 991115113429999999L;
		System.out.println(to(l2) + " -> " + to(Long.toHexString(l2)) + " , " + to(Long.toString(l2, 32)));
		long l3 = IdWorker.nextId();
		System.out.println(to(l3) + " -> " + to(Long.toHexString(l3)) + " , " + to(Long.toString(l3, 32)));
		System.out.println(Long.parseLong("4nvc9905rqm1", 32));
		long s2 = System.currentTimeMillis();
		System.out.println((s2 - s1) + "s");
	}

	static String to(Object o) {
		return String.valueOf(o) + "[" + String.valueOf(o).length() + "]";
	}
}
