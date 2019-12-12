 package net.rebeyond.behinder.utils.jc;
 
 import java.io.File;
 import java.io.IOException;
 import java.net.JarURLConnection;
 import java.net.URI;
 import java.net.URL;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Enumeration;
 import java.util.List;
 import java.util.jar.JarEntry;
 import java.util.jar.JarFile;
 import javax.tools.JavaFileObject;
 
 
 
 
 public class PackageInternalsFinder
 {
   private ClassLoader classLoader;
   private static final String CLASS_FILE_EXTENSION = ".class";
   
   public PackageInternalsFinder(ClassLoader classLoader)
   {
     this.classLoader = classLoader;
   }
   
   public List<JavaFileObject> find(String packageName) throws IOException {
     String javaPackageName = packageName.replaceAll("\\.", "/");
     
     List<JavaFileObject> result = new ArrayList();
     
     Enumeration<URL> urlEnumeration = this.classLoader.getResources(javaPackageName);
     
 
     while (urlEnumeration.hasMoreElements()) {
       URL packageFolderURL = (URL)urlEnumeration.nextElement();
       if (packageFolderURL.toString().startsWith("jar"))
       {
         result.addAll(listUnder(packageName, packageFolderURL));
       }
     }
     
 
 
 
 
 
     return result;
   }
   
   private Collection<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
     File directory = new File(packageFolderURL.getFile());
     if (directory.isDirectory()) {
       return processDir(packageName, directory);
     }
     return processJar(packageFolderURL);
   }
   
   private List<JavaFileObject> processJar(URL packageFolderURL)
   {
     List<JavaFileObject> result = new ArrayList();
     try {
       String jarUri = packageFolderURL.toExternalForm().split("!")[0];
       
       JarURLConnection jarConn = (JarURLConnection)packageFolderURL.openConnection();
       String rootEntryName = jarConn.getEntryName();
       int rootEnd = rootEntryName.length() + 1;
       
       Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
       while (entryEnum.hasMoreElements()) {
         JarEntry jarEntry = (JarEntry)entryEnum.nextElement();
         String name = jarEntry.getName();
         if ((name.startsWith(rootEntryName)) && (name.indexOf('/', rootEnd) == -1) && (name.endsWith(".class"))) {
           URI uri = URI.create(jarUri + "!/" + name);
           String binaryName = name.replaceAll("/", ".");
           binaryName = binaryName.replaceAll(".class$", "");
           result.add(new CustomJavaFileObject(binaryName, uri));
         }
       }
       jarConn.setDefaultUseCaches(false);
     } catch (Exception e) {
       throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
     }
     return result;
   }
   
   private List<JavaFileObject> processRsrc(URL packageFolderURL) { List<JavaFileObject> result = new ArrayList();
     try {
       String jarUri = packageFolderURL.toExternalForm().split("!")[0];
       
       JarURLConnection jarConn = (JarURLConnection)packageFolderURL.openConnection();
       String rootEntryName = jarConn.getEntryName();
       int rootEnd = rootEntryName.length() + 1;
       
       Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
       while (entryEnum.hasMoreElements()) {
         JarEntry jarEntry = (JarEntry)entryEnum.nextElement();
         String name = jarEntry.getName();
         if ((name.startsWith(rootEntryName)) && (name.indexOf('/', rootEnd) == -1) && (name.endsWith(".class"))) {
           URI uri = URI.create(jarUri + "!/" + name);
           String binaryName = name.replaceAll("/", ".");
           binaryName = binaryName.replaceAll(".class$", "");
           result.add(new CustomJavaFileObject(binaryName, uri));
         }
       }
     } catch (Exception e) {
       throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
     }
     return result;
   }
   
   private List<JavaFileObject> processDir(String packageName, File directory) {
     List<JavaFileObject> result = new ArrayList();
     
     File[] childFiles = directory.listFiles();
     File[] arrayOfFile1; int j = (arrayOfFile1 = childFiles).length; for (int i = 0; i < j; i++) { File childFile = arrayOfFile1[i];
       if (childFile.isFile())
       {
         if (childFile.getName().endsWith(".class")) {
           String binaryName = packageName + "." + childFile.getName();
           binaryName = binaryName.replaceAll(".class$", "");
           
           result.add(new CustomJavaFileObject(binaryName, childFile.toURI()));
         }
       }
     }
     
     return result;
   }
 }


/* Location:              /Users/0x101/safe/mytools_10012106/afterLoader/Behinder.jar!/net/rebeyond/behinder/utils/jc/PackageInternalsFinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */