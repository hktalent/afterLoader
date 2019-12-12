 package net.rebeyond.behinder.utils.jc;
 
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.io.Writer;
 import java.net.URI;
 import java.net.URL;
 import javax.lang.model.element.NestingKind;
 import javax.tools.JavaFileObject;
 import javax.tools.JavaFileObject.Kind;
 
 public class CustomJavaFileObject implements JavaFileObject
 {
   private final String binaryName;
   private final URI uri;
   private final String name;
   
   public CustomJavaFileObject(String binaryName, URI uri)
   {
     this.uri = uri;
     this.binaryName = binaryName;
     this.name = (uri.getPath() == null ? uri.getSchemeSpecificPart() : uri.getPath());
   }
   
   public URI toUri()
   {
     return this.uri;
   }
   
   public InputStream openInputStream() throws IOException
   {
     return this.uri.toURL().openStream();
   }
   
   public OutputStream openOutputStream() throws IOException
   {
     throw new UnsupportedOperationException();
   }
   
   public String getName()
   {
     return this.name;
   }
   
   public java.io.Reader openReader(boolean ignoreEncodingErrors) throws IOException
   {
     throw new UnsupportedOperationException();
   }
   
   public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException
   {
     throw new UnsupportedOperationException();
   }
   
   public Writer openWriter() throws IOException
   {
     throw new UnsupportedOperationException();
   }
   
   public long getLastModified()
   {
     return 0L;
   }
   
   public boolean delete()
   {
     throw new UnsupportedOperationException();
   }
   
   public JavaFileObject.Kind getKind()
   {
     return JavaFileObject.Kind.CLASS;
   }
   
   public boolean isNameCompatible(String simpleName, JavaFileObject.Kind kind)
   {
     String baseName = simpleName + kind.extension;
     return (kind.equals(getKind())) && (
       (baseName.equals(getName())) || 
       (getName().endsWith("/" + baseName)));
   }
   
   public NestingKind getNestingKind()
   {
     throw new UnsupportedOperationException();
   }
   
   public javax.lang.model.element.Modifier getAccessLevel()
   {
     throw new UnsupportedOperationException();
   }
   
   public String binaryName() {
     return this.binaryName;
   }
   
 
   public String toString()
   {
     return 
     
       "CustomJavaFileObject{uri=" + this.uri + '}';
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/jc/CustomJavaFileObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */