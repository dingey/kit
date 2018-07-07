package com.di.kit;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public interface Request {
    Request execute();

    String getContentType();

    String returnContent();

    String returnContent(String charset);

    Request body(Map<Object, Object> form);

    Request body(Form form);

    Request body(String str);

    Request json(String str);

    Request xml(String str);

    Request requestType(String str);

    Request accept(String str);

    Request add(String k, String v);

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

        private GetRequest(String url) {
            this.url = url;
        }

        @Override
        public Request execute() {
            try {
                URL u = new URL(url());
                conn = u.openConnection();
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
        public Request json(String str) {
            this.requestType = "application/json";
            this.reqStr = str;
            return this;
        }

        @Override
        public Request xml(String str) {
            this.requestType = "text/xml";
            this.reqStr = str;
            return this;
        }

        @Override
        public Request requestType(String requestType) {
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
    }

    class PostRequest implements Request {
        String url;
        URLConnection conn;
        Map<Object, Object> form;
        byte[] readbytes;
        String reqStr;
        String contentType;
        String requestType;
        String accept;

        private PostRequest(String url) {
            this.url = url;
        }

        @Override
        public Request execute() {
            try {
                URL u = new URL(url);
                conn = u.openConnection();
                if (this.requestType != null && !this.requestType.isEmpty()) {
                    conn.setRequestProperty("Content-Type", requestType);
                }
                if (this.accept != null && !this.accept.isEmpty()) {
                    conn.setRequestProperty("Accept", accept);
                }
                conn.setDoOutput(true);
                conn.setDoInput(true);
                PrintWriter writer = new PrintWriter(conn.getOutputStream());
                writer.print(requestBody());
                writer.flush();
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

        @Override
        public Request body(Map<Object, Object> form) {
            this.form = form;
            return this;
        }

        @Override
        public Request body(Form form) {
            this.form = form.build();
            return this;
        }

        @Override
        public Request body(String str) {
            this.reqStr = str;
            return this;
        }

        @Override
        public Request json(String str) {
            this.requestType = "application/json";
            this.reqStr = str;
            return this;
        }

        @Override
        public Request xml(String str) {
            this.requestType = "text/xml";
            this.reqStr = str;
            return this;
        }

        @Override
        public Request requestType(String requestType) {
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
            if (this.form == null) {
                this.form = new LinkedHashMap<>();
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
