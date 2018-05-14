package com.di.kit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public interface Request {
	Request execute();

	String returnContent();

	Request bodyForm(Map<Object, Object> form);

	Request bodyForm(Form form);

	Request add(String k, String v);

	public static Request Get(String url) {
		return new GetRequest(url);
	}

	static class GetRequest implements Request {
		String url;
		URLConnection conn;
		private String result;
		Map<Object, Object> form;
		String reqStr;

		private GetRequest(String url) {
			this.url = url;
		}

		@Override
		public Request execute() {
			try {
				URL u = new URL(url());
				conn = u.openConnection();
				conn.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
				this.result = result.toString();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public String returnContent() {
			return result;
		}

		private String url() {
			if (this.url != null && !this.url.isEmpty() && form != null && !form.isEmpty()) {
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
		private String result;
		Map<Object, Object> form;
		String reqStr;

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
				PrintWriter out = new PrintWriter(conn.getOutputStream());
				out.print(requestBody());
				out.flush();
				BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder result = new StringBuilder();
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
				this.result = result.toString();
				out.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return this;
		}

		@Override
		public String returnContent() {
			return result;
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
			if (this.url != null && !this.url.isEmpty() && form != null && !form.isEmpty()) {
				StringBuilder s = new StringBuilder(this.reqStr == null ? "" : this.reqStr);
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
		String get = Request.Get("http://localhost:8080/hi").execute().returnContent();
		System.out.println(get);

		String post = Request.Post("http://localhost:8080/hi").add("name", "alice").execute().returnContent();
		System.out.println(post);
	}

}
