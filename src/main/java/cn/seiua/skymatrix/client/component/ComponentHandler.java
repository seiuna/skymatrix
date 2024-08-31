package cn.seiua.skymatrix.client.component;

import cn.seiua.skymatrix.client.auth.AuthClient;
import cn.seiua.skymatrix.utils.MethodA;
import cn.seiua.skymatrix.utils.ReflectUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ComponentHandler {

    private static final Logger logger = LogManager.getLogger();
    public static final String MYPACKAGE = "cn.seiua.skymatrix";
    public static final String MYMIXINPACKAGE = "cn.seiua.skymatrix.mixin";
    private static HashMap<Class, Object> classes = new HashMap<>();
    private static HashSet<Object> objects = new HashSet<>();
    private static HashMap<Object, MethodA> init = new HashMap<>();
    private static HashMap<Field, Object> use = new HashMap<>();
    private static HashMap<Method, Object> usem = new HashMap<>();
    private static List<String> clazzs = new ArrayList<>();
    private static AuthClient authClient = new AuthClient();
    public static void loadAllClasesName() {
//         Class ccc=ComponentHandler.class.getClassLoader().getClass();
//        try {
//            Method method=ccc.getMethod("addUrlFwd", URL.class);
//            method.setAccessible(true);
//            method.invoke(ComponentHandler.class.getClassLoader(), new File("D:\\minecraftmod\\skymatrix\\build\\libs\\test.jar").toURI().toURL());
//            Class c=ComponentHandler.class.getClassLoader().loadClass("cn.seiua.skymatrix.test.Hello");
//            System.out.println(c+"           322222222222222222222222");
//            System.out.println(  c.newInstance()+"           322222222222222222222222");
//        } catch (NoSuchMethodException | MalformedURLException | IllegalAccessException | InvocationTargetException |
//                 ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        }
        load();
        String classpath = System.getProperty("java.class.path");
        String[] classpathEntries = classpath.split(File.pathSeparator);
        ArrayList<String> paths = new ArrayList<>(List.of(classpathEntries));
        for (File file : new File(FabricLoader.getInstance().getGameDir().toFile(), "mods").listFiles()) {
            paths.add(file.toString());
        }

        for (String classpathEntry : paths) {
            if (!classpathEntry.contains("skymatrix")) continue;
            File file = new File(classpathEntry);
            if (!file.toString().endsWith(".jar")) {
                String[] files = file.list();
//                System.out.println(file + "  Directory");
                traverseDirectory(file, file);
            } else {
//                System.out.println(file + "  jar");
                traverseJar(file);
            }
        }
    }

    private static void traverseDirectory(File dir, File root) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    traverseDirectory(file, root);
                }
            }
        } else {
            if (dir.toString().endsWith(".class")) {
                addClass(dir.toString().replace(root.toString() + "\\", "").replace("\\", ".").replace(".class", ""));
            }
        }
    }

    private static void addClass(String c) {
        if (c.startsWith(MYPACKAGE)) {
            if (!c.startsWith(MYMIXINPACKAGE)) {
                clazzs.add(c);
            }
        }
    }
    private static void traverseJar(File jarFile) {
        try {
            URL[] urls = {jarFile.toURI().toURL()};
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        JarFile jar = null;
        try {
            jar = new JarFile(jarFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6);
                addClass(className);
            }
        }

        try {
            jar.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void load() {
        File data = authClient.getResource();
        if (data == null) {
            logger.info("Your token is invalid or your username and password is no set!");
            return;
        }

    }

    public static void setup() {
        try {
            HashSet<String> ignore = new HashSet<>();
            C:for (String s : ComponentHandler.clazzs) {
                Class c = ComponentHandler.class.getClassLoader().loadClass(s);
                if (ReflectUtils.withAnnotation(c, Component.class)) {
                    Component component = (Component) c.getAnnotation(Component.class);
                    SModule sModule = (SModule) c.getAnnotation(SModule.class);
                    Pro pro = (Pro) c.getAnnotation(Pro.class);
                    IgnoreDev ignoreDev = (IgnoreDev) c.getAnnotation(IgnoreDev.class);
                    if (ignoreDev != null) {
                        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) {
                            continue;
                        }
                    }
                    if (pro != null) {
                        Class ccc = c.getSuperclass();
                        if (ccc != null) {
                            classes.remove(ccc);
                            ignore.add(ccc.getName());
                        }
                    }
                    if (ignore.contains(c.getName())) {
                        continue;
                    }

                    Object o = null;
                     try {
                         o = c.newInstance();
                     }catch (Exception e){
                        continue C;
                     }
                    classes.put(c, o);
                    logger.debug("Component Loaded " + o.getClass().toString());
                    for (Field f : c.getDeclaredFields()) {
                        Annotation annotation = f.getAnnotation(Use.class);
                        if (annotation != null) {
                            use.put(f, o);
                            logger.debug("Wrote Loaded " + o.getClass().toString() + "." + f.getName());
                        }
                    }
                    //加载初始化方法
                    for (Method m : c.getDeclaredMethods()) {
                        Annotation annotation = m.getAnnotation(Init.class);
                        if (annotation != null) {
                            init.put(o, new MethodA(m, ((Init) annotation).level(), o));
                            logger.debug("Initialization Loaded " + o.getClass().toString() + "." + m.getName());
                        }
                    }
                }
            }
            for (Field o : use.keySet()) {
                Object target = use.get(o);
                Class type = o.getType();
                o.setAccessible(true);
                if (o.getType() == List.class || List.class.isAssignableFrom(o.getType())) {
                    Type typee = o.getGenericType();
                    if (typee instanceof ParameterizedType) {
                        ParameterizedType pType = (ParameterizedType) typee;
                        Type[] arr = pType.getActualTypeArguments();
                        List rt = ReflectUtils.IsCoincidentWith(classes, (Class) arr[0]);
                        o.set(target, rt);
                    }
                } else {
                    o.set(target, classes.get(o.getType()));
                }
                logger.debug("Write " + o.getClass().toString() + "." + o.getName());
            }
            ArrayList<MethodA> arrayList = new ArrayList<>(init.values());
            arrayList.sort(Comparator.comparingInt(MethodA::getLevel));
            for (MethodA o : arrayList) {
                MethodA target = o;
                target.getMethod().setAccessible(true);
                target.getMethod().invoke(target.getObject());
                logger.info("Init " + target.getObject().getClass().toString() + "." + target.getMethod().getName());
            }

        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }


    }


    private static void init() {

    }


}
