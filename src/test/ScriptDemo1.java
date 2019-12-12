
package test;

import java.util.Iterator;
import java.util.List;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

public class ScriptDemo1 {
	public ScriptDemo1() {
	}

	public static void main(String[] args) {
		ScriptEngineManager manager = new ScriptEngineManager();
		List<ScriptEngineFactory> factories = manager.getEngineFactories();
		Iterator var4 = factories.iterator();

		while (var4.hasNext()) {
			ScriptEngineFactory factory = (ScriptEngineFactory) var4.next();
			System.out.println("Full name = " + factory.getEngineName());
			System.out.println("Version = " + factory.getEngineVersion());
			System.out.println("Extensions");
			List<String> extensions = factory.getExtensions();
			Iterator var7 = extensions.iterator();

			while (var7.hasNext()) {
				String extension = (String) var7.next();
				System.out.println("   " + extension);
			}

			System.out.println("Language name = " + factory.getLanguageName());
			System.out.println("Language version = " + factory.getLanguageVersion());
			System.out.println("MIME Types");
			List<String> mimetypes = factory.getMimeTypes();
			Iterator var8 = mimetypes.iterator();

			while (var8.hasNext()) {
				String mimetype = (String) var8.next();
				System.out.println("   " + mimetype);
			}

			System.out.println("Short Names");
			List<String> shortnames = factory.getNames();
			Iterator var9 = shortnames.iterator();

			while (var9.hasNext()) {
				String shortname = (String) var9.next();
				System.out.println("   " + shortname);
			}

			String[] params = new String[] { "javax.script.engine", "javax.script.engine_version",
					"javax.script.language", "javax.script.language_version", "javax.script.name", "THREADING" };
			String[] var12 = params;
			int var11 = params.length;

			for (int var10 = 0; var10 < var11; ++var10) {
				String param = var12[var10];
				System.out.printf("Parameter %s = %s", param, factory.getParameter(param));
				System.out.println();
			}

			ScriptEngine engine = factory.getScriptEngine();
			System.out.println(engine);
			System.out.println();
		}

	}
}
