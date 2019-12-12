 package org.json;
 
 import java.util.Iterator;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class XML
 {
   public static final Character AMP = Character.valueOf('&');
   
 
   public static final Character APOS = Character.valueOf('\'');
   
 
   public static final Character BANG = Character.valueOf('!');
   
 
   public static final Character EQ = Character.valueOf('=');
   
 
   public static final Character GT = Character.valueOf('>');
   
 
   public static final Character LT = Character.valueOf('<');
   
 
   public static final Character QUEST = Character.valueOf('?');
   
 
   public static final Character QUOT = Character.valueOf('"');
   
 
   public static final Character SLASH = Character.valueOf('/');
   
 
 
 
 
 
 
 
 
 
 
   private static Iterable<Integer> codePointIterator(String string)
   {
     new Iterable()
     {
       public Iterator<Integer> iterator() {
         new Iterator() {
           private int nextIndex = 0;
           private int length = XML.this.length();
           
           public boolean hasNext()
           {
             return this.nextIndex < this.length;
           }
           
           public Integer next()
           {
             int result = XML.this.codePointAt(this.nextIndex);
             this.nextIndex += Character.charCount(result);
             return Integer.valueOf(result);
           }
           
           public void remove()
           {
             throw new UnsupportedOperationException();
           }
         };
       }
     };
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static String escape(String string)
   {
     StringBuilder sb = new StringBuilder(string.length());
     for (Iterator localIterator = codePointIterator(string).iterator(); localIterator.hasNext();) { int cp = ((Integer)localIterator.next()).intValue();
       switch (cp) {
       case 38: 
         sb.append("&amp;");
         break;
       case 60: 
         sb.append("&lt;");
         break;
       case 62: 
         sb.append("&gt;");
         break;
       case 34: 
         sb.append("&quot;");
         break;
       case 39: 
         sb.append("&apos;");
         break;
       default: 
         if (mustEscape(cp)) {
           sb.append("&#x");
           sb.append(Integer.toHexString(cp));
           sb.append(';');
         } else {
           sb.appendCodePoint(cp);
         }
         break; }
     }
     return sb.toString();
   }
   
 
 
 
 
 
 
 
 
 
 
 
   private static boolean mustEscape(int cp)
   {
     return ((Character.isISOControl(cp)) && (cp != 9) && (cp != 10) && (cp != 13)) || (((cp < 32) || (cp > 55295)) && ((cp < 57344) || (cp > 65533)) && ((cp < 65536) || (cp > 1114111)));
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static String unescape(String string)
   {
     StringBuilder sb = new StringBuilder(string.length());
     int i = 0; for (int length = string.length(); i < length; i++) {
       char c = string.charAt(i);
       if (c == '&') {
         int semic = string.indexOf(';', i);
         if (semic > i) {
           String entity = string.substring(i + 1, semic);
           sb.append(XMLTokener.unescapeEntity(entity));
           
           i += entity.length() + 1;
         }
         else
         {
           sb.append(c);
         }
       }
       else {
         sb.append(c);
       }
     }
     return sb.toString();
   }
   
 
 
 
 
 
 
   public static void noSpace(String string)
     throws JSONException
   {
     int length = string.length();
     if (length == 0) {
       throw new JSONException("Empty string.");
     }
     for (int i = 0; i < length; i++) {
       if (Character.isWhitespace(string.charAt(i))) {
         throw new JSONException("'" + string + "' contains a space character.");
       }
     }
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   private static boolean parse(XMLTokener x, JSONObject context, String name, boolean keepStrings)
     throws JSONException
   {
     JSONObject jsonobject = null;
     
 
 
 
 
 
 
 
 
 
 
 
 
 
     Object token = x.nextToken();
     
 
 
     if (token == BANG) {
       char c = x.next();
       if (c == '-') {
         if (x.next() == '-') {
           x.skipPast("-->");
           return false;
         }
         x.back();
       } else if (c == '[') {
         token = x.nextToken();
         if (("CDATA".equals(token)) && 
           (x.next() == '[')) {
           String string = x.nextCDATA();
           if (string.length() > 0) {
             context.accumulate("content", string);
           }
           return false;
         }
         
         throw x.syntaxError("Expected 'CDATA['");
       }
       int i = 1;
       do {
         token = x.nextMeta();
         if (token == null)
           throw x.syntaxError("Missing '>' after '<!'.");
         if (token == LT) {
           i++;
         } else if (token == GT) {
           i--;
         }
       } while (i > 0);
       return false; }
     if (token == QUEST)
     {
 
       x.skipPast("?>");
       return false; }
     if (token == SLASH)
     {
 
 
       token = x.nextToken();
       if (name == null) {
         throw x.syntaxError("Mismatched close tag " + token);
       }
       if (!token.equals(name)) {
         throw x.syntaxError("Mismatched " + name + " and " + token);
       }
       if (x.nextToken() != GT) {
         throw x.syntaxError("Misshaped close tag");
       }
       return true;
     }
     if ((token instanceof Character)) {
       throw x.syntaxError("Misshaped tag");
     }
     
 
 
     String tagName = (String)token;
     token = null;
     jsonobject = new JSONObject();
     for (;;) {
       if (token == null) {
         token = x.nextToken();
       }
       
       if (!(token instanceof String)) break;
       String string = (String)token;
       token = x.nextToken();
       if (token == EQ) {
         token = x.nextToken();
         if (!(token instanceof String)) {
           throw x.syntaxError("Missing value");
         }
         jsonobject.accumulate(string, 
           keepStrings ? (String)token : stringToValue((String)token));
         token = null;
       } else {
         jsonobject.accumulate(string, "");
       }
     }
     
     if (token == SLASH)
     {
       if (x.nextToken() != GT) {
         throw x.syntaxError("Misshaped tag");
       }
       if (jsonobject.length() > 0) {
         context.accumulate(tagName, jsonobject);
       } else {
         context.accumulate(tagName, "");
       }
       return false;
     }
     if (token == GT) {
       do {
         for (;;) {
           token = x.nextContent();
           if (token == null) {
             if (tagName != null) {
               throw x.syntaxError("Unclosed tag " + tagName);
             }
             return false; }
           if (!(token instanceof String)) break;
           String string = (String)token;
           if (string.length() > 0) {
             jsonobject.accumulate("content", 
               keepStrings ? string : stringToValue(string));
           }
         }
       } while ((token != LT) || 
       
         (!parse(x, jsonobject, tagName, keepStrings)));
       if (jsonobject.length() == 0) {
         context.accumulate(tagName, "");
       } else if ((jsonobject.length() == 1) && 
         (jsonobject.opt("content") != null)) {
         context.accumulate(tagName, jsonobject
           .opt("content"));
       } else {
         context.accumulate(tagName, jsonobject);
       }
       return false;
     }
     
 
 
     throw x.syntaxError("Misshaped tag");
   }
   
 
 
 
 
 
 
 
 
 
 
   public static Object stringToValue(String string)
   {
     if (string.equals("")) {
       return string;
     }
     if (string.equalsIgnoreCase("true")) {
       return Boolean.TRUE;
     }
     if (string.equalsIgnoreCase("false")) {
       return Boolean.FALSE;
     }
     if (string.equalsIgnoreCase("null")) {
       return JSONObject.NULL;
     }
     
 
 
 
 
 
     char initial = string.charAt(0);
     if (((initial >= '0') && (initial <= '9')) || (initial == '-'))
     {
       try
       {
         if ((string.indexOf('.') > -1) || (string.indexOf('e') > -1) || 
           (string.indexOf('E') > -1) || ("-0".equals(string))) {
           Double d = Double.valueOf(string);
           if ((!d.isInfinite()) && (!d.isNaN())) {
             return d;
           }
         } else {
           Long myLong = Long.valueOf(string);
           if (string.equals(myLong.toString())) {
             if (myLong.longValue() == myLong.intValue()) {
               return Integer.valueOf(myLong.intValue());
             }
             return myLong;
           }
         }
       }
       catch (Exception localException) {}
     }
     return string;
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static JSONObject toJSONObject(String string)
     throws JSONException
   {
     return toJSONObject(string, false);
   }
   
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   public static JSONObject toJSONObject(String string, boolean keepStrings)
     throws JSONException
   {
     JSONObject jo = new JSONObject();
     XMLTokener x = new XMLTokener(string);
     while (x.more()) {
       x.skipPast("<");
       if (x.more()) {
         parse(x, jo, null, keepStrings);
       }
     }
     return jo;
   }
   
 
 
 
 
 
   public static String toString(Object object)
     throws JSONException
   {
     return toString(object, null);
   }
   
 
 
 
 
 
 
 
 
 
   public static String toString(Object object, String tagName)
     throws JSONException
   {
     StringBuilder sb = new StringBuilder();
     
 
 
 
     if ((object instanceof JSONObject))
     {
 
       if (tagName != null) {
         sb.append('<');
         sb.append(tagName);
         sb.append('>');
       }
       
 
 
       JSONObject jo = (JSONObject)object;
       for (String key : jo.keySet()) {
         Object value = jo.opt(key);
         if (value == null) {
           value = "";
         } else if (value.getClass().isArray()) {
           value = new JSONArray(value);
         }
         
 
         if ("content".equals(key)) {
           if ((value instanceof JSONArray)) {
             JSONArray ja = (JSONArray)value;
             int jaLength = ja.length();
             
             for (int i = 0; i < jaLength; i++) {
               if (i > 0) {
                 sb.append('\n');
               }
               Object val = ja.opt(i);
               sb.append(escape(val.toString()));
             }
           } else {
             sb.append(escape(value.toString()));
           }
           
 
         }
         else if ((value instanceof JSONArray)) {
           JSONArray ja = (JSONArray)value;
           int jaLength = ja.length();
           
           for (int i = 0; i < jaLength; i++) {
             Object val = ja.opt(i);
             if ((val instanceof JSONArray)) {
               sb.append('<');
               sb.append(key);
               sb.append('>');
               sb.append(toString(val));
               sb.append("</");
               sb.append(key);
               sb.append('>');
             } else {
               sb.append(toString(val, key));
             }
           }
         } else if ("".equals(value)) {
           sb.append('<');
           sb.append(key);
           sb.append("/>");
 
         }
         else
         {
           sb.append(toString(value, key));
         }
       }
       if (tagName != null)
       {
 
         sb.append("</");
         sb.append(tagName);
         sb.append('>');
       }
       return sb.toString();
     }
     
 
     if ((object != null) && (((object instanceof JSONArray)) || (object.getClass().isArray()))) { JSONArray ja;
       JSONArray ja; if (object.getClass().isArray()) {
         ja = new JSONArray(object);
       } else {
         ja = (JSONArray)object;
       }
       int jaLength = ja.length();
       
       for (int i = 0; i < jaLength; i++) {
         Object val = ja.opt(i);
         
 
 
         sb.append(toString(val, tagName == null ? "array" : tagName));
       }
       return sb.toString();
     }
     
     String string = object == null ? "null" : escape(object.toString());
     return 
     
       "<" + tagName + ">" + string + "</" + tagName + ">";
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/json-20180130.jar!/org/json/XML.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */