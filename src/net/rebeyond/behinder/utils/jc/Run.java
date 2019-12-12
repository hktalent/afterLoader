package net.rebeyond.behinder.utils.jc;

import javax.tools.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Run {
    public static void main(String[] args) {
        new Run().test();
    }

    public void test() {
        String sourceCode = "import javax.servlet.jsp.PageContext;import javax.servlet.ServletOutputStream;public class test{public boolean equals(Object obj){PageContext page = (PageContext) obj;try {ServletOutputStream so=page.getResponse().getOutputStream();so.write(\"afsddf\".getBytes(\"UTF-8\"));so.flush();so.close();page.getOut().clear();} catch (Exception e) {e.printStackTrace();}return true;}}";
        try {
            for (; ; ) {
                Thread.sleep(2000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] getClassFromSourceCode(String sourceCode)
            throws Exception {
        byte[] classBytes = null;
        Pattern CLASS_PATTERN = Pattern.compile("class\\s+([$_a-zA-Z][$_a-zA-Z0-9]*)\\s*");
        Matcher matcher = CLASS_PATTERN.matcher(sourceCode);
        String cls;
        if (matcher.find()) {
            cls = matcher.group(1);
        } else {
            throw new IllegalArgumentException("No such class name in " + sourceCode);
        }
        JavaCompiler jc = ToolProvider.getSystemJavaCompiler();
        if (jc == null) {
            throw new Exception("本地机器上没有找到编译环境，请确认:1.是否安装了JDK环境;2." + System.getProperty("java.home") + File.separator + "lib目录下是否有tools.jar.");
        }
        StandardJavaFileManager standardJavaFileManager = jc.getStandardFileManager(null, null, null);
        JavaFileManager fileManager = new CustomClassloaderJavaFileManager(Run.class.getClassLoader(), standardJavaFileManager);
        JavaFileObject javaFileObject = new MyJavaFileObject(cls, sourceCode);

        List<String> options = new ArrayList();
        options.add("-source");
        options.add("1.6");
        options.add("-target");
        options.add("1.6");
        DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector();
        JavaCompiler.CompilationTask cTask = jc.getTask(null, fileManager, collector, options, null, Arrays.asList(new JavaFileObject[]{javaFileObject}));
        boolean result = cTask.call().booleanValue();
        if (!result) {
            List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();
            Iterator localIterator = diagnostics.iterator();
            if (localIterator.hasNext()) {
                Diagnostic<? extends JavaFileObject> diagnostic = (Diagnostic) localIterator.next();

                throw new Exception(diagnostic.getMessage(null));
            }
        }
        JavaFileObject fileObject = (JavaFileObject) CustomClassloaderJavaFileManager.fileObjects.get(cls);
        if (fileObject != null) {
            classBytes = ((MyJavaFileObject) fileObject).getCompiledBytes();
        }


        return classBytes;
    }
}
