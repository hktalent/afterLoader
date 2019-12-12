
package test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

public class App {
	private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap();

	public App() {
	}

	public static void main(String[] args) throws IOException {
		String code = "public class Man {\n\tpublic void hello(){\n\t\tSystem.out.println(\"hello world\");\n\t}\n}";
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector();
		JavaFileManager javaFileManager = new App.MyJavaFileManager(
				compiler.getStandardFileManager(collector, (Locale) null, (Charset) null));
		List<String> options = new ArrayList();
		options.add("-target");
		options.add("1.8");
		Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
		Matcher matcher = CLASS_PATTERN.matcher(code);
		if (matcher.find()) {
			String cls = matcher.group(1);
			App.MyJavaFileObject javaFileObject = new App.MyJavaFileObject(cls, code);
			Boolean var10 = compiler.getTask((Writer) null, javaFileManager, collector, options, (Iterable) null,
					Arrays.asList(javaFileObject)).call();
			JavaFileObject fileObject = (JavaFileObject) fileObjects.get(cls);
			if (fileObject != null) {
				byte[] bytes = ((App.MyJavaFileObject) fileObject).getCompiledBytes();
				System.out.println(bytes.length);
			}

		} else {
			throw new IllegalArgumentException("No such class name in " + code);
		}
	}

	public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
		protected MyJavaFileManager(JavaFileManager fileManager) {
			super(fileManager);
		}

		public JavaFileObject getJavaFileForInput(Location location, String className, Kind kind) throws IOException {
			JavaFileObject javaFileObject = (JavaFileObject) App.fileObjects.get(className);
			if (javaFileObject == null) {
				super.getJavaFileForInput(location, className, kind);
			}

			return javaFileObject;
		}

		public JavaFileObject getJavaFileForOutput(Location location, String qualifiedClassName, Kind kind,
				FileObject sibling) throws IOException {
			JavaFileObject javaFileObject = new App.MyJavaFileObject(qualifiedClassName, kind);
			App.fileObjects.put(qualifiedClassName, javaFileObject);
			return javaFileObject;
		}
	}

	public static class MyJavaFileObject extends SimpleJavaFileObject {
		private String source;
		private ByteArrayOutputStream outPutStream;

		public MyJavaFileObject(String name, String source) {
			super(URI.create("String:///" + name + Kind.SOURCE.extension), Kind.SOURCE);
			this.source = source;
		}

		public MyJavaFileObject(String name, Kind kind) {
			super(URI.create("String:///" + name + kind.extension), kind);
			this.source = null;
		}

		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			if (this.source == null) {
				throw new IllegalArgumentException("source == null");
			} else {
				return this.source;
			}
		}

		public OutputStream openOutputStream() throws IOException {
			this.outPutStream = new ByteArrayOutputStream();
			return this.outPutStream;
		}

		public byte[] getCompiledBytes() {
			return this.outPutStream.toByteArray();
		}
	}
}
