package com.github.ulwx.aka.dbutils.tool.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassUtils {

	private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

	public static String getAllClassLoaderPathsStr(Class clazz) {
		String classpath = null;
		URLClassLoader parentClassLoader = (URLClassLoader)clazz.getClassLoader();
		StringBuilder sb = new StringBuilder();
		for (URL url : parentClassLoader.getURLs()) {
			String p = url.getFile();
			sb.append(p).append(File.pathSeparator);
		}
		return sb.toString();
	}
	public static List<String> getAllClassLoaderPaths(Class clazz) {
		URLClassLoader parentClassLoader = (URLClassLoader)clazz.getClassLoader();
		List<String> list=new ArrayList<>();
		for (URL url : parentClassLoader.getURLs()) {
			String p = url.getFile();
			list.add(p);
		}
		return list;
	}
	
	/**
	 * 得到指定目录下的当前所有class的名称数组
	 * 
	 * @param file
	 * @return
	 */
	public static String[] getClassName(File file) {
		if (!file.exists() || !file.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return new String[0];
		} else {
			File[] classfiles = file.listFiles(new FileFilter() {
				// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
				public boolean accept(File file) {

					return (file.isFile()) && (file.getName().endsWith(".class"));
				}
			});
			List<String> list = new ArrayList<>();
			if (classfiles != null) {
				for (File f : classfiles) {
					list.add(StringUtils.trimTailString(f.getName(), ".class"));
				}
				return list.toArray(new String[0]);
			}

		}

		return new String[0];

	}

	/**
	 * 得到当前包名下，当前以prefix开头的类文件数组
	 * 
	 * @param pack
	 * @param prefix
	 * @return
	 */
	public static File[] getDirectChildPackages(String pack, String prefix) {
		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		File[] ret = new File[0];
		Enumeration<URL> dirs;
		try {


			// 获取下一个元素
			URL url = Thread.currentThread().getContextClassLoader().getResource(packageDirName);
			if ("file".equals(url.getProtocol())) {
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				File dir = new File(filePath);
				// 如果不存在或者 也不是目录就直接返回
				if (!dir.exists() || !dir.isDirectory()) {
					// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
					return ret;
				}
				// 如果存在 就获取包下的所有文件 包括目录
				File[] dirfiles = dir.listFiles(new FileFilter() {
					// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
					public boolean accept(File file) {

						return (file.isDirectory()) && (file.getName().startsWith(prefix));
					}
				});

				return dirfiles;
			}

		
		} catch (IOException e) {
			log.error("", e);
		}

		return null;

	}

	/**
	 * 从包pack中获取所有的Class的名称，可递归查询
	 * 
	 * @param pack
	 * @return
	 */
	public static Set<String> getClasses(String pack, boolean recursive) {

		// 第一个class类的集合
		Set<String> classes = new LinkedHashSet<String>();
		// 是否循环迭代

		// 获取包的名字 并进行替换
		String packageName = pack;
		String packageDirName = packageName.replace('.', '/');
		// 定义一个枚举的集合 并进行循环来处理这个目录下的things
		// log.debug("========333="+packageDirName);
		try {

			// 获取下一个元素
			URL url = Thread.currentThread().getContextClassLoader().getResource(packageDirName);
			// log.debug("========222"+url.toString());
			// 得到协议的名称
			String protocol = url.getProtocol();
			// 如果是以文件的形式保存在服务器上
			if ("file".equals(protocol)) {
				log.error("file类型的扫描");
				// 获取包的物理路径
				String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
				// 以文件的方式扫描整个包下的文件 并添加到集合中
				findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes);
			} else if ("jar".equals(protocol)) {
				// 如果是jar包文件
				// 定义一个JarFile
				log.error("jar类型的扫描");
				JarFile jar;
				try {
					// 获取jar
					jar = ((JarURLConnection) url.openConnection()).getJarFile();
					// 从此jar包 得到一个枚举类
					Enumeration<JarEntry> entries = jar.entries();
					// 同样的进行循环迭代
					while (entries.hasMoreElements()) {
						// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						// 如果是以/开头的
						if (name.charAt(0) == '/') {
							// 获取后面的字符串
							name = name.substring(1);
						}
						// 如果前半部分和定义的包名相同
						if (name.startsWith(packageDirName)) {
							int idx = name.lastIndexOf('/');
							// 如果以"/"结尾 是一个包
							if (idx != -1) {
								// 获取包名 把"/"替换成"."
								packageName = name.substring(0, idx).replace('/', '.');
							}
							// 如果可以迭代下去 并且是一个包
							if ((idx != -1) || recursive) {
								// 如果是一个.class文件 而且不是目录
								if (name.endsWith(".class") && !entry.isDirectory()) {
									// 去掉后面的".class" 获取真正的类名
									String className = name.substring(packageName.length() + 1, name.length() - 6);
									try {
										// 添加到classes
										classes.add(packageName + '.' + className);
									} catch (Exception e) {
										// log
										// .error("添加用户自定义视图类错误
										// 找不到此类的.class文件");
										log.error("", e);
									}
								}
							}
						}
					}
				} catch (IOException e) {
					// log.error("在扫描用户定义视图时从jar包获取文件出错");
					log.error("", e);
				}
			}
		
		} catch (IOException e) {
			log.error("", e);
		}

		return classes;
	}

	/**
	 * 获取包下的所有Class的名称，可以选择递归或非递归方式
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param classes
	 */
	public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive,
			Set<String> classes) {
		// 获取此包的目录 建立一个File
		File dir = new File(packagePath);
		// 如果不存在或者 也不是目录就直接返回
		if (!dir.exists() || !dir.isDirectory()) {
			// log.warn("用户定义包名 " + packageName + " 下没有任何文件");
			return;
		}
		// 如果存在 就获取包下的所有文件 包括目录
		File[] dirfiles = dir.listFiles(new FileFilter() {
			// 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
			public boolean accept(File file) {
				return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));
			}
		});
		// 循环所有文件
		for (File file : dirfiles) {
			// 如果是目录 则继续扫描
			if (file.isDirectory()) {
				findAndAddClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,
						classes);
			} else {
				// 如果是java类文件 去掉后面的.class 只留下类名
				String className = file.getName().substring(0, file.getName().length() - 6);
				try {
					// 添加到集合中去
					classes.add(packageName + '.' + className);
				} catch (Exception e) {
					// log.error("添加用户自定义视图类错误 找不到此类的.class文件");
					log.error("", e);
				}
			}
		}

	}

	
	/**
	 * 查询所有包下的给定后缀的文件名称，可以选择递归或非递归方式
	 * 
	 * @param packageName
	 * @param packagePath
	 * @param recursive
	 * @param fileNames 输出的文件名
	 * @param suffix 后缀，例如 .class
	 */
	public static void findAndAddInPackageByFile(String packagePath, final boolean recursive,
			List<String> fileNames,String suffix) {
		 Path.findAndAddInPackageByFile(packagePath, recursive, fileNames, suffix);
	}

	public static void main(String[] args) throws Exception {
		/*
		 * Set<String> clzz = getClasses("org.apache.juli", false); for (String
		 * c : clzz) { log.info(c); }
		 */

		System.out.println(ObjectUtils.toString(getDirectChildPackages("com.github.ulwx.tool", "ex")));
		File[] fs = getDirectChildPackages("com.github.ulwx.tool", "ex");
		System.out.println(ObjectUtils.toString(getClassName(fs[0])));
		System.out.println(getAllClassLoaderPathsStr(ClassUtils.class));

	}

	/**
	 * 获取参数化类型里的可实例化的类型
	 * 
	 * @param type
	 * @return
	 */
	public static Class getActualTypeForAvailInstance(Type type) {
		if (type instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			// 类型变量 T
			if (typeArguments[0] instanceof TypeVariable) {
				return null;
			}
			// 通配符表达式 ?
			else if (typeArguments[0] instanceof WildcardType) {
				return null;
			}
			// 泛型的实际类型，即实际存在的类型
			else if (typeArguments[0] instanceof Class) {
				return (Class) typeArguments[0];
			}
		}
		return null;
	}

	public static Class getActualType(Type type) {
		if (type instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			// 类型变量 T
			if (typeArguments[0] instanceof TypeVariable) {
				return null;
			}
			// 通配符表达式 ?
			else if (typeArguments[0] instanceof WildcardType) {
				return null;
			}
			// 泛型的实际类型，即实际存在的类型
			else if (typeArguments[0] instanceof Class) {
				return (Class) typeArguments[0];
			}
		}
		return null;

	}
}

/**
 * 下面的类为用于泛型获取类型的测试类
 * 
 * @author yong
 *
 * @param <T>
 */

class TypeAA<T extends Object> {
	public TypeArguments<Integer, Integer> a;
}

class AA<T>{
	public T obj;
}

class BB extends AA<Integer>{
	
}

class TypeArguments<T extends Integer, U extends Integer> {

	public T t;

	public TypeArguments() {
	}

	public <E> TypeArguments(E e) {
	}

	public Map<T, String> genericField;

	public List listField;

	public <B> Map<Integer, String>[] genericMethod(List<? extends Integer> list, List<String> list2, String str,
			B[] tArr) throws IOException, NoSuchMethodException {
		return null;
	}

	public static void main(String[] args) throws Exception {
		
		Field field2 = BB.class.getField("obj");
		Type tt = field2.getGenericType();
		instanceActualTypeArguments(tt);
		System.out.println();
		
		
		Class<TypeArguments> clazz = TypeArguments.class;

		System.out.println("0．  类TypeArguments。");
		TypeVariable<Class<TypeArguments>>[] ss = clazz.getTypeParameters();

		instanceActualTypeArguments(ss[0]);
		System.out.println();

		System.out.println("一．  成员变量类型的泛型参数");
		Field field = clazz.getField("genericField");
		Type fieldGenericType = field.getGenericType();
		instanceActualTypeArguments(fieldGenericType);
		System.out.println();

		System.out.println("二．  成员方法返回值的泛型参数。");
		Method method = clazz.getMethod("genericMethod",
				new Class<?>[] { List.class, List.class, String.class, Object[].class });
		Type genericReturnType = method.getGenericReturnType();
		instanceActualTypeArguments(genericReturnType);
		System.out.println();

		System.out.println("三．  成员方法参数类型的泛型参数。");
		Type[] genericParameterTypes = method.getGenericParameterTypes();
		for (int i = 0; i < genericParameterTypes.length; i++) {
			System.out.println("该方法的第" + (i + 1) + "个参数：");
			instanceActualTypeArguments(genericParameterTypes[i]);
		}
		System.out.println();

		System.out.println("三．  构造方法参数类型的泛型参数。");
		Constructor<?> constructor = clazz.getConstructor(new Class<?>[] { Object.class });
		Type[] constructorParameterTypes = constructor.getGenericParameterTypes();
		for (int i = 0; i < constructorParameterTypes.length; i++) {
			System.out.println("该构造方法的第" + (i + 1) + "个参数：");
			instanceActualTypeArguments(constructorParameterTypes[i]);
		}
		System.out.println();

		System.out.println("4．  成员变量类型的泛型参数(listField)。");
		field = clazz.getField("listField");
		fieldGenericType = field.getGenericType();
		instanceActualTypeArguments(fieldGenericType);
		System.out.println();
	}

	/**
	 * 实例化泛型的实际类型参数
	 * 
	 * @param type
	 * @throws Exception
	 */
	private static void instanceActualTypeArguments(Type type) throws Exception {
		System.out.println("该类型是" + type);
		// 参数化类型
		if (type instanceof ParameterizedType) {
			Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			for (int i = 0; i < typeArguments.length; i++) {
				// 类型变量
				if (typeArguments[i] instanceof TypeVariable) {
					System.out.println("第" + (i + 1) + "个泛型参数类型是类型变量" + typeArguments[i] + "，无法实例化。");
				}
				// 通配符表达式
				else if (typeArguments[i] instanceof WildcardType) {
					System.out.println("第" + (i + 1) + "个泛型参数类型是通配符表达式" + typeArguments[i] + "，无法实例化。");
				}
				// 泛型的实际类型，即实际存在的类型
				else if (typeArguments[i] instanceof Class) {
					System.out.println("第" + (i + 1) + "个泛型参数类型是:" + typeArguments[i] + "，可以直接实例化对象");
				}
			}
			// 参数化类型数组或类型变量数组
		} else if (type instanceof GenericArrayType) {
			System.out.println("该泛型类型是参数化类型数组或类型变量数组，可以获取其原始类型。");
			Type componentType = ((GenericArrayType) type).getGenericComponentType();
			// 类型变量
			if (componentType instanceof TypeVariable) {
				System.out.println("该类型变量数组的原始类型是类型变量" + componentType + "，无法实例化。");
			}
			// 参数化类型，参数化类型数组或类型变量数组
			// 参数化类型数组或类型变量数组也可以是多维的数组，getGenericComponentType()方法仅仅是去掉最右边的[]
			else {
				// 递归调用方法自身
				instanceActualTypeArguments(componentType);
			}
		} else if (type instanceof TypeVariable) {
			System.out.println("该类型是类型变量");
		} else if (type instanceof WildcardType) {
			System.out.println("该类型是通配符表达式");
		} else if (type instanceof Class) {
			System.out.println("该类型不是泛型类型");
		} else {
			throw new Exception();
		}
	}
}