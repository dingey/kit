package com.di.kit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;

/**
 * @author di
 */
public class FileUtil {
    public static void writeString(String path, String content) {
	writeString(path, content, false);
    }

    public static void writeString(String path, String content, boolean append) {
	try {
	    File f = new File(path);
	    if (!f.exists()) {
		f.createNewFile();
	    }
	    FileWriter fw = new FileWriter(f, append);
	    fw.write(content);
	    fw.close();
	} catch (IOException e) {
	}
    }

    public static void writeFile(File f, String nameAndPath) {
	InputStream in = null;
	OutputStream out = null;
	try {
	    in = new FileInputStream(f);
	    out = new FileOutputStream(nameAndPath);
	    byte[] bytes = new byte[1024];
	    while (in.read(bytes) != -1) {
		out.write(bytes);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (in != null)
		    in.close();
		if (out != null)
		    out.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public static void writeBytes(byte[] bytes, String nameAndPath) {
	OutputStream out = null;
	try {
	    out = new FileOutputStream(nameAndPath);
	    out.write(bytes);
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (out != null)
		    out.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public static String readString(String path, String format) {
	String str = "";
	try {
	    FileInputStream in = new FileInputStream(path);
	    int size = in.available();
	    byte[] buffer = new byte[size];
	    in.read(buffer);
	    in.close();
	    str = new String(buffer, format == null ? "utf-8" : format);
	} catch (IOException e) {
	    e.printStackTrace();
	}
	return str;
    }

    public static String readStringRelative(String relativePath, String format) {
	return readString(getRealPath(relativePath), format);
    }

    public static byte[] readBytesRelative(String relativePath, String format) {
	return readBytes(getRealPath(relativePath));
    }

    public static byte[] readBytes(String path) {
	FileInputStream in = null;
	try {
	    in = new FileInputStream(path);
	    byte[] buffer = new byte[1024];
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    int n = 0;
	    while ((n = in.read(buffer)) != -1) {
		out.write(buffer, 0, n);
	    }
	    return out.toByteArray();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (in != null)
		    in.close();
	    } catch (IOException e) {
	    }
	}
	return null;
    }

    private static String getRealPath(String relativePath) {
	String path = relativePath;
	File f = new File(path);
	if (!f.exists()) {
	    try {
		path = Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	    path = path + relativePath;
	    f = new File(path);
	    if (!f.exists()) {
		if (path.indexOf("test-classes") != -1) {
		    path = path.replaceFirst("test-classes", "classes");
		}
		f = new File(path);
		if (!f.exists()) {
		    System.err.println(relativePath + " not found");
		}
	    }
	}
	return path;
    }
}
