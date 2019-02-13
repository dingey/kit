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
	 * 设置xml的请求
	 * 
	 * @param str 请求内容
	 * @return 请求对象
	 */
	Request form(Multipart part);

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

	/**
	 * 新增cookie参数
	 * 
	 * @param k key
	 * @param value value
	 * @return 请求对象
	 */
	Request addCookie(String k, String value);

	/**
	 * 新增head参数
	 * 
	 * @param name name
	 * @param value value
	 * @return 请求对象
	 */
	Request addHead(String name, String value);

	/**
	 * 创建GET请求
	 * 
	 * @param url 路径
	 * @return 请求对象
	 */
	static Request get(String url) {
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
		Map<String, String> heads;
		StringBuilder cookie;

		public GetRequest(String url) {
			this.url = url;
		}

		void init(URLConnection con) {
			if (this.requestType != null && !this.requestType.isEmpty()) {
				conn.setRequestProperty("Content-Type", requestType);
			}
			if (this.accept != null && !this.accept.isEmpty()) {
				conn.setRequestProperty("Accept", accept);
			}
			if (cookie != null) {
				conn.setRequestProperty("Cookie", cookie.toString());
			}
			if (heads != null && !heads.isEmpty()) {
				for (String k : heads.keySet()) {
					conn.setRequestProperty(k, heads.get(k));
				}
			}
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
				init(conn);
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
				throw new RuntimeException(e);
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
				throw new RuntimeException(e);
			}
		}

		@Override
		public String returnContent(String charset) {
			try {
				return new String(readbytes, charset);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
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

		@Override
		public Request addCookie(String k, String value) {
			if (cookie == null) {
				cookie = new StringBuilder();
			}
			cookie.append(k).append("=").append(value).append(";");
			return this;
		}

		@Override
		public Request addHead(String name, String value) {
			if (heads == null) {
				heads = new LinkedHashMap<>();
			}
			heads.put(name, value);
			return this;
		}

		@Override
		public Request form(Multipart part) {
			throw new UnsupportedOperationException("不支持的类型");
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
				init(conn);
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
				throw new RuntimeException(e);
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
				throw new RuntimeException(e);
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

		@Override
		public Request form(Multipart part) {
			this.reqBytes = part.toBytes();
			this.requestType = "multipart/form-data; boundary=" + part.boundary();
			return this;
		}
	}

	/**
	 * 创建POST请求
	 * 
	 * @param url 路径
	 * @return 请求对象
	 */
	static Request post(String url) {
		return new PostRequest(url);
	}

}
