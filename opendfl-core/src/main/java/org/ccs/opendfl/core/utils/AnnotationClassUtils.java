package org.ccs.opendfl.core.utils;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 注解类处理
 */
@Slf4j
public class AnnotationClassUtils {
    private AnnotationClassUtils() {

    }

    /**
     * 增加扫描到的class类
     *
     * @param packageName 包名
     * @param packagePath 包地址
     * @param recursive   是否递归
     * @param classes     类信息
     * @author chenjh
     */
    public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes) {
        //获取此包的目录 建立一个File
        File dir = new File(packagePath);
        //如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        //如果存在 就获取包下的所有文件 包括目录
        File[] dirFiles = dir.listFiles(new FileFilter() {
            //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            @Override
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
            }
        });
        //循环所有文件
        for (File file : dirFiles) {
            //如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes);
            } else {
                //如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    //添加到集合中去
                    classes.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                } catch (ClassNotFoundException e) {
                    log.error("-----getClassFromJar--className={} error={}", packageName + "." + className, e.getMessage(), e);
                }
            }
        }
    }

    private static final String CLASSES_PATH_STR = "/classes/";

    /**
     * 从jar包中扫码注解类
     *
     * @param jarEntries  jar包
     * @param packageName 包名
     * @param isRecursion 是否递归
     * @return 找到的类型
     */
    private static List<Class<?>> getClassFromJar(Enumeration<JarEntry> jarEntries, String packageName, boolean isRecursion) {
        List<Class<?>> classList = new ArrayList<>();
        final String CLASS_STR = ".class";
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            if (!jarEntry.isDirectory()) {
                /*
                 * 这里是为了方便，先把"/" 转成 "." 再判断 ".class" 的做法可能会有bug
                 */
                String entryName = jarEntry.getName();
                if (!entryName.endsWith(CLASS_STR)) {
                    continue;
                }
                if (entryName.contains(CLASSES_PATH_STR)) {
                    entryName = entryName.substring(entryName.indexOf(CLASSES_PATH_STR) + CLASSES_PATH_STR.length());
                }
                entryName = entryName.replace("/", ".");
                if (!entryName.contains("$") && entryName.startsWith(packageName)) {
                    entryName = entryName.replace(CLASS_STR, "");
                    if (isRecursion || !entryName.replace(packageName + ".", "").contains(".")) {
                        String className = entryName;
                        try {
                            //添加到集合中去
                            classList.add(Thread.currentThread().getContextClassLoader().loadClass(className));
                        } catch (ClassNotFoundException e) {
                            log.error("-----getClassFromJar--className={} error={}", className, e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return classList;
    }


    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls        URL集合
     * @param packageName 包路径
     * @param isRecursion 是否遍历子包
     * @return 类的完整名称
     */
    public static List<Class<?>> getClassFromJars(URL[] urls, String packageName, boolean isRecursion) {
        List<Class<?>> classList = new ArrayList<>();
        List<String> checkPathList = new ArrayList<>();
        URL url;
        for (int i = 0; i < urls.length; i++) {
            url = urls[i];
            String classPath = url.getPath();
            //不必搜索classes文件夹
            if (classPath.endsWith(CLASSES_PATH_STR)) {
                continue;
            }
            if (classPath.startsWith("file:")) {
                classPath = classPath.substring("file:".length());
            }
            classList.addAll(getClassFromWarJar(classPath, packageName, checkPathList, isRecursion));
        }
        return classList;
    }

    /**
     * 支持从war中读jar的class: xxx/opendfl-mysql-1.0-SNAPSHOT.war!/WEB-INF/lib/xxx.jar
     *
     * @param jarPath       jqr包路径
     * @param pkgName       包名
     * @param checkPathList 检查path
     * @param isRecursion   是否递归
     * @return 类型信息
     * @author chenjh
     */
    public static List<Class<?>> getClassFromWarJar(String jarPath, String pkgName, List<String> checkPathList, boolean isRecursion) {
        List<Class<?>> classList = new ArrayList<>();
        int splitIndex = jarPath.indexOf("!/");
        if (splitIndex > 0 && jarPath.contains(".jar")) {
            String jarPath1 = jarPath.substring(0, splitIndex);
            String jarPath2 = jarPath.substring(splitIndex + 2);
            try (JarFile jarFile1 = new JarFile(jarPath1);) {
                File file = new File(jarPath1);
                log.debug("----getClassFromWarJar--jarPath1={} fileExist={}", jarPath1, file.exists());

                //避免重复读取war的class
                if (!checkPathList.contains(jarPath1)) {
                    checkPathList.add(jarPath1);
                    log.debug("-----getClassFromWarJar--jarPath1={}", jarPath1);
                    List<Class<?>> tmpList = getClassFromJar(jarFile1.entries(), pkgName, isRecursion);
                    classList.addAll(tmpList);
                }
                splitIndex = jarPath2.indexOf("!/");
                if (isRecursion && splitIndex > 0) {
                    String jarPath21 = jarPath2.substring(0, splitIndex);
                    log.debug("-----getClassFromWarJar--jarPath21={}", jarPath21);
                    /**
                     * 支持从war中读取jar的class列表
                     */
                    if (jarPath21.endsWith(".jar")) {
                        JarEntry jarFile2 = jarFile1.getJarEntry(jarPath21);
                        InputStream is = jarFile1.getInputStream(jarFile2);
                        File tmpFile = asFile(is, "tmp");
                        JarFile jarFile2Jar = new JarFile(tmpFile);

                        List<Class<?>> tmpList = getClassFromJar(jarFile2Jar.entries(), pkgName, true);
                        jarFile2Jar.close();
                        tmpFile.deleteOnExit();
                        classList.addAll(tmpList);
                    }
                }

            } catch (IOException e) {
                log.error("-----getClassFromWarJar--jarPath={} error={}", jarPath, e.getMessage());
            }
        }
        return classList;
    }

    public static File asFile(InputStream inputStream, String fileName) throws IOException {
        File tmp = File.createTempFile(fileName, ".jar");
        OutputStream os = new FileOutputStream(tmp);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        IoUtil.close(os);
        IoUtil.close(inputStream);
        return tmp;
    }
}
