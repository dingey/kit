package kit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Test;

import com.di.socket.ServiceMethod;
import com.di.socket.UserService;

/**
 * @author d
 */
public class ObjectSerilizeTest {
	@Test
	public void test() throws IOException, ClassNotFoundException {
		ServiceMethod m = new ServiceMethod();
		m.setClassName(UserService.class.getName());
		m.setMethodName("say");
		m.setParamTypes(new String[] { "String.class" });
		m.setParamValues(new String[] { "alice" });
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		long l1 = System.currentTimeMillis();
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		out.writeObject(m);
		out.flush();
		long l2 = System.currentTimeMillis();
		System.out.println("ObjectOutputStream:" + (l2 - l1) + "ms");
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(outputStream.toByteArray()));
		Object readObject = in.readObject();
		System.out.println(readObject);
		long l3 = System.currentTimeMillis();
		System.out.println("ObjectInputStream:" + (l3 - l2) + "ms");
	}
}
