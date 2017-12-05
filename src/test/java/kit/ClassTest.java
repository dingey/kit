package kit;

import java.util.Date;

import org.junit.Test;

import com.di.kit.ClassUtil;
import com.di.kit.Json;

/**
 * @author d
 */
public class ClassTest {
	@Test
	public void test() {
		System.out.println(int.class.isPrimitive());
		System.out.println(Integer.class.isPrimitive());
		System.out.println(ClassUtil.isJdkClass(byte.class));
		System.out.println(ClassUtil.isJdkClass(Byte.class));
		System.out.println(ClassUtil.isJdkClass(short.class));
		System.out.println(ClassUtil.isJdkClass(Short.class));
		System.out.println(ClassUtil.isJdkClass(int.class));
		System.out.println(ClassUtil.isJdkClass(Integer.class));
		System.out.println(ClassUtil.isJdkClass(long.class));
		System.out.println(ClassUtil.isJdkClass(Long.class));
		System.out.println(ClassUtil.isJdkClass(double.class));
		System.out.println(ClassUtil.isJdkClass(Double.class));
		System.out.println(ClassUtil.isJdkClass(float.class));
		System.out.println(ClassUtil.isJdkClass(Float.class));
		System.out.println(ClassUtil.isJdkClass(boolean.class));
		System.out.println(ClassUtil.isJdkClass(Boolean.class));
		System.out.println(ClassUtil.isJdkClass(char.class));
		System.out.println(ClassUtil.isJdkClass(Character.class));
		System.out.println(ClassUtil.isJdkClass(String.class));
		System.out.println(ClassUtil.isJdkClass(Date.class));
		System.out.println(ClassUtil.isJdkClass(Json.class));
	}
}
