package com.wolf.framework.paser;

import com.wolf.framework.config.FrameworkLoggerEnum;
import com.wolf.framework.logger.LogFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.slf4j.Logger;

/**
 *
 * @author aladdin
 */
public final class ClassParser {

    final Logger logger = LogFactory.getLogger(FrameworkLoggerEnum.FRAMEWORK);

    public List<String> findClass(final ClassLoader classloader, final String[] packageNames) {
        final List<String> classNameList = new ArrayList<String>(200);
        Enumeration<URL> eUrl;
        try {
            for (String packageName : packageNames) {
                //获取有效的url
                eUrl = classloader.getResources(this.getPackagePath(packageName));
                if (eUrl != null) {
                    while (eUrl.hasMoreElements()) {
                        //获取class路径
                        this.findClass(classNameList, classloader, eUrl.nextElement(), packageName);
                    }
                } else {
                    this.logger.error("can not find package:".concat(packageName));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException cfe) {
            throw new RuntimeException(cfe);
        }
        return classNameList;
    }

    /**
     * 包名与路径转换
     *
     * @param packageName
     * @return
     */
    private String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    /**
     * 类路径与类名转换
     *
     * @param packagePath
     * @return
     */
    private String getPackageName(String packagePath) {
        return packagePath == null ? null : packagePath.replace('/', '.');
    }

    /**
     * 根据url获取包含的class文件
     *
     * @param url
     * @return
     */
    private void findClass(final List<String> classNameList, final ClassLoader classloader, final URL url, final String packageName) throws IOException, ClassNotFoundException {
        //判断是否是jar包
        String urlName = url.getFile();
        if (urlName.indexOf("/src/test/") == -1 && urlName.indexOf("/src/main/") == -1) {
            int index = urlName.lastIndexOf(".jar");
            if (index > -1) {
                String jarUrlName = urlName.substring(0, index + 4);
                URL jarUrl = new URL(jarUrlName);
                this.findClassInJar(classNameList, classloader, jarUrl);
            } else {
                this.findClassInDirectory(classNameList, classloader, url, packageName);
            }
        }
    }

    /**
     * 获取文件目录中的class
     *
     * @param classloader
     * @param url
     * @param packageName
     * @throws IOException
     */
    private void findClassInDirectory(final List<String> classNameList, final ClassLoader classloader, final URL url, final String packageName) throws IOException, ClassNotFoundException {
        InputStream is = null;
        Enumeration<URL> eUrl;
        String className;
        String childPackageName;
        StringBuilder classBuilder = new StringBuilder();
        StringBuilder childPackageNameBuilder = new StringBuilder();
        try {
            is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            for (String line; (line = reader.readLine()) != null;) {
                if (line.endsWith(".class")) {
                    classBuilder.setLength(0);
                    className = line.substring(0, line.length() - 6);
                    classBuilder.append(packageName).append('.').append(className);
                    className = classBuilder.toString();
                    this.putClassName(classNameList, className);
                } else {
                    childPackageNameBuilder.setLength(0);
                    childPackageNameBuilder.append(packageName).append('.').append(line);
                    childPackageName = childPackageNameBuilder.toString();
                    eUrl = classloader.getResources(this.getPackagePath(childPackageName));
                    if (eUrl != null) {
                        while (eUrl.hasMoreElements()) {
                            //获取class路径
                            this.findClass(classNameList, classloader, eUrl.nextElement(), childPackageName);
                        }
                    }
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void putClassName(final List<String> classNameList, final String className) {
        if (!classNameList.contains(className)) {
            classNameList.add(className);
        }
    }

    /**
     * 获取jar包的class
     *
     * @param jarUrlName
     * @param resourceList
     */
    private void findClassInJar(final List<String> classNameList, final ClassLoader classloader, final URL url) throws IOException, ClassNotFoundException {
        InputStream is = null;
        try {
            is = url.openStream();
            JarInputStream jarInput = new JarInputStream(is);
            String entryName;
            String className;
            String classPath;
            for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null;) {
                if (!entry.isDirectory()) {
                    entryName = entry.getName();
                    if (entryName.endsWith(".class")) {
                        classPath = entryName.substring(0, entryName.length() - 6);
                        className = this.getPackageName(classPath);
                        this.putClassName(classNameList, className);
                    }
                }
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
