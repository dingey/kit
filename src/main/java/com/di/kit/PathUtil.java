package com.di.kit;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @author di
 */
public class PathUtil {
	public static String getClassPath() {
		try {
			return Thread.currentThread().getContextClassLoader().getResource("").toURI().getPath();
		} catch (URISyntaxException e) {
		}
		return null;
	}

	public static String getMavenProjectPath() {
		String classPath = getClassPath();
		if (null != classPath && classPath.contains("/target/classes/")) {
			classPath = classPath.replaceFirst("/target/classes/", "/");
		}else if(null != classPath && classPath.contains("/target/test-classes/")){
			classPath = classPath.replaceFirst("/target/test-classes/", "/");
		}
		return classPath;
	}

	public static String getMavenSrcPath() {
		return getMavenProjectPath() + "src/";
	}

	public static String getMavenResPath() {
		return getMavenProjectPath() + "resources/";
	}

	public static String getMavenWebappPath() {
		return getMavenProjectPath() + "src/main/webapp/";
	}

	public static String getAntProjectPath() {
		String classPath = getClassPath();
		if (null != classPath && classPath.contains("/build/classes/")) {
			classPath = classPath.replaceFirst("/build/classes/", "/");
		}
		return classPath;
	}

	public static String getProjectPath() {
		String classPath = getClassPath();
		if (classPath.endsWith("/target/classes/")) {
			classPath = classPath.replaceFirst("/target/classes/", "/");
		}else if (classPath.endsWith("/build/test-classes/")) {
			classPath = classPath.replaceFirst("/build/test-classes/", "/");
		} else if (classPath.endsWith("/build/classes/")) {
			classPath = classPath.replaceFirst("/build/classes/", "/");
		} else if (classPath.endsWith("/WebContent/WEB-INF/lib/")) {
			classPath = classPath.replaceFirst("/WebContent/WEB-INF/lib/", "/");
		} else if (classPath.endsWith("/WebRoot/WEB-INF/lib/")) {
			classPath = classPath.replaceFirst("/WebRoot/WEB-INF/lib/", "/");
		}
		return classPath;
	}

	public static boolean isMaven() {
		File f = new File(getMavenProjectPath() + "pom.xml");
		return f.exists();
	}
}
