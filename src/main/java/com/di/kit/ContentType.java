package com.di.kit;

public enum ContentType {

	APPLICATION("application/x-www-form-urlencoded"), //
	AVI("video/avi", "avi"), //
	BMP("application/x-bmp", "bmp"), //
	CSS("text/css", "css"), //
	DOC("application/msword", "doc"), //
	EXE("application/x-msdownload", "exe"), //
	GIF("image/gif", "gif"), //
	HTML("text/html", "html", "htm", "xhtml", "jsp"), //

	ICO("image/x-icon", "ico"), //
	IMG("application/x-img", "img"), //
	
	JAVA("java/*", "java"), //
	JPEG("image/jpeg", "jfif", "jpe", "jpeg", "jpg"), //
	JPG("application/x-jpg", "jpg"), //
	JS("application/x-javascript", "js"), //
	JSON("application/json", "json"), //
	JSP("text/html", "jsp"), //

	MULTIPART("multipart/form-data"), //
	MP3("audio/mp3", "mp3"), //
	MP4("video/mpeg4", "mp4", "m4e"), //
	MPEG("video/mpg", "mpeg"), //

	PNG("application/x-png", "png"), //
	PPT("application/x-ppt", "ppt"), //
	
	RM("application/vnd.rn-realmedia", "rm"), //

	STREAM("application/octet-stream"), //
	SWF("application/x-shockwave-flash", "swf"), //

	TIF("image/tiff", "tif"), //
	TXT("text/plain", "txt"), //

	WAV("audio/wav", "wav"), //
	WMA("audio/x-ms-wma", "wma"), //
	WMV("video/x-ms-wmv", "wmv"), //

	XML("text/xml", "xml", "xsl", "xsd", "xslt", "svg", "math", "biz", "dtd", "vxml", "wsdl"), //
	XHTML("text/html", "xhtml"), //
	XLS("application/x-xls", "xls"), //
	;

	private ContentType(String value) {
		this.value = value;
	}

	private ContentType(String value, String... ext) {
		this.value = value;
		this.ext = ext;
	}

	private String value;
	private String[] ext;

	public String getValue() {
		return value;
	}

	public String[] getExt() {
		return ext;
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

	public static String parseExt(String filename) {
		for (ContentType ct : values()) {
			if (ct.getExt() != null && ct.getExt().length > 0)
				for (String s : ct.getExt()) {
					if (filename.endsWith(s))
						return ct.getValue();
				}
		}
		return STREAM.getValue();
	}
}
