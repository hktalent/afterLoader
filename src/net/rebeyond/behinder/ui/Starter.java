package net.rebeyond.behinder.ui;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class Starter {
	public static void main(String[] args) {
		System.setProperty("org.eclipse.swt.browser.DefaultType", "ie,webkit");
		addJarToClasspath(getArchFilename("swt"));
		Main main = new Main();
		main.start();
	}

	public static String getArchFilename(String prefix) {
		return prefix + "_" + getOSName() + "_" + getArchName() + ".jar";
	}

	private static String getOSName() {
		String osNameProperty = System.getProperty("os.name");

		if (osNameProperty == null) {
			throw new RuntimeException("os.name property is not set");
		}

		osNameProperty = osNameProperty.toLowerCase();

		if (osNameProperty.contains("win")) {
			return "win";
		}
		if (osNameProperty.contains("mac")) {
			return "osx";
		}
		if ((osNameProperty.contains("linux")) || (osNameProperty.contains("nix"))) {
			return "linux";
		}

		throw new RuntimeException("Unknown OS name: " + osNameProperty);
	}

	private static String getArchName() {
		String osArch = System.getProperty("os.arch");

		if ((osArch != null) && (osArch.contains("64"))) {
			return "64";
		}

		return "32";
	}

	public static void addJarToClasspath(String jarFile) {
		try {
			URL url = Starter.class.getClassLoader().getResource("net/rebeyond/behinder/resource/lib/" + jarFile);
			URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			Class<?> urlClass = URLClassLoader.class;
			Method method = urlClass.getDeclaredMethod("addURL", new Class[] { URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { url });
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
