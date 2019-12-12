 package test;
 
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.util.List;
 import java.util.Map;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import javax.tools.DiagnosticCollector;
 import javax.tools.FileObject;
 import javax.tools.ForwardingJavaFileManager;
 import javax.tools.JavaCompiler;
 import javax.tools.JavaFileManager;
 import javax.tools.JavaFileManager.Location;
 import javax.tools.JavaFileObject;
 import javax.tools.JavaFileObject.Kind;
 import javax.tools.SimpleJavaFileObject;
 
 public class App
 {
   private static Map<String, JavaFileObject> fileObjects = new java.util.concurrent.ConcurrentHashMap();
   
   public static void main(String[] args) throws IOException { String code = "public class Man {\n\tpublic void hello(){\n\t\tSystem.out.println(\"hello world\");\n\t}\n}";
     
 
 
 
 
     JavaCompiler compiler = javax.tools.ToolProvider.getSystemJavaCompiler();
     DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector();
     JavaFileManager javaFileManager = new MyJavaFileManager(compiler.getStandardFileManager(collector, null, null));
     
     List<String> options = new java.util.ArrayList();
     options.add("-target");
     options.add("1.8");
     
     Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
     Matcher matcher = CLASS_PATTERN.matcher(code);
     String cls;
     if (matcher.find()) {
       cls = matcher.group(1);
     } else {
       throw new IllegalArgumentException("No such class name in " + code);
     }
     String cls;
     JavaFileObject javaFileObject = new MyJavaFileObject(cls, code);
     Boolean result = compiler.getTask(null, javaFileManager, collector, options, null, java.util.Arrays.asList(new JavaFileObject[] { javaFileObject })).call();
     
     JavaFileObject fileObject = (JavaFileObject)fileObjects.get(cls);
     if (fileObject != null) {
       byte[] bytes = ((MyJavaFileObject)fileObject).getCompiledBytes();
       System.out.println(bytes.length);
     }
   }
   
   public static class MyJavaFileObject extends SimpleJavaFileObject
   {
     private String source;
     private ByteArrayOutputStream outPutStream;
     
     public MyJavaFileObject(String name, String source)
     {
       super(JavaFileObject.Kind.SOURCE);
       this.source = source;
     }
     
     public MyJavaFileObject(String name, JavaFileObject.Kind kind) {
       super(kind);
       this.source = null;
     }
     
     public CharSequence getCharContent(boolean ignoreEncodingErrors)
     {
       if (this.source == null) {
         throw new IllegalArgumentException("source == null");
       }
       return this.source;
     }
     
     public java.io.OutputStream openOutputStream() throws IOException
     {
       this.outPutStream = new ByteArrayOutputStream();
       return this.outPutStream;
     }
     
     public byte[] getCompiledBytes() {
       return this.outPutStream.toByteArray();
     }
   }
   
   public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {
     protected MyJavaFileManager(JavaFileManager fileManager) {
       super();
     }
     
     public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind) throws IOException
     {
       JavaFileObject javaFileObject = (JavaFileObject)App.fileObjects.get(className);
       if (javaFileObject == null) {
         super.getJavaFileForInput(location, className, kind);
       }
       return javaFileObject;
     }
     
     public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling) throws IOException
     {
       JavaFileObject javaFileObject = new App.MyJavaFileObject(qualifiedClassName, kind);
       App.fileObjects.put(qualifiedClassName, javaFileObject);
       return javaFileObject;
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/test/App.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */