package com.di.kit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * http请求
 * 
 * @author di
 */
public class HttpClient {

	/**
	 * 发送一个get请求
	 * 
	 * @param url 地址
	 * @return 服务器响应内容
	 */
	public static String get(String url) {
		try {
			HttpURLConnection con = connect(url, null, null);
			return new String(readFromConnection(con), con.getContentEncoding());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 发送一个post请求
	 * 
	 * @param url 地址
	 * @param part multipart表单对象
	 * @return 服务器响应内容
	 * @return 服务器响应内容
	 */
	public static String post(String url, Multipart part) {
		HttpURLConnection con = null;
		try {
			Map<String, String> head = new HashMap<>();
			byte[] bytes = part.toBytes();
			head.put("Content-Length", String.valueOf(bytes.length));
			head.put("Content-Type", "multipart/form-data; boundary=" + part.boundary());
			con = connect(url, bytes, "POST", head, null);
			return new String(readFromConnection(con), "utf-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	/**
	 * 发送一个post请求
	 * 
	 * @param url 地址
	 * @param data 数据
	 * @return 服务器响应内容
	 */
	public static String post(String url, String data) {
		HttpURLConnection con = null;
		try {
			con = connect(url, data.getBytes("utf-8"), null);
			return new String(readFromConnection(con), "utf-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (con != null) {
				con.disconnect();
			}
		}
	}

	/**
	 * 建立http链接
	 * 
	 * @param url 请求地址
	 * @param body 请求数据
	 * @param sslSocketFactory 安全隧道
	 * @return 链接实例
	 * @throws IOException io异常
	 */
	public static HttpURLConnection connect(String url, byte[] body, SSLSocketFactory sslSocketFactory) throws IOException {
		return connect(url, body, body == null ? "GET" : "POST", null, sslSocketFactory);
	}

	/**
	 * 建立http链接
	 * 
	 * @param url 请求地址
	 * @param body 请求数据
	 * @param method 请求方法：GET/POST/HEAD/OPTIONS/PUT/DELETE/TRACE
	 * @param httpHeads 请求头
	 * @param sslSocketFactory 安全隧道
	 * @return 链接实例
	 * @throws IOException io异常
	 */
	public static HttpURLConnection connect(String url, byte[] body, String method, Map<String, String> httpHeads, SSLSocketFactory sslSocketFactory) throws IOException {
		URL u = new URL(url);
		HttpsURLConnection conn = (HttpsURLConnection) u.openConnection();
		if (sslSocketFactory != null) {
			conn.setSSLSocketFactory(sslSocketFactory);
		}
		if (httpHeads != null) {
			for (String k : httpHeads.keySet()) {
				conn.setRequestProperty(k, httpHeads.get(k));
			}
		}
		conn.setRequestMethod(method);
		if (!"GET".equals(method)) {
			if (body != null && body.length > 0) {
				conn.setRequestProperty("Content-Length", String.valueOf(body.length));
			}
			conn.setDoOutput(true);
			OutputStream outStream = conn.getOutputStream();
			outStream.write(body);
			outStream.flush();
			outStream.close();
		}
		conn.setConnectTimeout(5000);
		return conn;
	}

	/**
	 * 导入证书
	 * 
	 * @param sec 证书输入流
	 * @param key 密钥
	 * @return 安全的通道
	 * @throws Exception 异常
	 */
	public static SSLSocketFactory loadCertificate(InputStream sec, String key) throws Exception {
		SSLContext sslContext = SSLContext.getInstance("TLS");
		TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(sec, key.toCharArray());
		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		keyManagerFactory.init(keyStore, key.toCharArray());
		sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
		return sslContext.getSocketFactory();
	}

	/**
	 * 从http链接实例中读取返回
	 * 
	 * @param conn 链接
	 * @return 字节流
	 * @throws IOException 输入输出异常
	 */
	public static byte[] readFromConnection(HttpURLConnection conn) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] bytes = new byte[256];
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			while (conn.getInputStream().read(bytes) > 0) {
				out.write(bytes);
			}
		} else {
			while (conn.getErrorStream().read(bytes) > 0) {
				out.write(bytes);
			}
		}
		conn.disconnect();
		return out.toByteArray();
	}

	/**
	 * 从流中读取
	 * 
	 * @param input 输入流
	 * @return 字节
	 * @throws IOException 输入输出异常
	 */
	public static byte[] readFromStream(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[256];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} finally {
			input.close();
		}
		return output.toByteArray();
	}
}
