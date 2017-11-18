package com.di.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author d
 */
public class SimpleSocketClient {
	ObjectInputStream objectInputStream = null;
	InputStream inputStream = null;
	ObjectOutputStream objectOutputStream = null;
	OutputStream outputStream = null;

	public static void main(String[] args) throws Exception {
		Socket s = new Socket("localhost", 6666);
		long l1 = System.currentTimeMillis();
		request(s);
		long l2 = System.currentTimeMillis();
		System.out.println((l2 - l1) + "ms");
		s.close();
	}

	static void request(Socket s) throws UnknownHostException, IOException, ClassNotFoundException {
		ServiceMethod m = new ServiceMethod();
		m.setClassName(UserService.class.getName());
		m.setMethodName("say");
		m.setParamTypes(new String[] { "String.class" });
		m.setParamValues(new Object[] { "alice" });
		long l1 = System.currentTimeMillis();
		ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
		out.writeObject(m);
		out.flush();
		long l2 = System.currentTimeMillis();
		System.out.println("ObjectOutputStream:" + (l2 - l1) + "ms");
		ObjectInputStream in = new ObjectInputStream(s.getInputStream());
		Object readObject = in.readObject();
		System.out.println(readObject);
		long l3 = System.currentTimeMillis();
		System.out.println("ObjectInputStream:" + (l3 - l2) + "ms");
	}
}
