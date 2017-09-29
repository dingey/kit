package com.di.kit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import com.di.kit.ConnectionUtil.ContentTypeEnum;

/**
 * @author di
 */
public class HttpConnection {
	static String DEFAULT_ENCODE = "UTF-8";
	static String BOUNDARY = "--boundary666--";

	public static String postForm(String url, Map<Object, Object> params) throws IOException {
		return post(url, params, null, false, url.startsWith("https") || url.startsWith("HTTPS"));
	}

	public static String postMultipartForm(String url, Map<Object, Object> params) throws IOException {
		return post(url, params, null, true, url.startsWith("https") || url.startsWith("HTTPS"));
	}

	public static String postJson(String url, String json) {
		return post(url, json, null, ContentType.JSON, url.startsWith("https") || url.startsWith("HTTPS"));
	}

	public static String postXml(String url, String xml) {
		return post(url, xml, null, ContentType.XML, url.startsWith("https") || url.startsWith("HTTPS"));
	}

	public static String post(String url, Map<Object, Object> params, boolean multipart, boolean https)
			throws IOException {
		return post(url, params, null, multipart, https);
	}

	public static String post(String url, Map<Object, Object> params, Map<String, Object> httpHeads, boolean multipart,
			boolean https) throws IOException {
		if (params != null) {
			if (httpHeads == null) {
				httpHeads = new HashMap<>();
				httpHeads.put("Connection", "Keep-Alive");
				httpHeads.put("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
			}
			if (multipart) {
				httpHeads.put("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				for (Object key : params.keySet()) {
					Object v = params.get(key);
					if (v == null) {
						continue;
					}
					out.write(("\r\n--" + BOUNDARY + "\r\n").getBytes());
					if (v.getClass() == java.io.File.class) {
						try {
							File file = (File) v;
							String fileName = file.getName();
							String contentType = ContentTypeEnum.getMimeByFileExt(fileName);
							out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName
									+ "\"\r\n").getBytes());
							out.write(("Content-Type:" + contentType + "\r\n\r\n").getBytes());
							DataInputStream in = new DataInputStream(new FileInputStream(file));
							byte[] bufferOut = new byte[1024];
							while (in.read(bufferOut) != -1) {
								out.write(bufferOut);
							}
							in.close();
						} catch (IOException e) {
						}
					} else {
						out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + String.valueOf(v))
								.getBytes());
					}
				}
				httpHeads.put("Content-Length", out.size());
				if (https) {
					return new String(connects(url, out.toByteArray(), httpHeads, false));
				} else {
					return new String(connect(url, out.toByteArray(), httpHeads));
				}
			} else {
				StringBuilder s = new StringBuilder();
				httpHeads.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
				for (Object key : params.keySet()) {
					Object v = params.get(key);
					if (v == null) {
						continue;
					}
					try {
						v = URLEncoder.encode(String.valueOf(v), DEFAULT_ENCODE);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					s.append(String.valueOf(key)).append("=").append(v).append("&");
				}
				if (s.length() > 0 && s.charAt(s.length() - 1) == '&') {
					s.deleteCharAt(s.length() - 1);
				}
				httpHeads.put("Content-Length", s.toString().getBytes().length);
				return post(url, s.toString(), httpHeads, DEFAULT_ENCODE, null, https);
			}
		}
		return post(url, null, httpHeads, DEFAULT_ENCODE, null, https);
	}

	public static String post(String url, String params, Map<String, Object> httpHeads, ContentType contentType,
			boolean https) {
		return post(url, params, httpHeads, DEFAULT_ENCODE, contentType, https);
	}

	public static String post(String url, String params, Map<String, Object> httpHeads, String encode,
			ContentType contentType, boolean https) {
		try {
			if (httpHeads == null) {
				httpHeads = new HashMap<>();
			}
			if (encode == null) {
				encode = DEFAULT_ENCODE;
			}
			if (contentType != null) {
				httpHeads.put("Content-Type", contentType.getValue());
			}
			if (params == null) {
				params = "";
			}
			if (https) {
				return URLDecoder.decode(new String(connects(url, params.getBytes(encode), httpHeads, false), encode),
						encode);
			} else {
				return URLDecoder.decode(new String(connect(url, params.getBytes(encode), httpHeads), encode), encode);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(String url) {
		return get(url, DEFAULT_ENCODE, url.startsWith("https") || url.startsWith("HTTPS"));
	}

	public static String get(String url, String encode, boolean https) {
		try {
			if (https) {
				return new String(connects(url, new byte[0], null, true), encode);
			} else {
				return new String(connect(url, new byte[0], null), encode);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] connect(String url, byte[] params, Map<String, Object> httpHeads) {
		byte[] bytes = new byte[512];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL realURL = new URL(url);
			HttpURLConnection conn = null;
			if (realURL.getProtocol().equals("https")) {
				conn = (HttpsURLConnection) realURL.openConnection();
			} else {
				conn = (HttpURLConnection) realURL.openConnection();
			}
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
			if (httpHeads == null) {
				conn.setRequestProperty("accept", "*/*");
				// conn.setRequestProperty("connection", "Keep-Alive");
				// conn.setRequestProperty("user-agent",
				// "Mozilla/4.0(compatible;MSIE)");
			} else {
				for (String key : httpHeads.keySet()) {
					conn.setRequestProperty(key, String.valueOf(httpHeads.get(key)));
				}
			}
			if (params != null && params.length > 0) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				OutputStream outputStream = conn.getOutputStream();
				outputStream.write(params);
				outputStream.close();
			} else {
				conn.setRequestMethod("GET");
			}
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				while (conn.getInputStream().read(bytes) > 0) {
					out.write(bytes);
				}
			} else {
				while (conn.getErrorStream().read(bytes) > 0) {
					out.write(bytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static byte[] connects(String url, byte[] params, Map<String, Object> httpHeads, boolean get) {
		byte[] bytes = new byte[512];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL realURL = new URL(url);
			HttpsURLConnection conn = (HttpsURLConnection) realURL.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
			conn.setSSLSocketFactory(My509TrustManager.getSSFactory());
			if (httpHeads == null) {
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent", "Mozilla/4.0(compatible;MSIE)");
			} else {
				for (String key : httpHeads.keySet()) {
					conn.setRequestProperty(key, String.valueOf(httpHeads.get(key)));
				}
			}
			if (!get) {
				conn.setRequestMethod("POST");
				conn.setDoOutput(true);
				conn.getOutputStream().write(params);
			}
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				while (conn.getInputStream().read(bytes) > 0) {
					out.write(bytes);
				}
			} else {
				while (conn.getErrorStream().read(bytes) > 0) {
					out.write(bytes);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out.toByteArray();
	}

	public static class My509TrustManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		@Override
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public static SSLSocketFactory getSSFactory()
				throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
			TrustManager[] tm = { new My509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			return ssf;
		}
	}

	public enum ContentType {
		APPLICATION("application/x-www-form-urlencoded;charset=utf-8"), MULTIPART("multipart/form-data"), JSON(
				"application/json;charset=utf-8"), XML("text/xml;charset=utf-8");

		private ContentType(String value) {
			this.value = value;
		}

		private String value;

		public String getValue() {
			return value;
		}

		public static String getContentType(ContentType contentType) {
			if (contentType.equals(XML)) {
				return "text/xml;charset=utf-8";
			} else if (contentType.equals(JSON)) {
				return "application/json;charset=utf-8";
			} else {
				return "application/x-www-form-urlencoded;charset=utf-8";
			}
		}
	}
}
