package com.di.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import com.di.kit.ClassUtil;

/**
 * @author d
 */
public class SimpleSocketServer implements Runnable, Handler {
	private Socket socket;
	ObjectInputStream objectInputStream = null;
	InputStream inputStream = null;
	ObjectOutputStream objectOutputStream = null;
	OutputStream outputStream = null;

	public SimpleSocketServer(Socket socket) throws IOException {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			// 采用循环不断从Socket中读取客户端发送过来的数据
			while (!this.socket.isClosed()) {
				Object readObject = null;
				inputStream = this.socket.getInputStream();
				objectInputStream = new ObjectInputStream(inputStream);
				readObject = objectInputStream.readObject();

				long l1 = System.currentTimeMillis();
				System.out.println("收：" + Thread.currentThread().getId() + ":" + readObject);
				Object writeObject = handler(readObject);
				long l2 = System.currentTimeMillis();
				System.out.println("耗时:" + (l2 - l1) + "ms发:" + Thread.currentThread().getId() + ":" + writeObject);

				outputStream = this.socket.getOutputStream();
				objectOutputStream = new ObjectOutputStream(outputStream);
				objectOutputStream.writeObject(writeObject);
				objectOutputStream.flush();
				if (!this.socket.isOutputShutdown()) {
					objectOutputStream.close();
					outputStream.close();
				}
				if (!this.socket.isInputShutdown()) {
					inputStream.close();
					objectInputStream.close();
				}
			}
		} catch (IOException | ClassNotFoundException e) {
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
