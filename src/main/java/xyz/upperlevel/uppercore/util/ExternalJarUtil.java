package xyz.upperlevel.uppercore.util;

import java.io.File;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public final class ExternalJarUtil {
    private static final MethodHandle addPath;

    static {//void addURL(URL var1)

        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            MethodHandle tmp = MethodHandles.lookup().unreflect(method);
            addPath = tmp.bindTo(classLoader);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Unsupported class loader: " + classLoader.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Cannot get addUrl method", e);
        }
    }


    public static void addUrl(URL url) {
        try {
            addPath.invoke(url);
        } catch (Throwable t) {
            throw new RuntimeException("Error while loading external jar", t);
        }
    }

    public static void addUrl(File file) throws MalformedURLException {
        addUrl(file.toURI().toURL());
    }


    private ExternalJarUtil() {
    }
}
