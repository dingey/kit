package kit;

import com.di.kit.IpUtil;

public class IpTest {

	public static void main(String[] args) {
		long i = IpUtil.ipToInt("255.255.255.0");
		System.out.println(i);
		System.out.println(IpUtil.intToIp(i));
	}
}
