package com.di.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import com.di.kit.ClassUtil;
import com.di.kit.Json;

/**
 * @author d
 */
public class SimpleSocketServer implements Runnable, Handler {
	private Socket socket;
	InputStream inputStream = null;
	OutputStream outputStream = null;
	ByteArrayInputStream in;
	ByteArrayOutputStream out;

	public SimpleSocketServer(Socket socket) throws IOException {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// 采用循环不断从Socket中读取客户端发送过来的数据
			while (!this.socket.isClosed()) {
				long l1 = System.currentTimeMillis();
				BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
				StringBuffer buffer = new StringBuffer();
				String line = "";
				while ((line = br.readLine()) != null) {
					buffer.append(line);
				}
				System.out.println("接收客户端反馈: " + buffer.toString());
				if (!socket.isInputShutdown()) {
					socket.shutdownInput();
				}

				ServiceMethod method = Json.fromJson(buffer.toString(), ServiceMethod.class);

				System.out.println("收：" + Thread.currentThread().getId() + ":" + buffer.toString());
				Object writeObject = handler(method);
				System.out.println("发:" + Thread.currentThread().getId() + ":" + writeObject);

				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
				bw.write(Json.toJsonString(writeObject));
				bw.flush();
				br.close();
				bw.close();
				if (!socket.isClosed() && !socket.isOutputShutdown()) {
					this.socket.shutdownOutput();
				}
				if (!socket.isClosed()) {
					this.socket.close();
				}
				long l2 = System.currentTimeMillis();
				System.out.println("耗时:" + (l2 - l1) + "ms");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	@Override
	public Object handler(Object obj) {
		if (obj instanceof Heartbeat) {
			return Integer.MIN_VALUE;
		} else if (obj instanceof ServiceMethod) {
			ServiceMethod method = (ServiceMethod) obj;
			try {
				Class<?> c = Class.forName(method.getClassName());
				Class<?>[] parameterTypes = null;
				if (method.getParamTypes().length > 0) {
					parameterTypes = new Class[method.getParamTypes().length];
					for (int i = 0; i < parameterTypes.length; i++) {
						parameterTypes[i] = ClassUtil.getByClassName(method.getParamTypes()[i]);
					}
				}
				Method declaredMethod = ClassUtil.getDeclaredMethod(c, method.getMethodName(), parameterTypes);
				return declaredMethod.invoke(c.newInstance(), method.getParamValues());
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | InstantiationException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		@SuppressWarnings("resource")
		ServerSocket ss = new ServerSocket(6666);
		System.out.println("start server");
		while (true) {
			Socket accept = ss.accept();
			System.out.println("start handler request.");
			// new Thread(new SimpleSocketServer(accept)).start();
			new SimpleSocketServer(accept).run();
		}
	}
}
