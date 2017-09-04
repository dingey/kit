package com.di.kit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import com.di.kit.ConnectionUtil.ContentTypeEnum;

/**
 * @author di
 */
public class HttpConnection {
	static String DEFAULT_ENCODE = "UTF-8";
	static String BOUNDARY = "--boundary--";

	public static String postForm(String url, Map<Object, Object> params) throws IOException {
		return post(url, params, null, false);
	}

	public static String postMultipartForm(String url, Map<Object, Object> params) throws IOException {
		return post(url, params, null, true);
	}

	public static String postJson(String url, String params) {
		return post(url, params, null, ContentType.JSON);
	}

	public static String postXml(String url, String params) {
		return post(url, params, null, ContentType.XML);
	}

	public static String post(String url, Map<Object, Object> params, boolean multipart) throws IOException {
		return post(url, params, null, multipart);
	}

	public static String post(String url, Map<Object, Object> params, Map<String, Object> httpHeads, boolean multipart)
			throws IOException {
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
				return new String(connect(url, out.toByteArray(), httpHeads, false));
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
				return post(url, s.toString(), httpHeads, DEFAULT_ENCODE, null);
			}
		}
		return post(url, null, httpHeads, DEFAULT_ENCODE, null);
	}

	public static String post(String url, String params, Map<String, Object> httpHeads, ContentType contentType) {
		return post(url, params, httpHeads, DEFAULT_ENCODE, contentType);
	}

	public static String post(String url, String params, Map<String, Object> httpHeads, String encode,
			ContentType contentType) {
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
			return URLDecoder.decode(new String(connect(url, params.getBytes(encode), httpHeads, false), encode),
					encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(String url) {
		return get(url, DEFAULT_ENCODE);
	}

	public static String get(String url, String encode) {
		try {
			return new String(connect(url, new byte[0], null, true), encode);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] connect(String url, byte[] params, Map<String, Object> httpHeads, boolean get) {
		byte[] bytes = new byte[512];
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			URL realURL = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realURL.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(3000);
			conn.setDoInput(true);
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
