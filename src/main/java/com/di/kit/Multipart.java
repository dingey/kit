package com.di.kit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public interface Multipart extends Map<String, Object> {
	byte[] toBytes();

	byte[] toBytes(String charsetName) throws IOException;

	Multipart param(String k, Object v);

	Multipart param(String name, String fileName, String contentType, InputStream in);

	String boundary();

	public static Multipart build() {
		return new MultipartImpl();
	}

	public static class ContentDisposition {
		private String fileName;
		private String contentType;
		private byte[] body;

		public String getFileName() {
			return fileName;
		}

		public ContentDisposition setFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public String getContentType() {
			return contentType;
		}

		public ContentDisposition setContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public byte[] getBody() {
			return body;
		}

		public ContentDisposition setBody(byte[] body) {
			this.body = body;
			return this;
		}
	}

	static class MultipartImpl extends HashMap<String, Object> implements Multipart {

		private static final long serialVersionUID = -924320981328986895L;

		private String boundary;

		MultipartImpl() {
			boundary = "----WebKitFormBoundary";
		}

		@Override
		public byte[] toBytes(String charsetName) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			for (String k : this.keySet()) {
				Object v = get(k);
				if (v == null)
					continue;
				out.write(("\r\n--" + boundary + "\r\n").getBytes(charsetName));
				if (v instanceof File) {
					File file = (File) v;
					String fileName = file.getName();
					String contentType = ContentType.parseExt(fileName);
					out.write(("Content-Disposition: form-data; name=\"" + k + "\"; filename=\"" + fileName + "\"\r\n").getBytes(charsetName));
					out.write(("Content-Type:" + contentType + "\r\n\r\n").getBytes(charsetName));
					DataInputStream in = new DataInputStream(new FileInputStream(file));
					byte[] bufferOut = new byte[256];
					while (in.read(bufferOut) != -1) {
						out.write(bufferOut);
					}
					in.close();
				} else if (v instanceof ContentDisposition) {
					ContentDisposition cd = (ContentDisposition) v;
					out.write(("Content-Disposition: form-data; name=\"" + k + "\"; filename=\"" + cd.getFileName() + "\"\r\n").getBytes(charsetName));
					out.write(("Content-Type:" + cd.getContentType() + "\r\n\r\n").getBytes(charsetName));
					out.write(cd.getBody());
				} else {
					out.write(("Content-Disposition: form-data; name=\"" + k + "\"\r\n\r\n" + String.valueOf(v)).getBytes(charsetName));
				}
			}
			out.write(("\r\n--" + boundary + "--\r\n").getBytes(charsetName));
			return out.toByteArray();
		}

		@Override
		public Multipart param(String name, String fileName, String contentType, InputStream in) {
			ContentDisposition cd = new ContentDisposition();
			cd.setContentType(ContentType.parseExt(fileName));
			cd.setFileName(fileName);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bs = new byte[128];
			try {
				while (in.read(bs) != -1) {
					out.write(bs);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			cd.setBody(out.toByteArray());
			put(name, cd);
			return this;
		}

		@Override
		public Multipart param(String k, Object v) {
			if (k != null && !k.isEmpty())
				this.put(k, v);
			return this;
		}

		@Override
		public String boundary() {
			return boundary;
		}

		@Override
		public byte[] toBytes() {
			try {
				return toBytes("utf-8");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static void main(String[] args) {
		Multipart p = Multipart.build();
		p.param("a", 1);
		System.out.println(p);
	}
}
