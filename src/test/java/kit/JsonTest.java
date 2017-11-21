package kit;

import org.junit.Test;

import com.di.kit.Json;
import com.di.socket.ServiceMethod;
import com.di.socket.UserService;

/**
 * @author di
 */
public class JsonTest {
	@Test
	public void test() {
		ServiceMethod m = new ServiceMethod();
		m.setClassName(UserService.class.getName());
		m.setMethodName("say");
		m.setParamTypes(new String[] { "String.class" });
		m.setParamValues(new Object[] { "alice" });
		String json = Json.toJsonString(m);
		System.out.println(json);
		ServiceMethod object = Json.fromJson(json, ServiceMethod.class);
		System.out.println(Json.toJsonString(object));
	}
}
