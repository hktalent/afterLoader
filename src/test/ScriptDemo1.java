 package test;
 
 import java.io.PrintStream;
 import java.util.List;
 import javax.script.ScriptEngine;
 import javax.script.ScriptEngineFactory;
 import javax.script.ScriptEngineManager;
 
 
 
 public class ScriptDemo1
 {
   public static void main(String[] args)
   {
     ScriptEngineManager manager = new ScriptEngineManager();
     
 
 
     List<ScriptEngineFactory> factories = manager.getEngineFactories();
     
 
 
 
     for (ScriptEngineFactory factory : factories)
     {
       System.out.println("Full name = " + factory.getEngineName());
       
       System.out.println("Version = " + factory.getEngineVersion());
       
       System.out.println("Extensions");
       List<String> extensions = factory.getExtensions();
       for (String extension : extensions) {
         System.out.println("   " + extension);
       }
       System.out.println("Language name = " + 
         factory.getLanguageName());
       
       System.out.println("Language version = " + 
         factory.getLanguageVersion());
       
       System.out.println("MIME Types");
       List<String> mimetypes = factory.getMimeTypes();
       for (String mimetype : mimetypes) {
         System.out.println("   " + mimetype);
       }
       System.out.println("Short Names");
       Object shortnames = factory.getNames();
       for (String shortname : (List)shortnames) {
         System.out.println("   " + shortname);
       }
       String[] params = 
         {
         "javax.script.engine", 
         "javax.script.engine_version", 
         "javax.script.language", 
         "javax.script.language_version", 
         "javax.script.name", 
         "THREADING" };
       String[] arrayOfString1;
       int j = (arrayOfString1 = params).length; for (int i = 0; i < j; i++) { String param = arrayOfString1[i];
         
         System.out.printf("Parameter %s = %s", new Object[] { param, 
           factory.getParameter(param) });
         System.out.println();
       }
       
       ScriptEngine engine = factory.getScriptEngine();
       System.out.println(engine);
       
       System.out.println();
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/ScriptDemo1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */