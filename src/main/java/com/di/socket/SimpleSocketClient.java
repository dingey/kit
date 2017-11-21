package com.di.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.di.kit.Json;

/**
 * @author d
 */
public class SimpleSocketClient {
	static InputStream inputStream = null;
	static OutputStream outputStream = null;

	public static void main(String[] args) throws Exception {
		Socket s = new Socket("localhost", 6666);
		long l1 = System.currentTimeMillis();
		request(s);
		long l2 = System.currentTimeMillis();
		System.out.println("总耗时：" + (l2 - l1) + "ms");
		s.close();
	}

	static void request(Socket s) throws UnknownHostException, IOException, ClassNotFoundException {
		ServiceMethod m = new ServiceMethod();
		m.setClassName(UserService.class.getName());
		m.setMethodName("say");
		m.setParamTypes(new String[] { "String.class" });
		m.setParamValues(new Object[] { "alice" });

		long l1 = System.currentTimeMillis();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"));
		bw.write(Json.toJsonString(m));
		bw.flush();
		long l2 = System.currentTimeMillis();
		System.out.println("write:" + (l2 - l1) + "ms:" + Json.toJsonString(m));
		if (!s.isOutputShutdown()) {
			s.shutdownOutput();
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
		StringBuffer buffer = new StringBuffer();
		String line = "";
		while ((line = br.readLine()) != null) {
			buffer.append(line);
		}
		String fromJson = Json.fromJson(buffer.toString(), String.class);
		System.out.println("接收服务端反馈: " + fromJson);
		long l3 = System.currentTimeMillis();
		System.out.println("inputStream:" + (l3 - l2) + "ms");
		bw.close();
		br.close();
		if (!s.isClosed() && !s.isInputShutdown()) {
			s.shutdownInput();
		}
		if (!s.isClosed()) {
			s.shutdownInput();
		}
	}
}
