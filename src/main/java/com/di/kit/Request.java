package com.di.kit;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public interface Request {
	/**
	 * 执行请求
	 * 
	 * @return 请求对象
	 */
	Request execute();

	/**
	 * 返回响应类型
	 * 
	 * @return 响应类型
	 */
	String getContentType();

	/**
	 * 返回响应字节
	 * 
	 * @return 响应文本
	 */
	byte[] returnBytes();

	/**
	 * 返回响应文本
	 *
	 * @return 响应文本
	 */
	String returnContent();

	/**
	 * 返回响应文本
	 * 
	 * @param charset 字符编码
	 * @return 响应文本
	 */
	String returnContent(String charset);

	/**
	 * 设置表单形式的请求
	 * 
	 * @param form 请求字符
	 * @return 请求对象
	 */
	Request body(Map<Object, Object> form);

	/**
	 * 设置表单形式的请求
	 * 
	 * @param str 请求字符
	 * @return 请求对象
	 */
	Request body(Form form);

	/**
	 * 设置字符内容的请求
	 * 
	 * @param str 请求字符
	 * @return 请求对象
	 */
	Request body(String str);

	/**
	 * 设置字节数组的请求
	 * 
	 * @param str 请求字节
	 * @return 请求对象
	 */
	Request body(byte[] reqBytes);

	/**
	 * 设置json的请求
	 * 
	 * @param str json请求内容
	 * @return 请求对象
	 */
	Request json(String str);

	/**
	 * 设置xml的请求
	 * 
	 * @param str 请求内容
	 * @return 请求对象
	 */
	Request xml(String str);

	/**
	 * 设置格式
	 * 
	 * @param str 格式值
	 * @return 请求对象
	 */
	Request setContentType(String str);

	/**
	 * 设置接收的格式
	 * 
	 * @param str text/xml,application/json...
	 * @return 请求对象
	 */
	Request accept(String str);

	/**
	 * 新增请求参数
	 * 
	 * @param k 参数名
	 * @param v 参数值
	 * @return 请求对象
	 */
	Request add(String k, String v);

	/**
	 * 导入证书，必须在execute方法前使用
	 * 
	 * @param stream 证书内容
	 * @param key 秘钥
	 * @return 请求对象
	 */
	Request loadKey(InputStream stream, String key);

	static Request Get(String url) {
		return new GetRequest(url);
	}

	class GetRequest implements Request {
		String url;
		String reqStr;
		URLConnection conn;
		Map<Object, Object> form;
		byte[] readbytes;
		String contentType;
		String requestType;
		String accept;
		SSLSocketFactory sf;

		public GetRequest(String url) {
			this.url = url;
		}

		@Override
		public Request execute() {
			try {
				URL u = new URL(url());
				if (sf != null) {
					HttpsURLConnection conns = (HttpsURLConnection) u.openConnection();
					conns.setSSLSocketFactory(sf);
					conn = conns;
				} else {
					conn = u.openConnection();
				}
				if (this.requestType != null && !this.requestType.isEmpty()) {
					conn.setRequestProperty("Content-Type", requestType);
				}
				if (this.accept != null && !this.accept.isEmpty()) {
					conn.setRequestProperty("Accept", accept);
				}
				conn.connect();
				BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int readCount;
				while ((readCount = in.read(buf, 0, 100)) > 0) {
					out.write(buf, 0, readCount);
				}
				in.close();
				contentType = conn.getContentType();
				readbytes = out.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public String getContentType() {
			return contentType;
		}

		@Override
		public String returnContent() {
			try {
				return new String(readbytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public String returnContent(String charset) {
			try {
				return new String(readbytes, charset);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return null;
		}

		private String url() {
			if (this.reqStr != null && !reqStr.isEmpty()) {
				if (this.url.contains("?")) {
					this.url += "&" + reqStr;
				} else {
					this.url += "?" + reqStr;
				}
			} else if (this.url != null && !this.url.isEmpty() && form != null && !form.isEmpty()) {
				StringBuilder s = new StringBuilder(this.url);
				if (this.url.contains("?")) {
					s.append("&");
				} else {
					s.append("?");
				}
				for (Object k : form.keySet()) {
					s.append(k).append("=").append(StringUtil.urlEncode(form.get(k).toString(), "utf-8")).append("&");
				}
				this.url = s.toString();
			}
			return this.url;
		}

		@Override
		public Request body(Map<Object, Object> form) {
			if (this.form == null) {
				this.form = form;
			} else {
				this.form.putAll(form);
			}
			return this;
		}

		@Override
		public Request body(Form form) {
			return body(form.build());
		}

		@Override
		public Request body(String str) {
			this.reqStr = str;
			return this;
		}

		@Override
		public Request body(byte[] reqBytes) {
			throw new RuntimeException("GET方法不支持字节数组");
		}

		@Override
		public Request json(String str) {
			throw new RuntimeException("GET方法不支持JSON");
		}

		@Override
		public Request xml(String str) {
			throw new RuntimeException("GET方法不支持xml");
		}

		@Override
		public Request setContentType(String requestType) {
			this.requestType = requestType;
			return this;
		}

		@Override
		public Request accept(String accept) {
			this.accept = accept;
			return this;
		}

		@Override
		public Request add(String k, String v) {
			if (this.form == null)
				this.form = new LinkedHashMap<>();
			this.form.put(k, v);
			return this;
		}

		@Override
		public byte[] returnBytes() {
			return readbytes;
		}

		@Override
		public Request loadKey(InputStream stream, String key) {
			try {
				KeyStore ks = KeyStore.getInstance("pkcs12");
				ks.load(stream, key.toCharArray());
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				kmf.init(ks, key.toCharArray());
				TrustManager[] tm = { new X509TrustManager() {
					@Override
					public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					@Override
					public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					}

					@Override
					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				} };
				SSLContext ctx = SSLContext.getInstance("SSL");
				ctx.init(kmf.getKeyManagers(), tm, new SecureRandom());
				this.sf = ctx.getSocketFactory();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			return this;
		}
	}

	class PostRequest extends GetRequest {
		public PostRequest(String url) {
			super(url);
		}

		byte[] reqBytes;

		@Override
		public Request execute() {
			try {
				URL u = new URL(url);
				if (sf != null) {
					HttpsURLConnection conns = (HttpsURLConnection) u.openConnection();
					conns.setSSLSocketFactory(sf);
					conn = conns;
				} else {
					conn = u.openConnection();
				}
				if (this.requestType != null && !this.requestType.isEmpty()) {
					conn.setRequestProperty("Content-Type", requestType);
				}
				if (this.accept != null && !this.accept.isEmpty()) {
					conn.setRequestProperty("Accept", accept);
				}
				conn.setDoOutput(true);
				conn.setDoInput(true);
				BufferedOutputStream outputStream = new BufferedOutputStream(conn.getOutputStream());
				outputStream.write(requestBody());
				outputStream.flush();
				outputStream.close();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
				byte[] buf = new byte[128];
				int readCount;
				while ((readCount = in.read(buf, 0, 100)) > 0) {
					out.write(buf, 0, readCount);
				}
				in.close();
				contentType = conn.getContentType();
				readbytes = out.toByteArray();
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public Request body(Map<Object, Object> form) {
			this.form = form;
			return this;
		}

		@Override
		public Request body(String str) {
			try {
				this.reqBytes = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public Request body(byte[] reqBytes) {
			this.reqBytes = reqBytes;
			return this;
		}

		@Override
		public Request json(String str) {
			this.requestType = "application/json";
			try {
				this.reqBytes = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		@Override
		public Request xml(String str) {
			this.requestType = "text/xml";
			try {
				this.reqBytes = str.getBytes("utf-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			return this;
		}

		private byte[] requestBody() {
			if (this.url != null && !this.url.isEmpty() && form != null && !form.isEmpty()) {
				StringBuilder s = new StringBuilder();
				for (Object k : form.keySet()) {
					s.append(k).append("=").append(form.get(k)).append("&");
				}
				try {
					this.reqBytes = s.toString().getBytes("utf-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			return reqBytes;
		}

		@Override
		public byte[] returnBytes() {
			return readbytes;
		}

	}

	interface Form {
		Form add(String name, String value);

		Map<Object, Object> build();

		class FormData extends LinkedHashMap<Object, Object> implements Form {
			private static final long serialVersionUID = -4119390665446925457L;

			private FormData() {
			}

			@Override
			public Form add(String name, String value) {
				this.put(name, value);
				return this;
			}

			@Override
			public Map<Object, Object> build() {
				return this;
			}
		}

		static Form form() {
			return new FormData();
		}
	}

	static Request Post(String url) {
		return new PostRequest(url);
	}
}
