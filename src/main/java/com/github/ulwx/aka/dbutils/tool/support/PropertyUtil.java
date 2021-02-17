package com.github.ulwx.aka.dbutils.tool.support;

import com.github.ulwx.aka.dbutils.tool.support.type.TResult2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyUtil {
	private static Logger log = LoggerFactory.getLogger(PropertyUtil.class);

	public static <T1, T2> T1 copyProperties(T1 toBean, T2 fromBean) throws Exception {
		Map<String, Object> map = PropertyUtil.describe(fromBean);
		Set<String> keys = map.keySet();
		for (String key : keys) {
			Object val = map.get(key);
			try {
				PropertyUtil.setProperty(toBean, key, val);
			} catch (Exception e) {
				log.error("", e);
			}
		}

		return toBean;

	}

	public static void setProperty(Object bean, String name, Object value)  {

		Class cls = bean.getClass();
		try {
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("set")) {
					if (method.getParameterTypes().length == 1) {
						if (method.getName().compareToIgnoreCase("set" + name) == 0) {
							method.invoke(bean, value);
						}
					}
				}

			}
		} catch (Exception e) {

		}

	}

	public static Object getProperty(Object bean, String name)  {
		Class cls = bean.getClass();

		try {
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					if (method.getParameterTypes().length == 0) {
						if (method.getName().compareToIgnoreCase("get" + name) == 0) {
							Object val = method.invoke(bean);
							return val;
						}
					}
				} else if (method.getName().startsWith("is")) {
					if (method.getParameterTypes().length == 0 && method.getReturnType() == boolean.class) {
						if (method.getName().compareToIgnoreCase("is" + name) == 0) {
							Object val = method.invoke(bean);
							return val;

						}
					}
				}

			}

		} catch (Exception e) {
			log.error("", e);
		}
		return null;

	}

	public static Class getPropertyType(Object bean, String name) {
		Class cls = bean.getClass();

		try {
			Method[] methods = cls.getMethods();
			for (Method method : methods) {
				if (method.getName().startsWith("get")) {
					if (method.getParameterTypes().length == 0) {
						if (method.getName().compareToIgnoreCase("get" + name) == 0) {
							return method.getReturnType();
						}
					}
				} else if (method.getName().startsWith("is")) {
					if (method.getParameterTypes().length == 0 && method.getReturnType() == boolean.class) {
						if (method.getName().compareToIgnoreCase("is" + name) == 0) {
							return method.getReturnType();

						}
					}
				}

			}

		} catch (Exception e) {
			log.error("", e);
		}
		return null;

	}

	public static void setSimpleProperty(Object bean, String name, Object value) {
		setProperty(bean, name, value);

	}

	/**
	 * 对javaben进行反射，变成一个Map，通过get方法识别属性
	 * 
	 * @param bean
	 *            反射的类
	 * @param t 需要反射的类
	 * @return 属性对应的类型和值的map
	 */
	@SuppressWarnings("rawtypes")
	public static Map<String, TResult2<Class, Object>> describeForTypes(Object bean,Class t) throws Exception {
		Map<String, TResult2<Class, Object>> map = new HashMap<String, TResult2<Class, Object>>();
		
		
		Method[] methods = t.getMethods();
		for (Method method : methods) {
			try {
				method.setAccessible(true);
				String key = "";
				Class returnType = method.getReturnType();
				if (method.getName().startsWith("get")) {
					key = StringUtils.trimLeadingString(method.getName(), "get");
					key = StringUtils.firstCharLowCase(key);
					if (key.equals("class")) {
						continue;
					}
				} else if (returnType == boolean.class && method.getName().startsWith("is")) {
					key = StringUtils.trimLeadingString(method.getName(), "is");
					key = StringUtils.firstCharLowCase(key);
				} else {
					continue;
				}
				Object val=bean.getClass().getMethod(method.getName()).invoke(bean);
				TResult2<Class, Object> tr = new TResult2<Class, Object>(returnType, val);
				map.put(key, tr);
			} catch (Exception e) {
				throw e;
			}
		}
		return map;
	}

	/**
	 * javabean转换成一个map对象
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> describe(Object obj) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		Class cls = obj.getClass();
		Method[] methods = cls.getMethods();
		for (Method method : methods) {
			try {
				String key = "";
				method.setAccessible(true);
				Class returnType = method.getReturnType();
				if (method.getName().startsWith("get")) {
					key = StringUtils.trimLeadingString(method.getName(), "get");
					key = StringUtils.firstCharLowCase(key);
					if (key.equals("class")) {
						continue;
					}
				} else if (returnType == boolean.class && method.getName().startsWith("is")) {
					key = StringUtils.trimLeadingString(method.getName(), "is");
					key = StringUtils.firstCharLowCase(key);
				} else {
					continue;
				}
				Object val = method.invoke(obj);
				map.put(key, val);
			} catch (Exception e) {
				throw e;
			}
		}
		return map;
	}

	public static Map<String, String> loadAsMap(InputStream input, String charsetName) {
		Properties ps = new Properties();
		if(input==null) {
			return null;
		}
		InputStreamReader inr = null;
		try {
			inr = new InputStreamReader(input, charsetName);
			ps.load(inr);
		} catch (Exception e) {
			log.error("", e);
		} finally {
			if (inr != null) {
				try {
					inr.close();
				} catch (Exception e) {

				}
			}
		}
		Map<String, String> value = (Map) ps;// PropertyUtils.loadAsMap(file);
		// System.out.println(ObjectUtils.toString(value));;
		return value;
	}

	
	
	public static void main(String[] args) throws Exception {
		System.out.println(ObjectUtils.toString(loadAsMap(new FileInputStream("e:/xxx.property"))));
		// PropertyUtils.l
	}

	public static Map<String, String> loadAsMap(InputStream input) {
		return loadAsMap(input, "utf-8");
	}
	


	
}
