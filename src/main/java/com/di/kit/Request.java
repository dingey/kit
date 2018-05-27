package com.di.kit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public interface Request {
	Request execute();

	String getContentType();

	String returnContent();

	String returnContent(String charset);

	Request bodyForm(Map<Object, Object> form);

	Request bodyForm(Form form);

	Request add(String k, String v);

	public static Request Get(String url) {
		return new GetRequest(url);
	}

	static class GetRequest implements Request {
		String url;
		URLConnection conn;
		Map<Object, Object> form;
		byte[] readbytes;
		String contentType;

		private GetRequest(String url) {
			this.url = url;
		}

		@Override
		public Request execute() {
			try {
				URL u = new URL(url());
				conn = u.openConnection();
				conn.connect();
				BufferedInputStream in = new BufferedInputStream(
						conn.getInputStream());
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[128];
				int readCount = 0;
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
			if (this.url != null && !this.url.isEmpty() && form != null
					&& !form.isEmpty()) {
				StringBuilder s = new StringBuilder(this.url);
				if (this.url.contains("?")) {
					s.append("&");
				} else {
					s.append("?");
				}
				for (Object k : form.keySet()) {
					s.append(k).append("=").append(form.get(k)).append("&");
				}
				this.url = this.toString();
			}
			return this.url;
		}

		@Override
		public Request bodyForm(Map<Object, Object> form) {
			if (this.form == null) {
				this.form = form;
			} else {
				this.form.putAll(form);
			}
			return this;
		}

		@Override
		public Request bodyForm(Form form) {
			return bodyForm(form.build());
		}

		@Override
		public Request add(String k, String v) {
			this.form.put(k, v);
			return this;
		}
	}

	public static class PostRequest implements Request {
		String url;
		URLConnection conn;
		Map<Object, Object> form;
		byte[] readbytes;
		String reqStr;
		String contentType;
		private PostRequest(String url) {
			this.url = url;
		}

		@Override
		public Request execute() {
			try {
				URL u = new URL(url);
				conn = u.openConnection();
				conn.setDoOutput(true);
				conn.setDoInput(true);
				PrintWriter writer = new PrintWriter(conn.getOutputStream());
				writer.print(requestBody());
				writer.flush();
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				BufferedInputStream in = new BufferedInputStream(
						conn.getInputStream());
				byte[] buf = new byte[128];
				int readCount = 0;
				while ((readCount = in.read(buf, 0, 100)) > 0) {
					out.write(buf, 0, readCount);
				}
				in.close();
				contentType = conn.getContentType();
				readbytes = out.toByteArray();
			} catch (Exception e) {
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

		public Request bodyForm(Map<Object, Object> form) {
			this.form = form;
			return this;
		}

		@Override
		public Request bodyForm(Form form) {
			this.form = form.build();
			return this;
		}

		@Override
		public Request add(String k, String v) {
			if (this.form == null) {
				this.form = new HashMap<>();
			}
			this.form.put(k, v);
			return this;
		}

		private String requestBody() {
			if (this.url != null && !this.url.isEmpty() && form != null
					&& !form.isEmpty()) {
				StringBuilder s = new StringBuilder(
						this.reqStr == null ? "" : this.reqStr);
				if (this.reqStr != null && !this.reqStr.isEmpty()) {
					s.append("&");
				}
				for (Object k : form.keySet()) {
					s.append(k).append("=").append(form.get(k)).append("&");
				}
				this.reqStr = s.toString();
			}
			return reqStr;
		}

	}

	public static interface Form {
		Form add(String name, String value);

		Map<Object, Object> build();

		static class FormData extends HashMap<Object, Object> implements Form {
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

		public static Form form() {
			return new FormData();
		}
	}

	public static Request Post(String url) {
		return new PostRequest(url);
	}

	public static void main(String[] args) {
		Request get = Request.Get("http://localhost:8090/hi").execute();
		System.out.println(get.getContentType());
		System.out.println(get.returnContent());

		Request post = Request.Post("http://localhost:8090/hi")
				.add("name", "alice").execute();
		System.out.println(post.getContentType());
		System.out.println(post.returnContent());
	}

}
