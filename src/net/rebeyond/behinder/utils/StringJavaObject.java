 package net.rebeyond.behinder.utils;
 
 import java.io.IOException;
 import java.net.URI;
 import javax.tools.JavaFileObject.Kind;
 import javax.tools.SimpleJavaFileObject;
 
 public class StringJavaObject
   extends SimpleJavaFileObject
 {
   private String content = "";
   
   public StringJavaObject(String _javaFileName, String _content) {
     super(_createStringJavaObjectUri(_javaFileName), JavaFileObject.Kind.SOURCE);
     this.content = _content;
   }
   
   private static URI _createStringJavaObjectUri(String name)
   {
     return URI.create("String:///" + name + JavaFileObject.Kind.SOURCE.extension);
   }
   
   public CharSequence getCharContent(boolean ignoreEncodingErrors)
     throws IOException
   {
     return this.content;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/StringJavaObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */