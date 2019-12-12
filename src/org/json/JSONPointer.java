 package org.json;
 
 import java.io.UnsupportedEncodingException;
 import java.net.URLDecoder;
 import java.net.URLEncoder;
 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.List;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class JSONPointer
 {
   private static final String ENCODING = "utf-8";
   private final List<String> refTokens;
   
   public static class Builder
   {
     private final List<String> refTokens = new ArrayList();
     
 
 
 
     public JSONPointer build()
     {
       return new JSONPointer(this.refTokens);
     }
     
 
 
 
 
 
 
 
 
 
 
 
     public Builder append(String token)
     {
       if (token == null) {
         throw new NullPointerException("token cannot be null");
       }
       this.refTokens.add(token);
       return this;
     }
     
 
 
 
 
 
 
     public Builder append(int arrayIndex)
     {
       this.refTokens.add(String.valueOf(arrayIndex));
       return this;
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static Builder builder()
   {
     return new Builder();
   }
   
 
 
 
 
 
 
 
 
 
 
   public JSONPointer(String pointer)
   {
     if (pointer == null) {
       throw new NullPointerException("pointer cannot be null");
     }
     if ((pointer.isEmpty()) || (pointer.equals("#"))) {
       this.refTokens = Collections.emptyList();
       return;
     }
     
     if (pointer.startsWith("#/")) {
       String refs = pointer.substring(2);
       try {
         refs = URLDecoder.decode(refs, "utf-8");
       } catch (UnsupportedEncodingException e) {
         throw new RuntimeException(e);
       } } else { String refs;
       if (pointer.startsWith("/")) {
         refs = pointer.substring(1);
       } else
         throw new IllegalArgumentException("a JSON pointer should start with '/' or '#/'"); }
     String refs;
     this.refTokens = new ArrayList();
     for (String token : refs.split("/")) {
       this.refTokens.add(unescape(token));
     }
   }
   
   public JSONPointer(List<String> refTokens) {
     this.refTokens = new ArrayList(refTokens);
   }
   
   private String unescape(String token) {
     return 
     
       token.replace("~1", "/").replace("~0", "~").replace("\\\"", "\"").replace("\\\\", "\\");
   }
   
 
 
 
 
 
 
 
 
   public Object queryFrom(Object document)
     throws JSONPointerException
   {
     if (this.refTokens.isEmpty()) {
       return document;
     }
     Object current = document;
     for (String token : this.refTokens) {
       if ((current instanceof JSONObject)) {
         current = ((JSONObject)current).opt(unescape(token));
       } else if ((current instanceof JSONArray)) {
         current = readByIndexToken(current, token);
       } else {
         throw new JSONPointerException(String.format("value [%s] is not an array or object therefore its key %s cannot be resolved", new Object[] { current, token }));
       }
     }
     
 
     return current;
   }
   
   /* Error */
   private Object readByIndexToken(Object current, String indexToken)
     throws JSONPointerException
   {
     // Byte code:
     //   0: aload_2
     //   1: invokestatic 53	java/lang/Integer:parseInt	(Ljava/lang/String;)I
     //   4: istore_3
     //   5: aload_1
     //   6: checkcast 46	org/json/JSONArray
     //   9: astore 4
     //   11: iload_3
     //   12: aload 4
     //   14: invokevirtual 54	org/json/JSONArray:length	()I
     //   17: if_icmplt +38 -> 55
     //   20: new 48	org/json/JSONPointerException
     //   23: dup
     //   24: ldc 55
     //   26: iconst_2
     //   27: anewarray 50	java/lang/Object
     //   30: dup
     //   31: iconst_0
     //   32: iload_3
     //   33: invokestatic 56	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
     //   36: aastore
     //   37: dup
     //   38: iconst_1
     //   39: aload 4
     //   41: invokevirtual 54	org/json/JSONArray:length	()I
     //   44: invokestatic 56	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
     //   47: aastore
     //   48: invokestatic 51	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
     //   51: invokespecial 52	org/json/JSONPointerException:<init>	(Ljava/lang/String;)V
     //   54: athrow
     //   55: aload 4
     //   57: iload_3
     //   58: invokevirtual 57	org/json/JSONArray:get	(I)Ljava/lang/Object;
     //   61: areturn
     //   62: astore 5
     //   64: new 48	org/json/JSONPointerException
     //   67: dup
     //   68: new 59	java/lang/StringBuilder
     //   71: dup
     //   72: invokespecial 60	java/lang/StringBuilder:<init>	()V
     //   75: ldc 61
     //   77: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
     //   80: iload_3
     //   81: invokevirtual 63	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
     //   84: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
     //   87: aload 5
     //   89: invokespecial 65	org/json/JSONPointerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
     //   92: athrow
     //   93: astore_3
     //   94: new 48	org/json/JSONPointerException
     //   97: dup
     //   98: ldc 67
     //   100: iconst_1
     //   101: anewarray 50	java/lang/Object
     //   104: dup
     //   105: iconst_0
     //   106: aload_2
     //   107: aastore
     //   108: invokestatic 51	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
     //   111: aload_3
     //   112: invokespecial 65	org/json/JSONPointerException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
     //   115: athrow
     // Line number table:
     //   Java source line #214	-> byte code offset #0
     //   Java source line #215	-> byte code offset #5
     //   Java source line #216	-> byte code offset #11
     //   Java source line #217	-> byte code offset #20
     //   Java source line #218	-> byte code offset #41
     //   Java source line #217	-> byte code offset #48
     //   Java source line #221	-> byte code offset #55
     //   Java source line #222	-> byte code offset #62
     //   Java source line #223	-> byte code offset #64
     //   Java source line #225	-> byte code offset #93
     //   Java source line #226	-> byte code offset #94
     // Local variable table:
     //   start	length	slot	name	signature
     //   0	116	0	this	JSONPointer
     //   0	116	1	current	Object
     //   0	116	2	indexToken	String
     //   4	77	3	index	int
     //   93	19	3	e	NumberFormatException
     //   9	47	4	currentArr	JSONArray
     //   62	26	5	e	JSONException
     // Exception table:
     //   from	to	target	type
     //   55	61	62	org/json/JSONException
     //   0	61	93	java/lang/NumberFormatException
     //   62	93	93	java/lang/NumberFormatException
   }
   
   public String toString()
   {
     StringBuilder rval = new StringBuilder("");
     for (String token : this.refTokens) {
       rval.append('/').append(escape(token));
     }
     return rval.toString();
   }
   
 
 
 
 
 
 
 
   private String escape(String token)
   {
     return 
     
 
       token.replace("~", "~0").replace("/", "~1").replace("\\", "\\\\").replace("\"", "\\\"");
   }
   
 
 
   public String toURIFragment()
   {
     try
     {
       StringBuilder rval = new StringBuilder("#");
       for (String token : this.refTokens) {
         rval.append('/').append(URLEncoder.encode(token, "utf-8"));
       }
       return rval.toString();
     } catch (UnsupportedEncodingException e) {
       throw new RuntimeException(e);
     }
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/JSONPointer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */